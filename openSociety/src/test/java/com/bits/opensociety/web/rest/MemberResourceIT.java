package com.bits.opensociety.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.bits.opensociety.IntegrationTest;
import com.bits.opensociety.domain.Flat;
import com.bits.opensociety.domain.Member;
import com.bits.opensociety.domain.User;
import com.bits.opensociety.domain.enumeration.MemberType;
import com.bits.opensociety.repository.EntityManager;
import com.bits.opensociety.repository.MemberRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link MemberResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MemberResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MOBILE = "AAAAAAAAAA";
    private static final String UPDATED_MOBILE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "$r7;AF@l+w6(^.0U71B!";
    private static final String UPDATED_EMAIL = "x%a?x@En.zrW.1>";

    private static final MemberType DEFAULT_MEMBER_TYPE = MemberType.TENANT;
    private static final MemberType UPDATED_MEMBER_TYPE = MemberType.OWNER;

    private static final String ENTITY_API_URL = "/api/members";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MemberRepository memberRepository;

    @Mock
    private MemberRepository memberRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Member member;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Member createEntity(EntityManager em) {
        Member member = new Member().name(DEFAULT_NAME).mobile(DEFAULT_MOBILE).email(DEFAULT_EMAIL).memberType(DEFAULT_MEMBER_TYPE);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        member.setUser(user);
        // Add required entity
        Flat flat;
        flat = em.insert(FlatResourceIT.createEntity(em)).block();
        member.setFlat(flat);
        return member;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Member createUpdatedEntity(EntityManager em) {
        Member member = new Member().name(UPDATED_NAME).mobile(UPDATED_MOBILE).email(UPDATED_EMAIL).memberType(UPDATED_MEMBER_TYPE);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        member.setUser(user);
        // Add required entity
        Flat flat;
        flat = em.insert(FlatResourceIT.createUpdatedEntity(em)).block();
        member.setFlat(flat);
        return member;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Member.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        UserResourceIT.deleteEntities(em);
        FlatResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        member = createEntity(em);
    }

    @Test
    void createMember() throws Exception {
        int databaseSizeBeforeCreate = memberRepository.findAll().collectList().block().size();
        // Create the Member
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(member))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeCreate + 1);
        Member testMember = memberList.get(memberList.size() - 1);
        assertThat(testMember.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMember.getMobile()).isEqualTo(DEFAULT_MOBILE);
        assertThat(testMember.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testMember.getMemberType()).isEqualTo(DEFAULT_MEMBER_TYPE);
    }

    @Test
    void createMemberWithExistingId() throws Exception {
        // Create the Member with an existing ID
        member.setId(1L);

        int databaseSizeBeforeCreate = memberRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(member))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = memberRepository.findAll().collectList().block().size();
        // set the field null
        member.setName(null);

        // Create the Member, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(member))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkMobileIsRequired() throws Exception {
        int databaseSizeBeforeTest = memberRepository.findAll().collectList().block().size();
        // set the field null
        member.setMobile(null);

        // Create the Member, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(member))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = memberRepository.findAll().collectList().block().size();
        // set the field null
        member.setEmail(null);

        // Create the Member, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(member))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkMemberTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = memberRepository.findAll().collectList().block().size();
        // set the field null
        member.setMemberType(null);

        // Create the Member, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(member))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllMembersAsStream() {
        // Initialize the database
        memberRepository.save(member).block();

        List<Member> memberList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Member.class)
            .getResponseBody()
            .filter(member::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(memberList).isNotNull();
        assertThat(memberList).hasSize(1);
        Member testMember = memberList.get(0);
        assertThat(testMember.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMember.getMobile()).isEqualTo(DEFAULT_MOBILE);
        assertThat(testMember.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testMember.getMemberType()).isEqualTo(DEFAULT_MEMBER_TYPE);
    }

    @Test
    void getAllMembers() {
        // Initialize the database
        memberRepository.save(member).block();

        // Get all the memberList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(member.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].mobile")
            .value(hasItem(DEFAULT_MOBILE))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].memberType")
            .value(hasItem(DEFAULT_MEMBER_TYPE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMembersWithEagerRelationshipsIsEnabled() {
        when(memberRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(memberRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMembersWithEagerRelationshipsIsNotEnabled() {
        when(memberRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(memberRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMember() {
        // Initialize the database
        memberRepository.save(member).block();

        // Get the member
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, member.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(member.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.mobile")
            .value(is(DEFAULT_MOBILE))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.memberType")
            .value(is(DEFAULT_MEMBER_TYPE.toString()));
    }

    @Test
    void getNonExistingMember() {
        // Get the member
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewMember() throws Exception {
        // Initialize the database
        memberRepository.save(member).block();

        int databaseSizeBeforeUpdate = memberRepository.findAll().collectList().block().size();

        // Update the member
        Member updatedMember = memberRepository.findById(member.getId()).block();
        updatedMember.name(UPDATED_NAME).mobile(UPDATED_MOBILE).email(UPDATED_EMAIL).memberType(UPDATED_MEMBER_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedMember.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedMember))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
        Member testMember = memberList.get(memberList.size() - 1);
        assertThat(testMember.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMember.getMobile()).isEqualTo(UPDATED_MOBILE);
        assertThat(testMember.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testMember.getMemberType()).isEqualTo(UPDATED_MEMBER_TYPE);
    }

    @Test
    void putNonExistingMember() throws Exception {
        int databaseSizeBeforeUpdate = memberRepository.findAll().collectList().block().size();
        member.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, member.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(member))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMember() throws Exception {
        int databaseSizeBeforeUpdate = memberRepository.findAll().collectList().block().size();
        member.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(member))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMember() throws Exception {
        int databaseSizeBeforeUpdate = memberRepository.findAll().collectList().block().size();
        member.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(member))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMemberWithPatch() throws Exception {
        // Initialize the database
        memberRepository.save(member).block();

        int databaseSizeBeforeUpdate = memberRepository.findAll().collectList().block().size();

        // Update the member using partial update
        Member partialUpdatedMember = new Member();
        partialUpdatedMember.setId(member.getId());

        partialUpdatedMember.name(UPDATED_NAME).mobile(UPDATED_MOBILE).email(UPDATED_EMAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMember.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMember))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
        Member testMember = memberList.get(memberList.size() - 1);
        assertThat(testMember.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMember.getMobile()).isEqualTo(UPDATED_MOBILE);
        assertThat(testMember.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testMember.getMemberType()).isEqualTo(DEFAULT_MEMBER_TYPE);
    }

    @Test
    void fullUpdateMemberWithPatch() throws Exception {
        // Initialize the database
        memberRepository.save(member).block();

        int databaseSizeBeforeUpdate = memberRepository.findAll().collectList().block().size();

        // Update the member using partial update
        Member partialUpdatedMember = new Member();
        partialUpdatedMember.setId(member.getId());

        partialUpdatedMember.name(UPDATED_NAME).mobile(UPDATED_MOBILE).email(UPDATED_EMAIL).memberType(UPDATED_MEMBER_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMember.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMember))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
        Member testMember = memberList.get(memberList.size() - 1);
        assertThat(testMember.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMember.getMobile()).isEqualTo(UPDATED_MOBILE);
        assertThat(testMember.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testMember.getMemberType()).isEqualTo(UPDATED_MEMBER_TYPE);
    }

    @Test
    void patchNonExistingMember() throws Exception {
        int databaseSizeBeforeUpdate = memberRepository.findAll().collectList().block().size();
        member.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, member.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(member))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMember() throws Exception {
        int databaseSizeBeforeUpdate = memberRepository.findAll().collectList().block().size();
        member.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(member))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMember() throws Exception {
        int databaseSizeBeforeUpdate = memberRepository.findAll().collectList().block().size();
        member.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(member))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMember() {
        // Initialize the database
        memberRepository.save(member).block();

        int databaseSizeBeforeDelete = memberRepository.findAll().collectList().block().size();

        // Delete the member
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, member.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Member> memberList = memberRepository.findAll().collectList().block();
        assertThat(memberList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

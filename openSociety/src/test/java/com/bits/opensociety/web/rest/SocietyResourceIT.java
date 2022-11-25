package com.bits.opensociety.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.bits.opensociety.IntegrationTest;
import com.bits.opensociety.domain.Society;
import com.bits.opensociety.repository.EntityManager;
import com.bits.opensociety.repository.SocietyRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link SocietyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SocietyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/societies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SocietyRepository societyRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Society society;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Society createEntity(EntityManager em) {
        Society society = new Society().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return society;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Society createUpdatedEntity(EntityManager em) {
        Society society = new Society().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return society;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Society.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        society = createEntity(em);
    }

    @Test
    void createSociety() throws Exception {
        int databaseSizeBeforeCreate = societyRepository.findAll().collectList().block().size();
        // Create the Society
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(society))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Society in the database
        List<Society> societyList = societyRepository.findAll().collectList().block();
        assertThat(societyList).hasSize(databaseSizeBeforeCreate + 1);
        Society testSociety = societyList.get(societyList.size() - 1);
        assertThat(testSociety.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSociety.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createSocietyWithExistingId() throws Exception {
        // Create the Society with an existing ID
        society.setId(1L);

        int databaseSizeBeforeCreate = societyRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(society))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Society in the database
        List<Society> societyList = societyRepository.findAll().collectList().block();
        assertThat(societyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = societyRepository.findAll().collectList().block().size();
        // set the field null
        society.setName(null);

        // Create the Society, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(society))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Society> societyList = societyRepository.findAll().collectList().block();
        assertThat(societyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllSocietiesAsStream() {
        // Initialize the database
        societyRepository.save(society).block();

        List<Society> societyList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Society.class)
            .getResponseBody()
            .filter(society::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(societyList).isNotNull();
        assertThat(societyList).hasSize(1);
        Society testSociety = societyList.get(0);
        assertThat(testSociety.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSociety.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllSocieties() {
        // Initialize the database
        societyRepository.save(society).block();

        // Get all the societyList
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
            .value(hasItem(society.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getSociety() {
        // Initialize the database
        societyRepository.save(society).block();

        // Get the society
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, society.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(society.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingSociety() {
        // Get the society
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewSociety() throws Exception {
        // Initialize the database
        societyRepository.save(society).block();

        int databaseSizeBeforeUpdate = societyRepository.findAll().collectList().block().size();

        // Update the society
        Society updatedSociety = societyRepository.findById(society.getId()).block();
        updatedSociety.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSociety.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedSociety))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Society in the database
        List<Society> societyList = societyRepository.findAll().collectList().block();
        assertThat(societyList).hasSize(databaseSizeBeforeUpdate);
        Society testSociety = societyList.get(societyList.size() - 1);
        assertThat(testSociety.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSociety.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void putNonExistingSociety() throws Exception {
        int databaseSizeBeforeUpdate = societyRepository.findAll().collectList().block().size();
        society.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, society.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(society))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Society in the database
        List<Society> societyList = societyRepository.findAll().collectList().block();
        assertThat(societyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSociety() throws Exception {
        int databaseSizeBeforeUpdate = societyRepository.findAll().collectList().block().size();
        society.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(society))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Society in the database
        List<Society> societyList = societyRepository.findAll().collectList().block();
        assertThat(societyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSociety() throws Exception {
        int databaseSizeBeforeUpdate = societyRepository.findAll().collectList().block().size();
        society.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(society))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Society in the database
        List<Society> societyList = societyRepository.findAll().collectList().block();
        assertThat(societyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSocietyWithPatch() throws Exception {
        // Initialize the database
        societyRepository.save(society).block();

        int databaseSizeBeforeUpdate = societyRepository.findAll().collectList().block().size();

        // Update the society using partial update
        Society partialUpdatedSociety = new Society();
        partialUpdatedSociety.setId(society.getId());

        partialUpdatedSociety.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSociety.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSociety))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Society in the database
        List<Society> societyList = societyRepository.findAll().collectList().block();
        assertThat(societyList).hasSize(databaseSizeBeforeUpdate);
        Society testSociety = societyList.get(societyList.size() - 1);
        assertThat(testSociety.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSociety.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void fullUpdateSocietyWithPatch() throws Exception {
        // Initialize the database
        societyRepository.save(society).block();

        int databaseSizeBeforeUpdate = societyRepository.findAll().collectList().block().size();

        // Update the society using partial update
        Society partialUpdatedSociety = new Society();
        partialUpdatedSociety.setId(society.getId());

        partialUpdatedSociety.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSociety.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSociety))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Society in the database
        List<Society> societyList = societyRepository.findAll().collectList().block();
        assertThat(societyList).hasSize(databaseSizeBeforeUpdate);
        Society testSociety = societyList.get(societyList.size() - 1);
        assertThat(testSociety.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSociety.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingSociety() throws Exception {
        int databaseSizeBeforeUpdate = societyRepository.findAll().collectList().block().size();
        society.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, society.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(society))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Society in the database
        List<Society> societyList = societyRepository.findAll().collectList().block();
        assertThat(societyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSociety() throws Exception {
        int databaseSizeBeforeUpdate = societyRepository.findAll().collectList().block().size();
        society.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(society))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Society in the database
        List<Society> societyList = societyRepository.findAll().collectList().block();
        assertThat(societyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSociety() throws Exception {
        int databaseSizeBeforeUpdate = societyRepository.findAll().collectList().block().size();
        society.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(society))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Society in the database
        List<Society> societyList = societyRepository.findAll().collectList().block();
        assertThat(societyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSociety() {
        // Initialize the database
        societyRepository.save(society).block();

        int databaseSizeBeforeDelete = societyRepository.findAll().collectList().block().size();

        // Delete the society
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, society.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Society> societyList = societyRepository.findAll().collectList().block();
        assertThat(societyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

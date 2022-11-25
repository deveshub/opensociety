package com.bits.opensociety.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.bits.opensociety.IntegrationTest;
import com.bits.opensociety.domain.Flat;
import com.bits.opensociety.domain.enumeration.ResidentialStatus;
import com.bits.opensociety.repository.EntityManager;
import com.bits.opensociety.repository.FlatRepository;
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
 * Integration tests for the {@link FlatResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class FlatResourceIT {

    private static final String DEFAULT_FLAT_NO = "AAAAAAAAAA";
    private static final String UPDATED_FLAT_NO = "BBBBBBBBBB";

    private static final ResidentialStatus DEFAULT_RESIDENTIAL_STATUS = ResidentialStatus.OWNED;
    private static final ResidentialStatus UPDATED_RESIDENTIAL_STATUS = ResidentialStatus.RENTED;

    private static final Integer DEFAULT_FLAT_AREA = 1;
    private static final Integer UPDATED_FLAT_AREA = 2;

    private static final String ENTITY_API_URL = "/api/flats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FlatRepository flatRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Flat flat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Flat createEntity(EntityManager em) {
        Flat flat = new Flat().flatNo(DEFAULT_FLAT_NO).residentialStatus(DEFAULT_RESIDENTIAL_STATUS).flatArea(DEFAULT_FLAT_AREA);
        return flat;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Flat createUpdatedEntity(EntityManager em) {
        Flat flat = new Flat().flatNo(UPDATED_FLAT_NO).residentialStatus(UPDATED_RESIDENTIAL_STATUS).flatArea(UPDATED_FLAT_AREA);
        return flat;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Flat.class).block();
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
        flat = createEntity(em);
    }

    @Test
    void createFlat() throws Exception {
        int databaseSizeBeforeCreate = flatRepository.findAll().collectList().block().size();
        // Create the Flat
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(flat))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Flat in the database
        List<Flat> flatList = flatRepository.findAll().collectList().block();
        assertThat(flatList).hasSize(databaseSizeBeforeCreate + 1);
        Flat testFlat = flatList.get(flatList.size() - 1);
        assertThat(testFlat.getFlatNo()).isEqualTo(DEFAULT_FLAT_NO);
        assertThat(testFlat.getResidentialStatus()).isEqualTo(DEFAULT_RESIDENTIAL_STATUS);
        assertThat(testFlat.getFlatArea()).isEqualTo(DEFAULT_FLAT_AREA);
    }

    @Test
    void createFlatWithExistingId() throws Exception {
        // Create the Flat with an existing ID
        flat.setId(1L);

        int databaseSizeBeforeCreate = flatRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(flat))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Flat in the database
        List<Flat> flatList = flatRepository.findAll().collectList().block();
        assertThat(flatList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkFlatNoIsRequired() throws Exception {
        int databaseSizeBeforeTest = flatRepository.findAll().collectList().block().size();
        // set the field null
        flat.setFlatNo(null);

        // Create the Flat, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(flat))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Flat> flatList = flatRepository.findAll().collectList().block();
        assertThat(flatList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllFlatsAsStream() {
        // Initialize the database
        flatRepository.save(flat).block();

        List<Flat> flatList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Flat.class)
            .getResponseBody()
            .filter(flat::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(flatList).isNotNull();
        assertThat(flatList).hasSize(1);
        Flat testFlat = flatList.get(0);
        assertThat(testFlat.getFlatNo()).isEqualTo(DEFAULT_FLAT_NO);
        assertThat(testFlat.getResidentialStatus()).isEqualTo(DEFAULT_RESIDENTIAL_STATUS);
        assertThat(testFlat.getFlatArea()).isEqualTo(DEFAULT_FLAT_AREA);
    }

    @Test
    void getAllFlats() {
        // Initialize the database
        flatRepository.save(flat).block();

        // Get all the flatList
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
            .value(hasItem(flat.getId().intValue()))
            .jsonPath("$.[*].flatNo")
            .value(hasItem(DEFAULT_FLAT_NO))
            .jsonPath("$.[*].residentialStatus")
            .value(hasItem(DEFAULT_RESIDENTIAL_STATUS.toString()))
            .jsonPath("$.[*].flatArea")
            .value(hasItem(DEFAULT_FLAT_AREA));
    }

    @Test
    void getFlat() {
        // Initialize the database
        flatRepository.save(flat).block();

        // Get the flat
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, flat.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(flat.getId().intValue()))
            .jsonPath("$.flatNo")
            .value(is(DEFAULT_FLAT_NO))
            .jsonPath("$.residentialStatus")
            .value(is(DEFAULT_RESIDENTIAL_STATUS.toString()))
            .jsonPath("$.flatArea")
            .value(is(DEFAULT_FLAT_AREA));
    }

    @Test
    void getNonExistingFlat() {
        // Get the flat
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewFlat() throws Exception {
        // Initialize the database
        flatRepository.save(flat).block();

        int databaseSizeBeforeUpdate = flatRepository.findAll().collectList().block().size();

        // Update the flat
        Flat updatedFlat = flatRepository.findById(flat.getId()).block();
        updatedFlat.flatNo(UPDATED_FLAT_NO).residentialStatus(UPDATED_RESIDENTIAL_STATUS).flatArea(UPDATED_FLAT_AREA);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedFlat.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedFlat))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Flat in the database
        List<Flat> flatList = flatRepository.findAll().collectList().block();
        assertThat(flatList).hasSize(databaseSizeBeforeUpdate);
        Flat testFlat = flatList.get(flatList.size() - 1);
        assertThat(testFlat.getFlatNo()).isEqualTo(UPDATED_FLAT_NO);
        assertThat(testFlat.getResidentialStatus()).isEqualTo(UPDATED_RESIDENTIAL_STATUS);
        assertThat(testFlat.getFlatArea()).isEqualTo(UPDATED_FLAT_AREA);
    }

    @Test
    void putNonExistingFlat() throws Exception {
        int databaseSizeBeforeUpdate = flatRepository.findAll().collectList().block().size();
        flat.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, flat.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(flat))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Flat in the database
        List<Flat> flatList = flatRepository.findAll().collectList().block();
        assertThat(flatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFlat() throws Exception {
        int databaseSizeBeforeUpdate = flatRepository.findAll().collectList().block().size();
        flat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(flat))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Flat in the database
        List<Flat> flatList = flatRepository.findAll().collectList().block();
        assertThat(flatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFlat() throws Exception {
        int databaseSizeBeforeUpdate = flatRepository.findAll().collectList().block().size();
        flat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(flat))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Flat in the database
        List<Flat> flatList = flatRepository.findAll().collectList().block();
        assertThat(flatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFlatWithPatch() throws Exception {
        // Initialize the database
        flatRepository.save(flat).block();

        int databaseSizeBeforeUpdate = flatRepository.findAll().collectList().block().size();

        // Update the flat using partial update
        Flat partialUpdatedFlat = new Flat();
        partialUpdatedFlat.setId(flat.getId());

        partialUpdatedFlat.flatNo(UPDATED_FLAT_NO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFlat.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFlat))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Flat in the database
        List<Flat> flatList = flatRepository.findAll().collectList().block();
        assertThat(flatList).hasSize(databaseSizeBeforeUpdate);
        Flat testFlat = flatList.get(flatList.size() - 1);
        assertThat(testFlat.getFlatNo()).isEqualTo(UPDATED_FLAT_NO);
        assertThat(testFlat.getResidentialStatus()).isEqualTo(DEFAULT_RESIDENTIAL_STATUS);
        assertThat(testFlat.getFlatArea()).isEqualTo(DEFAULT_FLAT_AREA);
    }

    @Test
    void fullUpdateFlatWithPatch() throws Exception {
        // Initialize the database
        flatRepository.save(flat).block();

        int databaseSizeBeforeUpdate = flatRepository.findAll().collectList().block().size();

        // Update the flat using partial update
        Flat partialUpdatedFlat = new Flat();
        partialUpdatedFlat.setId(flat.getId());

        partialUpdatedFlat.flatNo(UPDATED_FLAT_NO).residentialStatus(UPDATED_RESIDENTIAL_STATUS).flatArea(UPDATED_FLAT_AREA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFlat.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFlat))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Flat in the database
        List<Flat> flatList = flatRepository.findAll().collectList().block();
        assertThat(flatList).hasSize(databaseSizeBeforeUpdate);
        Flat testFlat = flatList.get(flatList.size() - 1);
        assertThat(testFlat.getFlatNo()).isEqualTo(UPDATED_FLAT_NO);
        assertThat(testFlat.getResidentialStatus()).isEqualTo(UPDATED_RESIDENTIAL_STATUS);
        assertThat(testFlat.getFlatArea()).isEqualTo(UPDATED_FLAT_AREA);
    }

    @Test
    void patchNonExistingFlat() throws Exception {
        int databaseSizeBeforeUpdate = flatRepository.findAll().collectList().block().size();
        flat.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, flat.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(flat))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Flat in the database
        List<Flat> flatList = flatRepository.findAll().collectList().block();
        assertThat(flatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFlat() throws Exception {
        int databaseSizeBeforeUpdate = flatRepository.findAll().collectList().block().size();
        flat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(flat))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Flat in the database
        List<Flat> flatList = flatRepository.findAll().collectList().block();
        assertThat(flatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFlat() throws Exception {
        int databaseSizeBeforeUpdate = flatRepository.findAll().collectList().block().size();
        flat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(flat))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Flat in the database
        List<Flat> flatList = flatRepository.findAll().collectList().block();
        assertThat(flatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFlat() {
        // Initialize the database
        flatRepository.save(flat).block();

        int databaseSizeBeforeDelete = flatRepository.findAll().collectList().block().size();

        // Delete the flat
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, flat.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Flat> flatList = flatRepository.findAll().collectList().block();
        assertThat(flatList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

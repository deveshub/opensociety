package com.bits.opensociety.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bits.opensociety.IntegrationTest;
import com.bits.opensociety.domain.VisitingFlat;
import com.bits.opensociety.repository.VisitingFlatRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VisitingFlatResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VisitingFlatResourceIT {

    private static final String DEFAULT_FLAT_NO = "AAAAAAAAAA";
    private static final String UPDATED_FLAT_NO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/visiting-flats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VisitingFlatRepository visitingFlatRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVisitingFlatMockMvc;

    private VisitingFlat visitingFlat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VisitingFlat createEntity(EntityManager em) {
        VisitingFlat visitingFlat = new VisitingFlat().flatNo(DEFAULT_FLAT_NO);
        return visitingFlat;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VisitingFlat createUpdatedEntity(EntityManager em) {
        VisitingFlat visitingFlat = new VisitingFlat().flatNo(UPDATED_FLAT_NO);
        return visitingFlat;
    }

    @BeforeEach
    public void initTest() {
        visitingFlat = createEntity(em);
    }

    @Test
    @Transactional
    void createVisitingFlat() throws Exception {
        int databaseSizeBeforeCreate = visitingFlatRepository.findAll().size();
        // Create the VisitingFlat
        restVisitingFlatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitingFlat)))
            .andExpect(status().isCreated());

        // Validate the VisitingFlat in the database
        List<VisitingFlat> visitingFlatList = visitingFlatRepository.findAll();
        assertThat(visitingFlatList).hasSize(databaseSizeBeforeCreate + 1);
        VisitingFlat testVisitingFlat = visitingFlatList.get(visitingFlatList.size() - 1);
        assertThat(testVisitingFlat.getFlatNo()).isEqualTo(DEFAULT_FLAT_NO);
    }

    @Test
    @Transactional
    void createVisitingFlatWithExistingId() throws Exception {
        // Create the VisitingFlat with an existing ID
        visitingFlat.setId(1L);

        int databaseSizeBeforeCreate = visitingFlatRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVisitingFlatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitingFlat)))
            .andExpect(status().isBadRequest());

        // Validate the VisitingFlat in the database
        List<VisitingFlat> visitingFlatList = visitingFlatRepository.findAll();
        assertThat(visitingFlatList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFlatNoIsRequired() throws Exception {
        int databaseSizeBeforeTest = visitingFlatRepository.findAll().size();
        // set the field null
        visitingFlat.setFlatNo(null);

        // Create the VisitingFlat, which fails.

        restVisitingFlatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitingFlat)))
            .andExpect(status().isBadRequest());

        List<VisitingFlat> visitingFlatList = visitingFlatRepository.findAll();
        assertThat(visitingFlatList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVisitingFlats() throws Exception {
        // Initialize the database
        visitingFlatRepository.saveAndFlush(visitingFlat);

        // Get all the visitingFlatList
        restVisitingFlatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(visitingFlat.getId().intValue())))
            .andExpect(jsonPath("$.[*].flatNo").value(hasItem(DEFAULT_FLAT_NO)));
    }

    @Test
    @Transactional
    void getVisitingFlat() throws Exception {
        // Initialize the database
        visitingFlatRepository.saveAndFlush(visitingFlat);

        // Get the visitingFlat
        restVisitingFlatMockMvc
            .perform(get(ENTITY_API_URL_ID, visitingFlat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(visitingFlat.getId().intValue()))
            .andExpect(jsonPath("$.flatNo").value(DEFAULT_FLAT_NO));
    }

    @Test
    @Transactional
    void getNonExistingVisitingFlat() throws Exception {
        // Get the visitingFlat
        restVisitingFlatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewVisitingFlat() throws Exception {
        // Initialize the database
        visitingFlatRepository.saveAndFlush(visitingFlat);

        int databaseSizeBeforeUpdate = visitingFlatRepository.findAll().size();

        // Update the visitingFlat
        VisitingFlat updatedVisitingFlat = visitingFlatRepository.findById(visitingFlat.getId()).get();
        // Disconnect from session so that the updates on updatedVisitingFlat are not directly saved in db
        em.detach(updatedVisitingFlat);
        updatedVisitingFlat.flatNo(UPDATED_FLAT_NO);

        restVisitingFlatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedVisitingFlat.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedVisitingFlat))
            )
            .andExpect(status().isOk());

        // Validate the VisitingFlat in the database
        List<VisitingFlat> visitingFlatList = visitingFlatRepository.findAll();
        assertThat(visitingFlatList).hasSize(databaseSizeBeforeUpdate);
        VisitingFlat testVisitingFlat = visitingFlatList.get(visitingFlatList.size() - 1);
        assertThat(testVisitingFlat.getFlatNo()).isEqualTo(UPDATED_FLAT_NO);
    }

    @Test
    @Transactional
    void putNonExistingVisitingFlat() throws Exception {
        int databaseSizeBeforeUpdate = visitingFlatRepository.findAll().size();
        visitingFlat.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitingFlatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, visitingFlat.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(visitingFlat))
            )
            .andExpect(status().isBadRequest());

        // Validate the VisitingFlat in the database
        List<VisitingFlat> visitingFlatList = visitingFlatRepository.findAll();
        assertThat(visitingFlatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVisitingFlat() throws Exception {
        int databaseSizeBeforeUpdate = visitingFlatRepository.findAll().size();
        visitingFlat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitingFlatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(visitingFlat))
            )
            .andExpect(status().isBadRequest());

        // Validate the VisitingFlat in the database
        List<VisitingFlat> visitingFlatList = visitingFlatRepository.findAll();
        assertThat(visitingFlatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVisitingFlat() throws Exception {
        int databaseSizeBeforeUpdate = visitingFlatRepository.findAll().size();
        visitingFlat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitingFlatMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitingFlat)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VisitingFlat in the database
        List<VisitingFlat> visitingFlatList = visitingFlatRepository.findAll();
        assertThat(visitingFlatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVisitingFlatWithPatch() throws Exception {
        // Initialize the database
        visitingFlatRepository.saveAndFlush(visitingFlat);

        int databaseSizeBeforeUpdate = visitingFlatRepository.findAll().size();

        // Update the visitingFlat using partial update
        VisitingFlat partialUpdatedVisitingFlat = new VisitingFlat();
        partialUpdatedVisitingFlat.setId(visitingFlat.getId());

        partialUpdatedVisitingFlat.flatNo(UPDATED_FLAT_NO);

        restVisitingFlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisitingFlat.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVisitingFlat))
            )
            .andExpect(status().isOk());

        // Validate the VisitingFlat in the database
        List<VisitingFlat> visitingFlatList = visitingFlatRepository.findAll();
        assertThat(visitingFlatList).hasSize(databaseSizeBeforeUpdate);
        VisitingFlat testVisitingFlat = visitingFlatList.get(visitingFlatList.size() - 1);
        assertThat(testVisitingFlat.getFlatNo()).isEqualTo(UPDATED_FLAT_NO);
    }

    @Test
    @Transactional
    void fullUpdateVisitingFlatWithPatch() throws Exception {
        // Initialize the database
        visitingFlatRepository.saveAndFlush(visitingFlat);

        int databaseSizeBeforeUpdate = visitingFlatRepository.findAll().size();

        // Update the visitingFlat using partial update
        VisitingFlat partialUpdatedVisitingFlat = new VisitingFlat();
        partialUpdatedVisitingFlat.setId(visitingFlat.getId());

        partialUpdatedVisitingFlat.flatNo(UPDATED_FLAT_NO);

        restVisitingFlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisitingFlat.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVisitingFlat))
            )
            .andExpect(status().isOk());

        // Validate the VisitingFlat in the database
        List<VisitingFlat> visitingFlatList = visitingFlatRepository.findAll();
        assertThat(visitingFlatList).hasSize(databaseSizeBeforeUpdate);
        VisitingFlat testVisitingFlat = visitingFlatList.get(visitingFlatList.size() - 1);
        assertThat(testVisitingFlat.getFlatNo()).isEqualTo(UPDATED_FLAT_NO);
    }

    @Test
    @Transactional
    void patchNonExistingVisitingFlat() throws Exception {
        int databaseSizeBeforeUpdate = visitingFlatRepository.findAll().size();
        visitingFlat.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitingFlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, visitingFlat.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(visitingFlat))
            )
            .andExpect(status().isBadRequest());

        // Validate the VisitingFlat in the database
        List<VisitingFlat> visitingFlatList = visitingFlatRepository.findAll();
        assertThat(visitingFlatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVisitingFlat() throws Exception {
        int databaseSizeBeforeUpdate = visitingFlatRepository.findAll().size();
        visitingFlat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitingFlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(visitingFlat))
            )
            .andExpect(status().isBadRequest());

        // Validate the VisitingFlat in the database
        List<VisitingFlat> visitingFlatList = visitingFlatRepository.findAll();
        assertThat(visitingFlatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVisitingFlat() throws Exception {
        int databaseSizeBeforeUpdate = visitingFlatRepository.findAll().size();
        visitingFlat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitingFlatMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(visitingFlat))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the VisitingFlat in the database
        List<VisitingFlat> visitingFlatList = visitingFlatRepository.findAll();
        assertThat(visitingFlatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVisitingFlat() throws Exception {
        // Initialize the database
        visitingFlatRepository.saveAndFlush(visitingFlat);

        int databaseSizeBeforeDelete = visitingFlatRepository.findAll().size();

        // Delete the visitingFlat
        restVisitingFlatMockMvc
            .perform(delete(ENTITY_API_URL_ID, visitingFlat.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<VisitingFlat> visitingFlatList = visitingFlatRepository.findAll();
        assertThat(visitingFlatList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

package com.bits.opensociety.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bits.opensociety.IntegrationTest;
import com.bits.opensociety.domain.Visitor;
import com.bits.opensociety.domain.enumeration.VisitorType;
import com.bits.opensociety.repository.VisitorRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VisitorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class VisitorResourceIT {

    private static final VisitorType DEFAULT_VISITOR_TYPE = VisitorType.GUEST;
    private static final VisitorType UPDATED_VISITOR_TYPE = VisitorType.DELIVERY;

    private static final String DEFAULT_MOBILE = "AAAAAAAAAA";
    private static final String UPDATED_MOBILE = "BBBBBBBBBB";

    private static final String DEFAULT_VEHICLE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_VEHICLE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Instant DEFAULT_IN_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_IN_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_OUT_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OUT_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/visitors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VisitorRepository visitorRepository;

    @Mock
    private VisitorRepository visitorRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVisitorMockMvc;

    private Visitor visitor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Visitor createEntity(EntityManager em) {
        Visitor visitor = new Visitor()
            .visitorType(DEFAULT_VISITOR_TYPE)
            .mobile(DEFAULT_MOBILE)
            .vehicleNumber(DEFAULT_VEHICLE_NUMBER)
            .address(DEFAULT_ADDRESS)
            .inTime(DEFAULT_IN_TIME)
            .outTime(DEFAULT_OUT_TIME);
        return visitor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Visitor createUpdatedEntity(EntityManager em) {
        Visitor visitor = new Visitor()
            .visitorType(UPDATED_VISITOR_TYPE)
            .mobile(UPDATED_MOBILE)
            .vehicleNumber(UPDATED_VEHICLE_NUMBER)
            .address(UPDATED_ADDRESS)
            .inTime(UPDATED_IN_TIME)
            .outTime(UPDATED_OUT_TIME);
        return visitor;
    }

    @BeforeEach
    public void initTest() {
        visitor = createEntity(em);
    }

    @Test
    @Transactional
    void createVisitor() throws Exception {
        int databaseSizeBeforeCreate = visitorRepository.findAll().size();
        // Create the Visitor
        restVisitorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitor)))
            .andExpect(status().isCreated());

        // Validate the Visitor in the database
        List<Visitor> visitorList = visitorRepository.findAll();
        assertThat(visitorList).hasSize(databaseSizeBeforeCreate + 1);
        Visitor testVisitor = visitorList.get(visitorList.size() - 1);
        assertThat(testVisitor.getVisitorType()).isEqualTo(DEFAULT_VISITOR_TYPE);
        assertThat(testVisitor.getMobile()).isEqualTo(DEFAULT_MOBILE);
        assertThat(testVisitor.getVehicleNumber()).isEqualTo(DEFAULT_VEHICLE_NUMBER);
        assertThat(testVisitor.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testVisitor.getInTime()).isEqualTo(DEFAULT_IN_TIME);
        assertThat(testVisitor.getOutTime()).isEqualTo(DEFAULT_OUT_TIME);
    }

    @Test
    @Transactional
    void createVisitorWithExistingId() throws Exception {
        // Create the Visitor with an existing ID
        visitor.setId(1L);

        int databaseSizeBeforeCreate = visitorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVisitorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitor)))
            .andExpect(status().isBadRequest());

        // Validate the Visitor in the database
        List<Visitor> visitorList = visitorRepository.findAll();
        assertThat(visitorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkVisitorTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = visitorRepository.findAll().size();
        // set the field null
        visitor.setVisitorType(null);

        // Create the Visitor, which fails.

        restVisitorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitor)))
            .andExpect(status().isBadRequest());

        List<Visitor> visitorList = visitorRepository.findAll();
        assertThat(visitorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVisitors() throws Exception {
        // Initialize the database
        visitorRepository.saveAndFlush(visitor);

        // Get all the visitorList
        restVisitorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(visitor.getId().intValue())))
            .andExpect(jsonPath("$.[*].visitorType").value(hasItem(DEFAULT_VISITOR_TYPE.toString())))
            .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE)))
            .andExpect(jsonPath("$.[*].vehicleNumber").value(hasItem(DEFAULT_VEHICLE_NUMBER)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].inTime").value(hasItem(DEFAULT_IN_TIME.toString())))
            .andExpect(jsonPath("$.[*].outTime").value(hasItem(DEFAULT_OUT_TIME.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVisitorsWithEagerRelationshipsIsEnabled() throws Exception {
        when(visitorRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVisitorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(visitorRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVisitorsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(visitorRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVisitorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(visitorRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getVisitor() throws Exception {
        // Initialize the database
        visitorRepository.saveAndFlush(visitor);

        // Get the visitor
        restVisitorMockMvc
            .perform(get(ENTITY_API_URL_ID, visitor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(visitor.getId().intValue()))
            .andExpect(jsonPath("$.visitorType").value(DEFAULT_VISITOR_TYPE.toString()))
            .andExpect(jsonPath("$.mobile").value(DEFAULT_MOBILE))
            .andExpect(jsonPath("$.vehicleNumber").value(DEFAULT_VEHICLE_NUMBER))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.inTime").value(DEFAULT_IN_TIME.toString()))
            .andExpect(jsonPath("$.outTime").value(DEFAULT_OUT_TIME.toString()));
    }

    @Test
    @Transactional
    void getNonExistingVisitor() throws Exception {
        // Get the visitor
        restVisitorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewVisitor() throws Exception {
        // Initialize the database
        visitorRepository.saveAndFlush(visitor);

        int databaseSizeBeforeUpdate = visitorRepository.findAll().size();

        // Update the visitor
        Visitor updatedVisitor = visitorRepository.findById(visitor.getId()).get();
        // Disconnect from session so that the updates on updatedVisitor are not directly saved in db
        em.detach(updatedVisitor);
        updatedVisitor
            .visitorType(UPDATED_VISITOR_TYPE)
            .mobile(UPDATED_MOBILE)
            .vehicleNumber(UPDATED_VEHICLE_NUMBER)
            .address(UPDATED_ADDRESS)
            .inTime(UPDATED_IN_TIME)
            .outTime(UPDATED_OUT_TIME);

        restVisitorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedVisitor.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedVisitor))
            )
            .andExpect(status().isOk());

        // Validate the Visitor in the database
        List<Visitor> visitorList = visitorRepository.findAll();
        assertThat(visitorList).hasSize(databaseSizeBeforeUpdate);
        Visitor testVisitor = visitorList.get(visitorList.size() - 1);
        assertThat(testVisitor.getVisitorType()).isEqualTo(UPDATED_VISITOR_TYPE);
        assertThat(testVisitor.getMobile()).isEqualTo(UPDATED_MOBILE);
        assertThat(testVisitor.getVehicleNumber()).isEqualTo(UPDATED_VEHICLE_NUMBER);
        assertThat(testVisitor.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testVisitor.getInTime()).isEqualTo(UPDATED_IN_TIME);
        assertThat(testVisitor.getOutTime()).isEqualTo(UPDATED_OUT_TIME);
    }

    @Test
    @Transactional
    void putNonExistingVisitor() throws Exception {
        int databaseSizeBeforeUpdate = visitorRepository.findAll().size();
        visitor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, visitor.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(visitor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visitor in the database
        List<Visitor> visitorList = visitorRepository.findAll();
        assertThat(visitorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVisitor() throws Exception {
        int databaseSizeBeforeUpdate = visitorRepository.findAll().size();
        visitor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(visitor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visitor in the database
        List<Visitor> visitorList = visitorRepository.findAll();
        assertThat(visitorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVisitor() throws Exception {
        int databaseSizeBeforeUpdate = visitorRepository.findAll().size();
        visitor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Visitor in the database
        List<Visitor> visitorList = visitorRepository.findAll();
        assertThat(visitorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVisitorWithPatch() throws Exception {
        // Initialize the database
        visitorRepository.saveAndFlush(visitor);

        int databaseSizeBeforeUpdate = visitorRepository.findAll().size();

        // Update the visitor using partial update
        Visitor partialUpdatedVisitor = new Visitor();
        partialUpdatedVisitor.setId(visitor.getId());

        partialUpdatedVisitor.visitorType(UPDATED_VISITOR_TYPE).vehicleNumber(UPDATED_VEHICLE_NUMBER).address(UPDATED_ADDRESS);

        restVisitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisitor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVisitor))
            )
            .andExpect(status().isOk());

        // Validate the Visitor in the database
        List<Visitor> visitorList = visitorRepository.findAll();
        assertThat(visitorList).hasSize(databaseSizeBeforeUpdate);
        Visitor testVisitor = visitorList.get(visitorList.size() - 1);
        assertThat(testVisitor.getVisitorType()).isEqualTo(UPDATED_VISITOR_TYPE);
        assertThat(testVisitor.getMobile()).isEqualTo(DEFAULT_MOBILE);
        assertThat(testVisitor.getVehicleNumber()).isEqualTo(UPDATED_VEHICLE_NUMBER);
        assertThat(testVisitor.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testVisitor.getInTime()).isEqualTo(DEFAULT_IN_TIME);
        assertThat(testVisitor.getOutTime()).isEqualTo(DEFAULT_OUT_TIME);
    }

    @Test
    @Transactional
    void fullUpdateVisitorWithPatch() throws Exception {
        // Initialize the database
        visitorRepository.saveAndFlush(visitor);

        int databaseSizeBeforeUpdate = visitorRepository.findAll().size();

        // Update the visitor using partial update
        Visitor partialUpdatedVisitor = new Visitor();
        partialUpdatedVisitor.setId(visitor.getId());

        partialUpdatedVisitor
            .visitorType(UPDATED_VISITOR_TYPE)
            .mobile(UPDATED_MOBILE)
            .vehicleNumber(UPDATED_VEHICLE_NUMBER)
            .address(UPDATED_ADDRESS)
            .inTime(UPDATED_IN_TIME)
            .outTime(UPDATED_OUT_TIME);

        restVisitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisitor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVisitor))
            )
            .andExpect(status().isOk());

        // Validate the Visitor in the database
        List<Visitor> visitorList = visitorRepository.findAll();
        assertThat(visitorList).hasSize(databaseSizeBeforeUpdate);
        Visitor testVisitor = visitorList.get(visitorList.size() - 1);
        assertThat(testVisitor.getVisitorType()).isEqualTo(UPDATED_VISITOR_TYPE);
        assertThat(testVisitor.getMobile()).isEqualTo(UPDATED_MOBILE);
        assertThat(testVisitor.getVehicleNumber()).isEqualTo(UPDATED_VEHICLE_NUMBER);
        assertThat(testVisitor.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testVisitor.getInTime()).isEqualTo(UPDATED_IN_TIME);
        assertThat(testVisitor.getOutTime()).isEqualTo(UPDATED_OUT_TIME);
    }

    @Test
    @Transactional
    void patchNonExistingVisitor() throws Exception {
        int databaseSizeBeforeUpdate = visitorRepository.findAll().size();
        visitor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, visitor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(visitor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visitor in the database
        List<Visitor> visitorList = visitorRepository.findAll();
        assertThat(visitorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVisitor() throws Exception {
        int databaseSizeBeforeUpdate = visitorRepository.findAll().size();
        visitor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(visitor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visitor in the database
        List<Visitor> visitorList = visitorRepository.findAll();
        assertThat(visitorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVisitor() throws Exception {
        int databaseSizeBeforeUpdate = visitorRepository.findAll().size();
        visitor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(visitor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Visitor in the database
        List<Visitor> visitorList = visitorRepository.findAll();
        assertThat(visitorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVisitor() throws Exception {
        // Initialize the database
        visitorRepository.saveAndFlush(visitor);

        int databaseSizeBeforeDelete = visitorRepository.findAll().size();

        // Delete the visitor
        restVisitorMockMvc
            .perform(delete(ENTITY_API_URL_ID, visitor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Visitor> visitorList = visitorRepository.findAll();
        assertThat(visitorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

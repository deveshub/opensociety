package com.demo.opensociety.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.demo.opensociety.IntegrationTest;
import com.demo.opensociety.domain.Notice;
import com.demo.opensociety.domain.enumeration.NoticeType;
import com.demo.opensociety.repository.NoticeRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link NoticeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NoticeResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final Instant DEFAULT_PUBLISH_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PUBLISH_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final NoticeType DEFAULT_NOTICE_TYPE = NoticeType.SOCIETY;
    private static final NoticeType UPDATED_NOTICE_TYPE = NoticeType.EVENT;

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String ENTITY_API_URL = "/api/notices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private MockMvc restNoticeMockMvc;

    private Notice notice;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notice createEntity() {
        Notice notice = new Notice()
            .title(DEFAULT_TITLE)
            .body(DEFAULT_BODY)
            .publishDate(DEFAULT_PUBLISH_DATE)
            .noticeType(DEFAULT_NOTICE_TYPE)
            .userId(DEFAULT_USER_ID);
        return notice;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notice createUpdatedEntity() {
        Notice notice = new Notice()
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .publishDate(UPDATED_PUBLISH_DATE)
            .noticeType(UPDATED_NOTICE_TYPE)
            .userId(UPDATED_USER_ID);
        return notice;
    }

    @BeforeEach
    public void initTest() {
        noticeRepository.deleteAll();
        notice = createEntity();
    }

    @Test
    void createNotice() throws Exception {
        int databaseSizeBeforeCreate = noticeRepository.findAll().size();
        // Create the Notice
        restNoticeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notice)))
            .andExpect(status().isCreated());

        // Validate the Notice in the database
        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeCreate + 1);
        Notice testNotice = noticeList.get(noticeList.size() - 1);
        assertThat(testNotice.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testNotice.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testNotice.getPublishDate()).isEqualTo(DEFAULT_PUBLISH_DATE);
        assertThat(testNotice.getNoticeType()).isEqualTo(DEFAULT_NOTICE_TYPE);
        assertThat(testNotice.getUserId()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    void createNoticeWithExistingId() throws Exception {
        // Create the Notice with an existing ID
        notice.setId("existing_id");

        int databaseSizeBeforeCreate = noticeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNoticeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notice)))
            .andExpect(status().isBadRequest());

        // Validate the Notice in the database
        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = noticeRepository.findAll().size();
        // set the field null
        notice.setTitle(null);

        // Create the Notice, which fails.

        restNoticeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notice)))
            .andExpect(status().isBadRequest());

        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkBodyIsRequired() throws Exception {
        int databaseSizeBeforeTest = noticeRepository.findAll().size();
        // set the field null
        notice.setBody(null);

        // Create the Notice, which fails.

        restNoticeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notice)))
            .andExpect(status().isBadRequest());

        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPublishDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = noticeRepository.findAll().size();
        // set the field null
        notice.setPublishDate(null);

        // Create the Notice, which fails.

        restNoticeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notice)))
            .andExpect(status().isBadRequest());

        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUserIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = noticeRepository.findAll().size();
        // set the field null
        notice.setUserId(null);

        // Create the Notice, which fails.

        restNoticeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notice)))
            .andExpect(status().isBadRequest());

        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllNotices() throws Exception {
        // Initialize the database
        noticeRepository.save(notice);

        // Get all the noticeList
        restNoticeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notice.getId())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].publishDate").value(hasItem(DEFAULT_PUBLISH_DATE.toString())))
            .andExpect(jsonPath("$.[*].noticeType").value(hasItem(DEFAULT_NOTICE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())));
    }

    @Test
    void getNotice() throws Exception {
        // Initialize the database
        noticeRepository.save(notice);

        // Get the notice
        restNoticeMockMvc
            .perform(get(ENTITY_API_URL_ID, notice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notice.getId()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY))
            .andExpect(jsonPath("$.publishDate").value(DEFAULT_PUBLISH_DATE.toString()))
            .andExpect(jsonPath("$.noticeType").value(DEFAULT_NOTICE_TYPE.toString()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()));
    }

    @Test
    void getNonExistingNotice() throws Exception {
        // Get the notice
        restNoticeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putNewNotice() throws Exception {
        // Initialize the database
        noticeRepository.save(notice);

        int databaseSizeBeforeUpdate = noticeRepository.findAll().size();

        // Update the notice
        Notice updatedNotice = noticeRepository.findById(notice.getId()).get();
        updatedNotice
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .publishDate(UPDATED_PUBLISH_DATE)
            .noticeType(UPDATED_NOTICE_TYPE)
            .userId(UPDATED_USER_ID);

        restNoticeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedNotice.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedNotice))
            )
            .andExpect(status().isOk());

        // Validate the Notice in the database
        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeUpdate);
        Notice testNotice = noticeList.get(noticeList.size() - 1);
        assertThat(testNotice.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testNotice.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testNotice.getPublishDate()).isEqualTo(UPDATED_PUBLISH_DATE);
        assertThat(testNotice.getNoticeType()).isEqualTo(UPDATED_NOTICE_TYPE);
        assertThat(testNotice.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    void putNonExistingNotice() throws Exception {
        int databaseSizeBeforeUpdate = noticeRepository.findAll().size();
        notice.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNoticeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notice.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notice))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notice in the database
        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchNotice() throws Exception {
        int databaseSizeBeforeUpdate = noticeRepository.findAll().size();
        notice.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoticeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notice))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notice in the database
        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamNotice() throws Exception {
        int databaseSizeBeforeUpdate = noticeRepository.findAll().size();
        notice.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoticeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notice)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notice in the database
        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateNoticeWithPatch() throws Exception {
        // Initialize the database
        noticeRepository.save(notice);

        int databaseSizeBeforeUpdate = noticeRepository.findAll().size();

        // Update the notice using partial update
        Notice partialUpdatedNotice = new Notice();
        partialUpdatedNotice.setId(notice.getId());

        partialUpdatedNotice.title(UPDATED_TITLE).publishDate(UPDATED_PUBLISH_DATE).noticeType(UPDATED_NOTICE_TYPE).userId(UPDATED_USER_ID);

        restNoticeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotice.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotice))
            )
            .andExpect(status().isOk());

        // Validate the Notice in the database
        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeUpdate);
        Notice testNotice = noticeList.get(noticeList.size() - 1);
        assertThat(testNotice.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testNotice.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testNotice.getPublishDate()).isEqualTo(UPDATED_PUBLISH_DATE);
        assertThat(testNotice.getNoticeType()).isEqualTo(UPDATED_NOTICE_TYPE);
        assertThat(testNotice.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    void fullUpdateNoticeWithPatch() throws Exception {
        // Initialize the database
        noticeRepository.save(notice);

        int databaseSizeBeforeUpdate = noticeRepository.findAll().size();

        // Update the notice using partial update
        Notice partialUpdatedNotice = new Notice();
        partialUpdatedNotice.setId(notice.getId());

        partialUpdatedNotice
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .publishDate(UPDATED_PUBLISH_DATE)
            .noticeType(UPDATED_NOTICE_TYPE)
            .userId(UPDATED_USER_ID);

        restNoticeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotice.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotice))
            )
            .andExpect(status().isOk());

        // Validate the Notice in the database
        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeUpdate);
        Notice testNotice = noticeList.get(noticeList.size() - 1);
        assertThat(testNotice.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testNotice.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testNotice.getPublishDate()).isEqualTo(UPDATED_PUBLISH_DATE);
        assertThat(testNotice.getNoticeType()).isEqualTo(UPDATED_NOTICE_TYPE);
        assertThat(testNotice.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    void patchNonExistingNotice() throws Exception {
        int databaseSizeBeforeUpdate = noticeRepository.findAll().size();
        notice.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNoticeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notice.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notice))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notice in the database
        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchNotice() throws Exception {
        int databaseSizeBeforeUpdate = noticeRepository.findAll().size();
        notice.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoticeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notice))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notice in the database
        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamNotice() throws Exception {
        int databaseSizeBeforeUpdate = noticeRepository.findAll().size();
        notice.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoticeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(notice)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notice in the database
        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteNotice() throws Exception {
        // Initialize the database
        noticeRepository.save(notice);

        int databaseSizeBeforeDelete = noticeRepository.findAll().size();

        // Delete the notice
        restNoticeMockMvc
            .perform(delete(ENTITY_API_URL_ID, notice.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Notice> noticeList = noticeRepository.findAll();
        assertThat(noticeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

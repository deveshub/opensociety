package com.demo.opensociety.web.rest;

import com.demo.opensociety.domain.Notice;
import com.demo.opensociety.repository.NoticeRepository;
import com.demo.opensociety.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.demo.opensociety.domain.Notice}.
 */
@RestController
@RequestMapping("/api")
public class NoticeResource {

    private final Logger log = LoggerFactory.getLogger(NoticeResource.class);

    private static final String ENTITY_NAME = "communicationServiceNotice";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NoticeRepository noticeRepository;

    public NoticeResource(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    /**
     * {@code POST  /notices} : Create a new notice.
     *
     * @param notice the notice to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notice, or with status {@code 400 (Bad Request)} if the notice has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/notices")
    public ResponseEntity<Notice> createNotice(@Valid @RequestBody Notice notice) throws URISyntaxException {
        log.debug("REST request to save Notice : {}", notice);
        if (notice.getId() != null) {
            throw new BadRequestAlertException("A new notice cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Notice result = noticeRepository.save(notice);
        return ResponseEntity
            .created(new URI("/api/notices/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /notices/:id} : Updates an existing notice.
     *
     * @param id the id of the notice to save.
     * @param notice the notice to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notice,
     * or with status {@code 400 (Bad Request)} if the notice is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notice couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/notices/{id}")
    public ResponseEntity<Notice> updateNotice(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Notice notice
    ) throws URISyntaxException {
        log.debug("REST request to update Notice : {}, {}", id, notice);
        if (notice.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notice.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!noticeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Notice result = noticeRepository.save(notice);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notice.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /notices/:id} : Partial updates given fields of an existing notice, field will ignore if it is null
     *
     * @param id the id of the notice to save.
     * @param notice the notice to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notice,
     * or with status {@code 400 (Bad Request)} if the notice is not valid,
     * or with status {@code 404 (Not Found)} if the notice is not found,
     * or with status {@code 500 (Internal Server Error)} if the notice couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/notices/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Notice> partialUpdateNotice(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Notice notice
    ) throws URISyntaxException {
        log.debug("REST request to partial update Notice partially : {}, {}", id, notice);
        if (notice.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notice.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!noticeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Notice> result = noticeRepository
            .findById(notice.getId())
            .map(existingNotice -> {
                if (notice.getTitle() != null) {
                    existingNotice.setTitle(notice.getTitle());
                }
                if (notice.getBody() != null) {
                    existingNotice.setBody(notice.getBody());
                }
                if (notice.getPublishDate() != null) {
                    existingNotice.setPublishDate(notice.getPublishDate());
                }
                if (notice.getNoticeType() != null) {
                    existingNotice.setNoticeType(notice.getNoticeType());
                }
                if (notice.getUserId() != null) {
                    existingNotice.setUserId(notice.getUserId());
                }

                return existingNotice;
            })
            .map(noticeRepository::save);

        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notice.getId()));
    }

    /**
     * {@code GET  /notices} : get all the notices.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notices in body.
     */
    @GetMapping("/notices")
    public List<Notice> getAllNotices() {
        log.debug("REST request to get all Notices");
        return noticeRepository.findAll();
    }

    /**
     * {@code GET  /notices/:id} : get the "id" notice.
     *
     * @param id the id of the notice to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notice, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/notices/{id}")
    public ResponseEntity<Notice> getNotice(@PathVariable String id) {
        log.debug("REST request to get Notice : {}", id);
        Optional<Notice> notice = noticeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(notice);
    }

    /**
     * {@code DELETE  /notices/:id} : delete the "id" notice.
     *
     * @param id the id of the notice to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/notices/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable String id) {
        log.debug("REST request to delete Notice : {}", id);
        noticeRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}

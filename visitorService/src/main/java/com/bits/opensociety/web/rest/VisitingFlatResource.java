package com.bits.opensociety.web.rest;

import com.bits.opensociety.domain.VisitingFlat;
import com.bits.opensociety.repository.VisitingFlatRepository;
import com.bits.opensociety.web.rest.errors.BadRequestAlertException;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.bits.opensociety.domain.VisitingFlat}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class VisitingFlatResource {

    private final Logger log = LoggerFactory.getLogger(VisitingFlatResource.class);

    private static final String ENTITY_NAME = "visitorServiceVisitingFlat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VisitingFlatRepository visitingFlatRepository;

    public VisitingFlatResource(VisitingFlatRepository visitingFlatRepository) {
        this.visitingFlatRepository = visitingFlatRepository;
    }

    /**
     * {@code POST  /visiting-flats} : Create a new visitingFlat.
     *
     * @param visitingFlat the visitingFlat to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new visitingFlat, or with status {@code 400 (Bad Request)} if the visitingFlat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/visiting-flats")
    public ResponseEntity<VisitingFlat> createVisitingFlat(@Valid @RequestBody VisitingFlat visitingFlat) throws URISyntaxException {
        log.debug("REST request to save VisitingFlat : {}", visitingFlat);
        if (visitingFlat.getId() != null) {
            throw new BadRequestAlertException("A new visitingFlat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VisitingFlat result = visitingFlatRepository.save(visitingFlat);
        return ResponseEntity
            .created(new URI("/api/visiting-flats/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /visiting-flats/:id} : Updates an existing visitingFlat.
     *
     * @param id the id of the visitingFlat to save.
     * @param visitingFlat the visitingFlat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visitingFlat,
     * or with status {@code 400 (Bad Request)} if the visitingFlat is not valid,
     * or with status {@code 500 (Internal Server Error)} if the visitingFlat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/visiting-flats/{id}")
    public ResponseEntity<VisitingFlat> updateVisitingFlat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VisitingFlat visitingFlat
    ) throws URISyntaxException {
        log.debug("REST request to update VisitingFlat : {}, {}", id, visitingFlat);
        if (visitingFlat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visitingFlat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!visitingFlatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        VisitingFlat result = visitingFlatRepository.save(visitingFlat);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, visitingFlat.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /visiting-flats/:id} : Partial updates given fields of an existing visitingFlat, field will ignore if it is null
     *
     * @param id the id of the visitingFlat to save.
     * @param visitingFlat the visitingFlat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visitingFlat,
     * or with status {@code 400 (Bad Request)} if the visitingFlat is not valid,
     * or with status {@code 404 (Not Found)} if the visitingFlat is not found,
     * or with status {@code 500 (Internal Server Error)} if the visitingFlat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/visiting-flats/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VisitingFlat> partialUpdateVisitingFlat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VisitingFlat visitingFlat
    ) throws URISyntaxException {
        log.debug("REST request to partial update VisitingFlat partially : {}, {}", id, visitingFlat);
        if (visitingFlat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visitingFlat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!visitingFlatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VisitingFlat> result = visitingFlatRepository
            .findById(visitingFlat.getId())
            .map(existingVisitingFlat -> {
                if (visitingFlat.getFlatNo() != null) {
                    existingVisitingFlat.setFlatNo(visitingFlat.getFlatNo());
                }

                return existingVisitingFlat;
            })
            .map(visitingFlatRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, visitingFlat.getId().toString())
        );
    }

    /**
     * {@code GET  /visiting-flats} : get all the visitingFlats.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of visitingFlats in body.
     */
    @GetMapping("/visiting-flats")
    public List<VisitingFlat> getAllVisitingFlats() {
        log.debug("REST request to get all VisitingFlats");
        return visitingFlatRepository.findAll();
    }

    /**
     * {@code GET  /visiting-flats/:id} : get the "id" visitingFlat.
     *
     * @param id the id of the visitingFlat to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the visitingFlat, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/visiting-flats/{id}")
    public ResponseEntity<VisitingFlat> getVisitingFlat(@PathVariable Long id) {
        log.debug("REST request to get VisitingFlat : {}", id);
        Optional<VisitingFlat> visitingFlat = visitingFlatRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(visitingFlat);
    }

    /**
     * {@code DELETE  /visiting-flats/:id} : delete the "id" visitingFlat.
     *
     * @param id the id of the visitingFlat to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/visiting-flats/{id}")
    public ResponseEntity<Void> deleteVisitingFlat(@PathVariable Long id) {
        log.debug("REST request to delete VisitingFlat : {}", id);
        visitingFlatRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

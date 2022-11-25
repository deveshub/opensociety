package com.bits.opensociety.web.rest;

import com.bits.opensociety.domain.Visitor;
import com.bits.opensociety.repository.VisitorRepository;
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
 * REST controller for managing {@link com.bits.opensociety.domain.Visitor}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class VisitorResource {

    private final Logger log = LoggerFactory.getLogger(VisitorResource.class);

    private static final String ENTITY_NAME = "visitorServiceVisitor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VisitorRepository visitorRepository;

    public VisitorResource(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    /**
     * {@code POST  /visitors} : Create a new visitor.
     *
     * @param visitor the visitor to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new visitor, or with status {@code 400 (Bad Request)} if the visitor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/visitors")
    public ResponseEntity<Visitor> createVisitor(@Valid @RequestBody Visitor visitor) throws URISyntaxException {
        log.debug("REST request to save Visitor : {}", visitor);
        if (visitor.getId() != null) {
            throw new BadRequestAlertException("A new visitor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Visitor result = visitorRepository.save(visitor);
        return ResponseEntity
            .created(new URI("/api/visitors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /visitors/:id} : Updates an existing visitor.
     *
     * @param id the id of the visitor to save.
     * @param visitor the visitor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visitor,
     * or with status {@code 400 (Bad Request)} if the visitor is not valid,
     * or with status {@code 500 (Internal Server Error)} if the visitor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/visitors/{id}")
    public ResponseEntity<Visitor> updateVisitor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Visitor visitor
    ) throws URISyntaxException {
        log.debug("REST request to update Visitor : {}, {}", id, visitor);
        if (visitor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visitor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!visitorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Visitor result = visitorRepository.save(visitor);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, visitor.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /visitors/:id} : Partial updates given fields of an existing visitor, field will ignore if it is null
     *
     * @param id the id of the visitor to save.
     * @param visitor the visitor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visitor,
     * or with status {@code 400 (Bad Request)} if the visitor is not valid,
     * or with status {@code 404 (Not Found)} if the visitor is not found,
     * or with status {@code 500 (Internal Server Error)} if the visitor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/visitors/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Visitor> partialUpdateVisitor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Visitor visitor
    ) throws URISyntaxException {
        log.debug("REST request to partial update Visitor partially : {}, {}", id, visitor);
        if (visitor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visitor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!visitorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Visitor> result = visitorRepository
            .findById(visitor.getId())
            .map(existingVisitor -> {
                if (visitor.getVisitorType() != null) {
                    existingVisitor.setVisitorType(visitor.getVisitorType());
                }
                if (visitor.getMobile() != null) {
                    existingVisitor.setMobile(visitor.getMobile());
                }
                if (visitor.getVehicleNumber() != null) {
                    existingVisitor.setVehicleNumber(visitor.getVehicleNumber());
                }
                if (visitor.getAddress() != null) {
                    existingVisitor.setAddress(visitor.getAddress());
                }
                if (visitor.getInTime() != null) {
                    existingVisitor.setInTime(visitor.getInTime());
                }
                if (visitor.getOutTime() != null) {
                    existingVisitor.setOutTime(visitor.getOutTime());
                }

                return existingVisitor;
            })
            .map(visitorRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, visitor.getId().toString())
        );
    }

    /**
     * {@code GET  /visitors} : get all the visitors.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of visitors in body.
     */
    @GetMapping("/visitors")
    public List<Visitor> getAllVisitors(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Visitors");
        return visitorRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /visitors/:id} : get the "id" visitor.
     *
     * @param id the id of the visitor to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the visitor, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/visitors/{id}")
    public ResponseEntity<Visitor> getVisitor(@PathVariable Long id) {
        log.debug("REST request to get Visitor : {}", id);
        Optional<Visitor> visitor = visitorRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(visitor);
    }

    /**
     * {@code DELETE  /visitors/:id} : delete the "id" visitor.
     *
     * @param id the id of the visitor to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/visitors/{id}")
    public ResponseEntity<Void> deleteVisitor(@PathVariable Long id) {
        log.debug("REST request to delete Visitor : {}", id);
        visitorRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

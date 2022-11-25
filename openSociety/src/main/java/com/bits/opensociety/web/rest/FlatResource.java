package com.bits.opensociety.web.rest;

import com.bits.opensociety.domain.Flat;
import com.bits.opensociety.repository.FlatRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.bits.opensociety.domain.Flat}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FlatResource {

    private final Logger log = LoggerFactory.getLogger(FlatResource.class);

    private static final String ENTITY_NAME = "flat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FlatRepository flatRepository;

    public FlatResource(FlatRepository flatRepository) {
        this.flatRepository = flatRepository;
    }

    /**
     * {@code POST  /flats} : Create a new flat.
     *
     * @param flat the flat to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new flat, or with status {@code 400 (Bad Request)} if the flat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/flats")
    public Mono<ResponseEntity<Flat>> createFlat(@Valid @RequestBody Flat flat) throws URISyntaxException {
        log.debug("REST request to save Flat : {}", flat);
        if (flat.getId() != null) {
            throw new BadRequestAlertException("A new flat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return flatRepository
            .save(flat)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/flats/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /flats/:id} : Updates an existing flat.
     *
     * @param id the id of the flat to save.
     * @param flat the flat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated flat,
     * or with status {@code 400 (Bad Request)} if the flat is not valid,
     * or with status {@code 500 (Internal Server Error)} if the flat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/flats/{id}")
    public Mono<ResponseEntity<Flat>> updateFlat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Flat flat
    ) throws URISyntaxException {
        log.debug("REST request to update Flat : {}, {}", id, flat);
        if (flat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, flat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return flatRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return flatRepository
                    .save(flat)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /flats/:id} : Partial updates given fields of an existing flat, field will ignore if it is null
     *
     * @param id the id of the flat to save.
     * @param flat the flat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated flat,
     * or with status {@code 400 (Bad Request)} if the flat is not valid,
     * or with status {@code 404 (Not Found)} if the flat is not found,
     * or with status {@code 500 (Internal Server Error)} if the flat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/flats/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Flat>> partialUpdateFlat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Flat flat
    ) throws URISyntaxException {
        log.debug("REST request to partial update Flat partially : {}, {}", id, flat);
        if (flat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, flat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return flatRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Flat> result = flatRepository
                    .findById(flat.getId())
                    .map(existingFlat -> {
                        if (flat.getFlatNo() != null) {
                            existingFlat.setFlatNo(flat.getFlatNo());
                        }
                        if (flat.getResidentialStatus() != null) {
                            existingFlat.setResidentialStatus(flat.getResidentialStatus());
                        }
                        if (flat.getFlatArea() != null) {
                            existingFlat.setFlatArea(flat.getFlatArea());
                        }

                        return existingFlat;
                    })
                    .flatMap(flatRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /flats} : get all the flats.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of flats in body.
     */
    @GetMapping("/flats")
    public Mono<List<Flat>> getAllFlats() {
        log.debug("REST request to get all Flats");
        return flatRepository.findAll().collectList();
    }

    /**
     * {@code GET  /flats} : get all the flats as a stream.
     * @return the {@link Flux} of flats.
     */
    @GetMapping(value = "/flats", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Flat> getAllFlatsAsStream() {
        log.debug("REST request to get all Flats as a stream");
        return flatRepository.findAll();
    }

    /**
     * {@code GET  /flats/:id} : get the "id" flat.
     *
     * @param id the id of the flat to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the flat, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/flats/{id}")
    public Mono<ResponseEntity<Flat>> getFlat(@PathVariable Long id) {
        log.debug("REST request to get Flat : {}", id);
        Mono<Flat> flat = flatRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(flat);
    }

    /**
     * {@code DELETE  /flats/:id} : delete the "id" flat.
     *
     * @param id the id of the flat to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/flats/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteFlat(@PathVariable Long id) {
        log.debug("REST request to delete Flat : {}", id);
        return flatRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}

package com.bits.opensociety.web.rest;

import com.bits.opensociety.domain.Society;
import com.bits.opensociety.repository.SocietyRepository;
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
 * REST controller for managing {@link com.bits.opensociety.domain.Society}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SocietyResource {

    private final Logger log = LoggerFactory.getLogger(SocietyResource.class);

    private static final String ENTITY_NAME = "society";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SocietyRepository societyRepository;

    public SocietyResource(SocietyRepository societyRepository) {
        this.societyRepository = societyRepository;
    }

    /**
     * {@code POST  /societies} : Create a new society.
     *
     * @param society the society to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new society, or with status {@code 400 (Bad Request)} if the society has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/societies")
    public Mono<ResponseEntity<Society>> createSociety(@Valid @RequestBody Society society) throws URISyntaxException {
        log.debug("REST request to save Society : {}", society);
        if (society.getId() != null) {
            throw new BadRequestAlertException("A new society cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return societyRepository
            .save(society)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/societies/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /societies/:id} : Updates an existing society.
     *
     * @param id the id of the society to save.
     * @param society the society to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated society,
     * or with status {@code 400 (Bad Request)} if the society is not valid,
     * or with status {@code 500 (Internal Server Error)} if the society couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/societies/{id}")
    public Mono<ResponseEntity<Society>> updateSociety(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Society society
    ) throws URISyntaxException {
        log.debug("REST request to update Society : {}, {}", id, society);
        if (society.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, society.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return societyRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return societyRepository
                    .save(society)
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
     * {@code PATCH  /societies/:id} : Partial updates given fields of an existing society, field will ignore if it is null
     *
     * @param id the id of the society to save.
     * @param society the society to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated society,
     * or with status {@code 400 (Bad Request)} if the society is not valid,
     * or with status {@code 404 (Not Found)} if the society is not found,
     * or with status {@code 500 (Internal Server Error)} if the society couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/societies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Society>> partialUpdateSociety(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Society society
    ) throws URISyntaxException {
        log.debug("REST request to partial update Society partially : {}, {}", id, society);
        if (society.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, society.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return societyRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Society> result = societyRepository
                    .findById(society.getId())
                    .map(existingSociety -> {
                        if (society.getName() != null) {
                            existingSociety.setName(society.getName());
                        }
                        if (society.getDescription() != null) {
                            existingSociety.setDescription(society.getDescription());
                        }

                        return existingSociety;
                    })
                    .flatMap(societyRepository::save);

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
     * {@code GET  /societies} : get all the societies.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of societies in body.
     */
    @GetMapping("/societies")
    public Mono<List<Society>> getAllSocieties() {
        log.debug("REST request to get all Societies");
        return societyRepository.findAll().collectList();
    }

    /**
     * {@code GET  /societies} : get all the societies as a stream.
     * @return the {@link Flux} of societies.
     */
    @GetMapping(value = "/societies", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Society> getAllSocietiesAsStream() {
        log.debug("REST request to get all Societies as a stream");
        return societyRepository.findAll();
    }

    /**
     * {@code GET  /societies/:id} : get the "id" society.
     *
     * @param id the id of the society to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the society, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/societies/{id}")
    public Mono<ResponseEntity<Society>> getSociety(@PathVariable Long id) {
        log.debug("REST request to get Society : {}", id);
        Mono<Society> society = societyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(society);
    }

    /**
     * {@code DELETE  /societies/:id} : delete the "id" society.
     *
     * @param id the id of the society to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/societies/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteSociety(@PathVariable Long id) {
        log.debug("REST request to delete Society : {}", id);
        return societyRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}

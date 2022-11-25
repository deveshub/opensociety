package com.bits.opensociety.repository;

import com.bits.opensociety.domain.Flat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Flat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FlatRepository extends ReactiveCrudRepository<Flat, Long>, FlatRepositoryInternal {
    @Query("SELECT * FROM flat entity WHERE entity.flat_id = :id")
    Flux<Flat> findByFlat(Long id);

    @Query("SELECT * FROM flat entity WHERE entity.flat_id IS NULL")
    Flux<Flat> findAllWhereFlatIsNull();

    @Override
    <S extends Flat> Mono<S> save(S entity);

    @Override
    Flux<Flat> findAll();

    @Override
    Mono<Flat> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface FlatRepositoryInternal {
    <S extends Flat> Mono<S> save(S entity);

    Flux<Flat> findAllBy(Pageable pageable);

    Flux<Flat> findAll();

    Mono<Flat> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Flat> findAllBy(Pageable pageable, Criteria criteria);

}

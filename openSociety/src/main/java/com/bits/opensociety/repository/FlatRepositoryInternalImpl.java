package com.bits.opensociety.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.bits.opensociety.domain.Flat;
import com.bits.opensociety.domain.enumeration.ResidentialStatus;
import com.bits.opensociety.repository.rowmapper.FlatRowMapper;
import com.bits.opensociety.repository.rowmapper.SocietyRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Flat entity.
 */
@SuppressWarnings("unused")
class FlatRepositoryInternalImpl extends SimpleR2dbcRepository<Flat, Long> implements FlatRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final SocietyRowMapper societyMapper;
    private final FlatRowMapper flatMapper;

    private static final Table entityTable = Table.aliased("flat", EntityManager.ENTITY_ALIAS);
    private static final Table flatTable = Table.aliased("society", "flat");

    public FlatRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        SocietyRowMapper societyMapper,
        FlatRowMapper flatMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Flat.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.societyMapper = societyMapper;
        this.flatMapper = flatMapper;
    }

    @Override
    public Flux<Flat> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Flat> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = FlatSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(SocietySqlHelper.getColumns(flatTable, "flat"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(flatTable)
            .on(Column.create("flat_id", entityTable))
            .equals(Column.create("id", flatTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Flat.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Flat> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Flat> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Flat process(Row row, RowMetadata metadata) {
        Flat entity = flatMapper.apply(row, "e");
        entity.setFlat(societyMapper.apply(row, "flat"));
        return entity;
    }

    @Override
    public <S extends Flat> Mono<S> save(S entity) {
        return super.save(entity);
    }
}

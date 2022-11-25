package com.bits.opensociety.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.bits.opensociety.domain.Member;
import com.bits.opensociety.domain.enumeration.MemberType;
import com.bits.opensociety.repository.rowmapper.FlatRowMapper;
import com.bits.opensociety.repository.rowmapper.MemberRowMapper;
import com.bits.opensociety.repository.rowmapper.UserRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Member entity.
 */
@SuppressWarnings("unused")
class MemberRepositoryInternalImpl extends SimpleR2dbcRepository<Member, Long> implements MemberRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final FlatRowMapper flatMapper;
    private final MemberRowMapper memberMapper;

    private static final Table entityTable = Table.aliased("member", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("bits_user", "e_user");
    private static final Table flatTable = Table.aliased("flat", "flat");

    public MemberRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        FlatRowMapper flatMapper,
        MemberRowMapper memberMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Member.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.flatMapper = flatMapper;
        this.memberMapper = memberMapper;
    }

    @Override
    public Flux<Member> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Member> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MemberSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(FlatSqlHelper.getColumns(flatTable, "flat"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(flatTable)
            .on(Column.create("flat_id", entityTable))
            .equals(Column.create("id", flatTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Member.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Member> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Member> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Member> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Member> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Member> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Member process(Row row, RowMetadata metadata) {
        Member entity = memberMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setFlat(flatMapper.apply(row, "flat"));
        return entity;
    }

    @Override
    public <S extends Member> Mono<S> save(S entity) {
        return super.save(entity);
    }
}

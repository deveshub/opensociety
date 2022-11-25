package com.bits.opensociety.repository.rowmapper;

import com.bits.opensociety.domain.Member;
import com.bits.opensociety.domain.enumeration.MemberType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Member}, with proper type conversions.
 */
@Service
public class MemberRowMapper implements BiFunction<Row, String, Member> {

    private final ColumnConverter converter;

    public MemberRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Member} stored in the database.
     */
    @Override
    public Member apply(Row row, String prefix) {
        Member entity = new Member();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setMobile(converter.fromRow(row, prefix + "_mobile", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setMemberType(converter.fromRow(row, prefix + "_member_type", MemberType.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setFlatId(converter.fromRow(row, prefix + "_flat_id", Long.class));
        return entity;
    }
}

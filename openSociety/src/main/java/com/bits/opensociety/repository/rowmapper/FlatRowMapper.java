package com.bits.opensociety.repository.rowmapper;

import com.bits.opensociety.domain.Flat;
import com.bits.opensociety.domain.enumeration.ResidentialStatus;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Flat}, with proper type conversions.
 */
@Service
public class FlatRowMapper implements BiFunction<Row, String, Flat> {

    private final ColumnConverter converter;

    public FlatRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Flat} stored in the database.
     */
    @Override
    public Flat apply(Row row, String prefix) {
        Flat entity = new Flat();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFlatNo(converter.fromRow(row, prefix + "_flat_no", String.class));
        entity.setResidentialStatus(converter.fromRow(row, prefix + "_residential_status", ResidentialStatus.class));
        entity.setFlatArea(converter.fromRow(row, prefix + "_flat_area", Integer.class));
        entity.setFlatId(converter.fromRow(row, prefix + "_flat_id", Long.class));
        return entity;
    }
}

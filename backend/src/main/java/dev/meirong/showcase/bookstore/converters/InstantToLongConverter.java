package dev.meirong.showcase.bookstore.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;

/**
 * JPA Converter for converting between Instant and Unix Timestamp (milliseconds).
 * This converter allows storing timestamps as BIGINT in the database while
 * using Instant in Java entities.
 *
 * Benefits:
 * - Database independence (works with any SQL database)
 * - Microservice friendly (Unix timestamps are language-agnostic)
 * - Easy serialization for REST APIs
 * - Simple time calculations
 *
 * @author meirong
 */
@Converter(autoApply = false)
public class InstantToLongConverter implements AttributeConverter<Instant, Long> {

    /**
     * Converts Instant to Unix timestamp (milliseconds) for database storage.
     *
     * @param instant the Instant value to convert
     * @return Unix timestamp in milliseconds, or null if instant is null
     */
    @Override
    public Long convertToDatabaseColumn(Instant instant) {
        return instant == null ? null : instant.toEpochMilli();
    }

    /**
     * Converts Unix timestamp (milliseconds) from database to Instant.
     *
     * @param dbData the Unix timestamp in milliseconds
     * @return Instant representation, or null if dbData is null
     */
    @Override
    public Instant convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : Instant.ofEpochMilli(dbData);
    }
}

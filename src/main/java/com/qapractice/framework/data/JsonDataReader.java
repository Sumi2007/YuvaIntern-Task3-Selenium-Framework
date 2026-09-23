package com.qapractice.framework.data;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * Loads JSON test data from the classpath (src/test/resources/testdata/...) into typed objects.
 * Test code never touches file paths or parsing - it just asks for a type.
 */
public final class JsonDataReader {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private JsonDataReader() {
    }

    public static <T> T read(String resourcePath, Class<T> type) {
        try (InputStream in = open(resourcePath)) {
            return MAPPER.readValue(in, type);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not parse test data file " + resourcePath, e);
        }
    }

    public static <T> List<T> readList(String resourcePath, Class<T> elementType) {
        try (InputStream in = open(resourcePath)) {
            return MAPPER.readValue(in, MAPPER.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not parse test data list " + resourcePath, e);
        }
    }

    private static InputStream open(String resourcePath) {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("Test data file not found on classpath: " + resourcePath);
        }
        return in;
    }
}

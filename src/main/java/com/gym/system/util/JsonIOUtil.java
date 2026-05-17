package com.gym.system.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;

public class JsonIOUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);

    /** Serializa qualquer objeto -> byte[] (JSON) */
    public static byte[] toByteArray(Object obj) {
        try {
            return MAPPER.writeValueAsBytes(obj);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao serializar para JSON (Jackson)", e);
        }
    }

    /** Deserializa byte[] (JSON) -> objeto tipado */
    public static <T> T fromByteArray(byte[] data, Class<T> clazz) {
        try {
            return MAPPER.readValue(data, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao desserializar JSON para " + clazz.getName(), e);
        }
    }

    /** Expõe o mapper para casos avançados (ex: manipulação de JsonNode) */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
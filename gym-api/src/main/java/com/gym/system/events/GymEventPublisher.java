package com.gym.system.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.system.api.config.RedisStreamConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GymEventPublisher {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishEvent(Object event) {
        try {
            String eventType = event.getClass().getSimpleName();
            String payload = objectMapper.writeValueAsString(event);

            Map<String, String> streamPayload = new HashMap<>();
            streamPayload.put("eventType", eventType);
            streamPayload.put("payload", payload);

            MapRecord<String, String, String> record = StreamRecords.string(streamPayload)
                    .withStreamKey(RedisStreamConfig.GYM_EVENTS_STREAM);

            RecordId recordId = stringRedisTemplate.opsForStream().add(record);
            System.out.println("[PUBLISHER] Evento publicado no Stream: " + eventType + " | RecordId: " + recordId);
        } catch (Exception e) {
            System.err.println("Erro ao publicar evento: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
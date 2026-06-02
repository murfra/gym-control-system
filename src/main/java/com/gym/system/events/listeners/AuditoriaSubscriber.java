package com.gym.system.events.listeners;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaSubscriber {
    public void process(MapRecord<String, String, String> message) {
        String eventType = message.getValue().get("eventType");
        String payload = message.getValue().get("payload");
        String recordId = message.getId().getValue();

        System.out.println("[AUDITORIA] Logando no banco de auditoria -> Evento: " + eventType + " | Payload: " + payload + " | ID: " + recordId);
    }
}
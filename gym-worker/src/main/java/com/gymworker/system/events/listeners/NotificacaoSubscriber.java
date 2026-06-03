package com.gymworker.system.events.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymworker.system.events.AlunoCadastradoEvent;
import com.gymworker.system.events.TreinoCriadoEvent;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoSubscriber {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void process(MapRecord<String, String, String> message) {
        String eventType = message.getValue().get("eventType");
        String payload = message.getValue().get("payload");
        String recordId = message.getId().getValue();

        try {
            System.out.println("[NOTIFICAÇÃO] Recebendo evento: " + eventType + " | ID: " + recordId);

            if ("AlunoCadastradoEvent".equals(eventType)) {
                AlunoCadastradoEvent event = objectMapper.readValue(payload, AlunoCadastradoEvent.class);
                System.out.println("Enviando e-mail de boas-vindas para: " + event.getEmail() + " | Mensagem: " + event.toString());
            }
            else if ("TreinoCriadoEvent".equals(eventType)) {
                TreinoCriadoEvent event = objectMapper.readValue(payload, TreinoCriadoEvent.class);
                System.out.println("Enviando Push Notification para o aluno " + event.getMatricula() + " | Mensagem: " + event.toString());
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar notificação: " + e.getMessage());
        }
    }
}
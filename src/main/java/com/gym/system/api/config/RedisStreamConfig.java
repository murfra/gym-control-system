package com.gym.system.api.config;

import com.gym.system.events.listeners.NotificacaoSubscriber;
import com.gym.system.events.listeners.AuditoriaSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;

@Configuration
public class RedisStreamConfig {

    public static final String GYM_EVENTS_STREAM = "gym-events-stream";

    @Bean(initMethod = "start", destroyMethod = "stop")
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer(
            RedisConnectionFactory connectionFactory,
            NotificacaoSubscriber notificacaoSubscriber,
            AuditoriaSubscriber auditoriaSubscriber) {

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .pollTimeout(Duration.ofMillis(100))
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        StreamOffset<String> offset = StreamOffset.create(GYM_EVENTS_STREAM, ReadOffset.from("0"));

        container.receive(offset, notificacaoSubscriber::process);
        container.receive(offset, auditoriaSubscriber::process);

        return container;
    }
}
package com.perfectial.geotrack.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfectial.geotrack.broker.TrackPointPublisher;
import com.perfectial.geotrack.gpx.TrackSIM7000;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class WebSocketConfiguration {

    @Bean
    Executor executor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    HandlerMapping handlerMapping(WebSocketHandler wsh) {
        return new SimpleUrlHandlerMapping() {
            {
                setUrlMap(Collections.singletonMap("/ws/trackpoint", wsh));
                setOrder(10);
            }
        };
    }

    @Bean
    WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    WebSocketHandler webSocketHandler(
            ObjectMapper objectMapper,
            TrackPointPublisher eventPublisher
    ) {

        Flux<TrackSIM7000> publish = Flux
                .create(eventPublisher)
                .share();

        return session -> {

            Flux<WebSocketMessage> messageFlux = publish
                    .map(tp -> {
                        try {
                            String json = objectMapper.writeValueAsString(tp);
                            log.info("sending " + json);
                            return session.textMessage(json);
                        }
                        catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });

            return session.send(messageFlux);
        };
    }
}

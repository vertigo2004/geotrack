package com.perfectial.geotrack.controller;

import com.perfectial.geotrack.broker.SSEPublisher;
import com.perfectial.geotrack.gpx.TrackSIM7000;
import lombok.extern.slf4j.Slf4j;


import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("v1/sse")
public class SSEController {
//    private ObjectMapper objectMapper;
    private Flux<TrackSIM7000> publish;

    public SSEController(// ObjectMapper objectMapper,
                         SSEPublisher eventPublisher) {
//        this.objectMapper = objectMapper;
        this.publish = Flux.create(eventPublisher).share();
    }

    @GetMapping("trackme")
    public Flux<ServerSentEvent<TrackSIM7000>> handle() {

        Flux<ServerSentEvent<TrackSIM7000>> messageFlux = publish
                .map(tp -> {
                        log.info("sending: " + tp);
                        return ServerSentEvent.<TrackSIM7000> builder()
                                .id(UUID.randomUUID().toString())
                                .event("periodic-event")
                                .data(tp)
                                .build();
                });

        return messageFlux;
    }
}

package com.perfectial.geotrack.controller;

import com.perfectial.geotrack.broker.SSEPublisher;
import com.perfectial.geotrack.gpx.TrackSIM7000;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("v1/sse")
@EnableAsync
public class SSEController {
    private Flux<TrackSIM7000> publish;

    public SSEController(SSEPublisher eventPublisher) {
        this.publish = Flux.create(eventPublisher).share();
    }

    @GetMapping("trackme")
    public Flux<ServerSentEvent<TrackSIM7000>> handle() {

        Flux<ServerSentEvent<TrackSIM7000>> messageFlux = publish
                .map(tp -> {
                    String id = UUID.randomUUID().toString();
                        log.info("Sending SSE - > ID: {}, Data: {} ", id, tp);
                        return ServerSentEvent.<TrackSIM7000> builder()
                                .id(id)
                                .event("thing-event")
                                .data(tp)
                                .build();
                });

        return messageFlux;
    }
}

package com.perfectial.geotrack.controller;

import com.perfectial.geotrack.broker.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController()
@RequestMapping("/v1/online")
public class GeoRest {

    @Autowired
    Subscriber subscriber;



}

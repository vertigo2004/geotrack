package com.perfectial.geotrack.configuration;

import com.perfectial.geotrack.broker.SSEPublisher;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class BrokerBean {

    @Value("${mosquitto.host}")
    private String host;
    @Value("${mosquitto.port}")
    private String port;
    @Value("${mosquitto.username}")
    private String username;
    @Value("${mosquitto.password}")
    private String password;
    @Value("${mosquitto.topic}")
    private String topic;
    @Value("${trackpoint.limit}")
    private int trackpointLimit;
    @Value("#{'tcp://${mosquitto.host}:${mosquitto.port}'}") String uri;

    @Bean
    public MqttConnectOptions getMqttConnectOptions() {
        log.info("MqttConnectOptions URI: {}", uri);
        MqttConnectOptions conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(true);
        conOpt.setUserName(username);
        conOpt.setPassword(password.toCharArray());
        log.info("MqttConnectOptions: {}",conOpt);
        return conOpt;
    }

    @Bean
    public SSEPublisher getSSEPublisher() throws MqttException {
        return new SSEPublisher(uri, getMqttConnectOptions(), topic, trackpointLimit);
    }

}

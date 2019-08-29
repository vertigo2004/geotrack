package com.perfectial.geotrack.configuration;

import com.perfectial.geotrack.broker.SSEPublisher;
import com.perfectial.geotrack.broker.Subscriber;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@PropertySource(value= {"classpath:broker.properties"})
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
    @Value("${trackpoint.count}")
    private int trackpointCount;
    @Value("#{'tcp://${mosquitto.host}:${mosquitto.port}'}") String uri;

    @Bean
    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(true);
        conOpt.setUserName(username);
        conOpt.setPassword(password.toCharArray());
        return conOpt;
    }

    @Bean
    public Subscriber getSubscriber() throws MqttException {
        return new Subscriber(uri, getMqttConnectOptions(), topic, trackpointCount);
    }

    @Bean
    public SSEPublisher getSSEPublisher() throws MqttException {
        return new SSEPublisher(uri, getMqttConnectOptions(), topic, trackpointCount, getExecutor());
    }

    @Bean
    public Executor getExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}

package com.perfectial.geotrack.configuration;

import com.perfectial.geotrack.broker.Subscriber;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

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
        return new Subscriber(host, port, username, password, topic, trackpointCount);
    }
}

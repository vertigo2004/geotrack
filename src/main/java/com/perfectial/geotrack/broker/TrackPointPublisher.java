package com.perfectial.geotrack.broker;

import com.perfectial.geotrack.gpx.TrackSIM7000;
import com.perfectial.geotrack.utils.BoundedQueue;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Slf4j
@Component
public class TrackPointPublisher implements MqttCallback, Consumer<FluxSink<TrackSIM7000>> {

    private final int QOS = 1;
    private final String SCHEMA = "tcp";
    String CLIENTID = "MQTT-Java-Example";

    private final String topic;
    private final BlockingQueue<TrackSIM7000> trackPoints;

    private MqttClient client;

    private Executor executor;

    public TrackPointPublisher(MqttConnectOptions conOpt,
                               @Value("#{tcp://${mosquitto.host}:${mosquitto.port}") String uri,
                               @Value("${mosquitto.topic}") String topic,
                               @Value("${trackpoint.count}") int trackpointsLimit,
                               Executor executor
    ) throws MqttException {
        this.topic = topic;
        this.executor = executor;
        this.trackPoints = new BoundedQueue<>(trackpointsLimit);

        this.client = new MqttClient(uri, CLIENTID, new MemoryPersistence());
        this.client.setCallback(this);
        this.client.connect(conOpt);

        this.client.subscribe(topic, QOS);
        log.info("Subscribed to {}", topic);
    }

    @Override
    public void accept(FluxSink<TrackSIM7000> sink) {
        this.executor.execute(() -> {
            while (true) {
                try {
                    TrackSIM7000 event = trackPoints.take();
                    sink.next(event);
                }
                catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
            }
        });
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.error("Connection Lost", throwable);
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        String payload = new String(mqttMessage.getPayload());
        log.info("Topic: {}. Message received: {}", this.topic, payload);
        trackPoints.add(TrackSIM7000.fromCsv(payload));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    }
}

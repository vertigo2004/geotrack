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
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.text.SimpleDateFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
public class SSEPublisher implements MqttCallback, Consumer<FluxSink<TrackSIM7000>> {

    private final int QOS = 1;
    String CLIENTID = "SSE";

    private final String topic;
    private final BlockingQueue<TrackSIM7000> trackPoints;

    private MqttClient client;

    public static String DATE_TIME_FORMAT = "yy/MM/dd,hh:mm:ss";
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);

    public SSEPublisher(String uri,
                        MqttConnectOptions conOpt,
                        String topic,
                        int trackpointLimit
    ) throws MqttException {

        this.topic = topic;
        this.trackPoints = new BoundedQueue<>(trackpointLimit);

        this.client = new MqttClient(uri, CLIENTID, new MemoryPersistence());
        this.client.setCallback(this);
        this.client.connect(conOpt);

        this.client.subscribe(topic, QOS);
        log.info("Subscribed to {}", topic);
    }

    @Override
    public void accept(FluxSink<TrackSIM7000> sink) {
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
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
        log.info("Message Arrived: {}", payload);
        trackPoints.add(TrackSIM7000.fromCsv(payload, sdf));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    }
}

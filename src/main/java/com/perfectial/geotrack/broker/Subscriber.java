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

import java.util.Queue;

@Slf4j
public class Subscriber implements MqttCallback {

    private final int QOS = 1;
    private final String SCHEMA = "tcp";
    String CLIENTID = "MQTT-Java-Example";

    private final String host;
    private final String port;
    private final String username;
    private final String password;
    private final String topic;
    private final Queue<TrackSIM7000> trackPoints;
    private TrackSIM7000 lastPoint;

    private MqttClient client;

    public Subscriber(String host, String port, String username, String password, String topic, int trackpointsCount
    ) throws MqttException {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.topic = topic;
        this.trackPoints = new BoundedQueue<>(trackpointsCount);

        log.info("Host: {}, Port: {}, UserName: {}, Password: {}", host, port, username, password);
        MqttConnectOptions conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(true);
        conOpt.setUserName(username);
        conOpt.setPassword(password.toCharArray());
        String uri = String.format("%s://%s:%s", SCHEMA, host, port);
        this.client = new MqttClient(uri, CLIENTID, new MemoryPersistence());
        this.client.setCallback(this);
        this.client.connect(conOpt);

        this.client.subscribe(topic, QOS);
        log.info("Subscribed to {}", topic);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.error("Connection Lost", throwable);
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        String payload = new String(mqttMessage.getPayload());
        log.info("Topic: {}. Message: {}", this.topic, payload);
        this.lastPoint = TrackSIM7000.fromCsv(payload);
        trackPoints.add(this.lastPoint);

        log.debug("Track points in the queue: {}", trackPoints.size());
        log.debug(getTrack());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    }

    public String getTrack() {
        StringBuilder sb = new StringBuilder("<gpx><trk><trkseg>");
        for (TrackSIM7000 trackPoint : trackPoints) {
            sb.append(trackPoint.getXML());
        }
        sb.append("</trkseg></trk>");

        if (this.lastPoint != null) {
            sb.append(lastPoint.getXML().replace("trkpt", "wpt"));
        }
        sb.append("</gpx>");
        return sb.toString();
    }

    public void clearQueue() {
        this.trackPoints.clear();
        this.lastPoint = null;
    }
}

package com.perfectial.geotrack.broker;

import com.perfectial.geotrack.gpx.GpxContentHandler;
import com.perfectial.geotrack.gpx.GpxParser;
import com.perfectial.geotrack.gpx.TrackPoint;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
public class GpxPublisher {

    private static String FILENAME = "static/gpx/2019-07-15.gpx";

    private static String CLIENTID = "MQTT-Gpx-Publisher";
    private static String CSV_FORMAT = "%s,%s,,,,,,,,";

    private static String HOST = "ec2-34-209-191-182.us-west-2.compute.amazonaws.com";
    private static String PORT = "1883";
    private static String USERNAME = "perfectial";
    private static String PASSWORD = "123Perfectial456";
    private static String TOPIC = "localgateway_to_awsiot";

    private IMqttClient client;

    private GpxParser gpxParser = new GpxParser();


    public GpxPublisher() throws MqttException {
        client = new MqttClient(String.format("tcp://%s:%s", HOST, PORT), CLIENTID);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        client.connect(options);
    }

    private List<List<TrackPoint>> openGpx(String filename) throws IOException {

        final GpxContentHandler gch = new GpxContentHandler();

        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File(filename)));
        gpxParser.parseGpx(inputStream, gch);
        inputStream.close();
        List<List<TrackPoint>> segments = gch.getSegments();
        log.info("Segments in total: {}", segments.size());

        return gch.getSegments();
    }

    public void processTrack(String filename) throws Exception {

        List<List<TrackPoint>> track = openGpx(filename);
        TrackPoint prev = null;
        for (List<TrackPoint> trackPoints : track) {
            for (TrackPoint point : trackPoints) {
                if (prev != null) {
                    long toWait = System.currentTimeMillis() + (point.getTime() - prev.getTime());
                    while (System.currentTimeMillis() < toWait) {
                        // wait
                    }
                }
                publish(point);
                prev = point;
            }
        }
    }

    private void publish(TrackPoint trackPoint) throws Exception {
        if ( !client.isConnected()) {
            log.error("MQTT client is not connected.");
            throw new Exception("MQTT client is not connected.");
        }
        String payload = String.format(CSV_FORMAT, trackPoint.getLon(), trackPoint.getLat());
        MqttMessage msg = new MqttMessage(payload.getBytes());
        msg.setQos(0);
        msg.setRetained(true);

        log.info("Published: {}", payload);
        client.publish(TOPIC, msg);
    }

    public static void main(String[] args) throws Exception {
        ClassLoader loader = GpxPublisher.class.getClassLoader();
        String file = loader.getResource(FILENAME).getFile();
        log.info(file);
        GpxPublisher publisher = new GpxPublisher();
        publisher.processTrack(file);
    }
}

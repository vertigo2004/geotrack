package com.perfectial.geotrack.gpx;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class TrackSIM7000 extends TrackPoint {

    private Double bmeTemperature;
    private Double bmeHumidity;
    private String bmePressure;
    private Double alsIlluminance;
    private String alsDirectSunLight;
    private String shockDetected;
    private String batteryPower;
    private String signalStrength;

    public static TrackSIM7000 fromCsv(String csv) {
        String[] strings = csv.split(",");
        TrackSIM7000 point = new TrackSIM7000();
        if (!StringUtils.isEmpty(strings[0])) {
            point.setLon(Double.valueOf(strings[0]));
        }
        if (!StringUtils.isEmpty(strings[1])) {
            point.setLat(Double.valueOf(strings[1]));
        }
        if (!StringUtils.isEmpty(strings[2])) {
            point.bmeTemperature = Double.valueOf(strings[2]);
        }
        if (!StringUtils.isEmpty(strings[3])) {
            point.bmeHumidity = Double.valueOf(strings[3]);
        }
        if (!StringUtils.isEmpty(strings[4])) {
            point.bmePressure = strings[4];
        }
        if (!StringUtils.isEmpty(strings[5])) {
            point.alsIlluminance = Double.valueOf(strings[5]);
        }
        if (!StringUtils.isEmpty(strings[6])) {
            point.alsDirectSunLight = strings[6];
        }
        if (!StringUtils.isEmpty(strings[7])) {
            point.shockDetected = strings[7];
        }
        if (!StringUtils.isEmpty(strings[8])) {
            point.batteryPower = strings[8];
        }
        if (!StringUtils.isEmpty(strings[9])) {
            point.signalStrength = strings[9];
        }

        return point;
    }

    protected void getExtensions(StringBuilder sb) {
        StringBuilder ext = new StringBuilder();
        if (bmeTemperature != null) {
            ext.append("<bmeTemperature>").append(bmeTemperature).append("</bmeTemperature>");
        }
        if (bmeHumidity != null) {
            ext.append("<bmeHumidity>").append(bmeHumidity).append("</bmeHumidity>");
        }
        if (bmePressure != null) {
            ext.append("<bmePressure>").append(bmePressure).append("</bmePressure>");
        }
        if (alsIlluminance != null) {
            ext.append("<alsIlluminance>").append(alsIlluminance).append("</alsIlluminance>");
        }
        if (alsDirectSunLight != null) {
            ext.append("<alsDirectSunLight>").append(alsDirectSunLight).append("</alsDirectSunLight>");
        }
        if (shockDetected != null) {
            ext.append("<shockDetected>").append(shockDetected).append("</shockDetected>");
        }
        if (batteryPower != null) {
            ext.append("<batteryPower>").append(batteryPower).append("</batteryPower>");
        }
        if (signalStrength != null) {
            ext.append("<signalStrength>").append(signalStrength).append("</signalStrength>");
        }

        if (ext.length() > 0) {
            sb.append("<extensions>").append(ext).append("</extensions>");
        }
    }

}

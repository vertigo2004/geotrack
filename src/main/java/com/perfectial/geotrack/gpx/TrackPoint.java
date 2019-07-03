package com.perfectial.geotrack.gpx;

import lombok.Data;

@Data
public class TrackPoint {

        private final double lat;
        private final double lon;
        private final long time;
        private final double elevation;
}

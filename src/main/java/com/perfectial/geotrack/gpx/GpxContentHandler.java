package com.perfectial.geotrack.gpx;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class GpxContentHandler extends DefaultHandler {

    private static final String ATTR_LON = "lon";
    private static final String ATTR_LAT = "lat";
    private static final String ELEM_TRKSEG = "trkseg";
    private static final String ELEM_TRKPT = "trkpt";
    private static final String ELEM_TIME = "time";
    private static final String ELEM_ELE = "ele";

    private final List<List<TrackPoint>> segments = new ArrayList<>();
    private List<TrackPoint> trackSegment;

    private StringBuilder sb;
    private long time = Long.MIN_VALUE;
    private double lat;
    private double lon;
    private double ele;

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if (ELEM_TRKSEG.equals(qName)) {
            trackSegment = new ArrayList<>();
        } else if (ELEM_TRKPT.equals(qName)) {
            lat = Double.parseDouble(attributes.getValue(ATTR_LAT));
            lon = Double.parseDouble(attributes.getValue(ATTR_LON));
        } else if (ELEM_TIME.equals(qName) || ELEM_ELE.equals(qName) ) {
            sb = new StringBuilder();
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (sb != null) {
            sb.append(ch, start, length);
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (ELEM_TRKSEG.equals(qName)) {
            segments.add(trackSegment);
            trackSegment = null;
        } else if (ELEM_TRKPT.equals(qName)) {
            trackSegment.add(new TrackPoint(lat, lon, time, ele));
            time = Long.MIN_VALUE;
        } else if (ELEM_TIME.equals(qName)) {
            final ZonedDateTime dateTime = ZonedDateTime.parse(sb.toString());
            time = dateTime.toInstant().toEpochMilli();
            sb = null;
        } else if (ELEM_ELE.equals(qName)) {
            ele = Double.parseDouble(sb.toString());
            sb = null;
        }
    }

    public List<List<TrackPoint>> getSegments() {
        return segments;
    }
}

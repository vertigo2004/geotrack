package com.perfectial.geotrack.gpx;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackPoint {

    private Double lat;
    private Double lon;
    private Long time;
    private Double elevation;

    @JsonIgnore
    public String getXML() {
        StringBuilder sb = new StringBuilder();

        sb.append("<trkpt");
        if (lat != null) {
            sb.append(" lat =\"").append(lat).append("\"");
        }
        if (lon != null) {
            sb.append(" lon =\"").append(lon).append("\"");
        }
        sb.append(">");

        if (time != null) {
            sb.append("<time>").append(time).append("</time>");
        }
        if (elevation!= null) {
            sb.append("<ele>").append(elevation).append("</ele>");
        }

        getExtensions(sb);

        sb.append("</trkpt>");

        return sb.toString();
    }

    @JsonIgnore
    protected void getExtensions(StringBuilder sb) {
    }
}

package com.perfectial.geotrack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfectial.geotrack.gpx.GpxContentHandler;
import com.perfectial.geotrack.gpx.GpxException;
import com.perfectial.geotrack.gpx.GpxParser;
import com.perfectial.geotrack.gpx.TrackPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@Slf4j
public class GeoController {

    @Autowired
    GpxParser gpxParser;

    @PostMapping("gpx-load")
    public String processGPX(@RequestParam("file") MultipartFile file, Model model) {

        log.info("ContentType: {}", file.getContentType());

        log.info("Name: {}", file.getName());
        log.info("OriginalFilename: {}", file.getOriginalFilename());

        final GpxContentHandler gch = new GpxContentHandler();

        gpxParser.parseGpx(file, gch);
        if (gch.getSegments().size() > 0) {
            List<TrackPoint> track = gch.getSegments().get(0);
            int trackSize = track.size();

            log.info("Track points red: {}", trackSize);
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                String json = objectMapper.writeValueAsString(track);
                log.info(json);
                model.addAttribute("lonLat", json);
            } catch (JsonProcessingException e) {
                throw new GpxException("Error parsing GPX to JSON");
            }
        }
        model.addAttribute("message", "File " + file.getOriginalFilename() + " is loaded!");
        return "map";
    }
}

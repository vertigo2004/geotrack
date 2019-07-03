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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
public class GeoController {

    @Autowired
    GpxParser gpxParser;

    @GetMapping("/")
    public String showMap(Model model)  throws IOException {

        return "map";
    }

    @PostMapping("/")
    public String processGPX(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        log.info("ContentType: {}", file.getContentType());

        log.info("Name: {}", file.getName());
        log.info("OriginalFilename: {}", file.getOriginalFilename());
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        final GpxContentHandler gch = new GpxContentHandler();

        gpxParser.parseGpx(file, gch);
        List<List<TrackPoint>> segments = gch.getSegments();

        if (segments.size() > 0) {

            log.info("Segments in total: {}", segments.size());
            log.info("TrackPoints in the first segment: {}", segments.get(0).size());
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                String json = objectMapper.writeValueAsString(segments);
                redirectAttributes.addFlashAttribute("segments", json);
            } catch (JsonProcessingException e) {
                throw new GpxException("Error parsing GPX to JSON");
            }
        }

        return "redirect:/";
    }
}

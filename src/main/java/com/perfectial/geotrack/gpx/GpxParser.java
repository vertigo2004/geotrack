package com.perfectial.geotrack.gpx;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

@Component
public class GpxParser {

    public void parseGpx(final MultipartFile file, final GpxContentHandler dh) {

        final SAXParser saxParser;
        try {
            saxParser = SAXParserFactory.newInstance().newSAXParser();
        } catch (final ParserConfigurationException | SAXException e) {
            throw new GpxException("can't create XML parser", e);
        }
        try (InputStream inputGpx = file.getInputStream()) {
            saxParser.parse(inputGpx, dh);
        } catch (final SAXException e) {
            throw new GpxException("error parsing input GPX file", e);
        } catch (final RuntimeException e) {
            throw new GpxException("internal error when parsing GPX file", e);
        } catch (final IOException e) {
            throw new GpxException("error reading input file", e);
        }
    }
}

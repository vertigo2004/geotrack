package com.perfectial.geotrack.gpx;

public class GpxException extends RuntimeException {

        private static final long serialVersionUID = -1L;

        public GpxException(final String message, final Throwable cause) {
            super(message, cause);
        }

        public GpxException(final String message) {
            super(message);
        }

}

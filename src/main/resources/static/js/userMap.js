// var fromProjection = new ol.Projection("EPSG:4326");   // Transform from WGS 1984 //HERE IS THE DATA SOURCE PROJECTION
// var toProjection = new ol.Projection("EPSG:900913"); // to Spherical Mercator Projection //HERE IS THE VIEW PROJECTION

drawTrack(segments);

function getNewMinMax(mm, trackPoint) {
    if (trackPoint.lat < mm.minLat) {
        mm.minLat = trackPoint.lat;
    }
    if (trackPoint.lat > mm.maxLat) {
        mm.maxLat = trackPoint.lat;
    }

    if (trackPoint.lon < mm.minLon) {
        mm.minLon = trackPoint.lon;
    }
    if (trackPoint.lon > mm.maxLon) {
        mm.maxLon = trackPoint.lon;
    }
    return mm;
}

function drawTrack(segments) {
    console.log("drawing Tracks");

    var lat = 48.91962;
    var lon = 24.71187;
    var zoom = 15;
    var trackIndexStep = 10;

    var raster = new ol.layer.Tile({
        projection: 'EPSG:4326',
        source: new ol.source.OSM()
    });

    // new vector graphic layer
    var vector = new ol.layer.Vector();

    var style = {
        strokeColor: "#FF0000",
        strokeWidth: 5,
    };

    var minMax = {
        maxLat: segments[0][0].lat,
        minLat: segments[0][0].lat,
        maxLon: segments[0][0].lon,
        minLon: segments[0][0].lon
    };

    console.log(minMax);

    segments.forEach(function (track) {

        // Start and end point
        var start_point = ol.proj.transform([track[0].lon, track[0].lat], 'EPSG:4326', 'EPSG:3857');
        var end_point;

        for (var i = trackIndexStep; i < track.length; i += trackIndexStep) {
            minMax = getNewMinMax(minMax, track[i]);
            end_point = ol.proj.transform([track[i].lon, track[i].lat], 'EPSG:4326', 'EPSG:3857');
            // Make line
            var geo = new ol.geom.LineString([start_point, end_point]);
            var line = new ol.feature.Vector(geo, null, style);
            // Add new feature to layer named by vector
            vector.addFeatures([line]);
            start_point = end_point;
        }
    });
    new ol.Map({
        layers: [raster, vector],
        view: new ol.View({
            projection: 'EPSG:3857',
            center: [0, 0],
            zoom: 10
        }),
        target: 'map'
    });

    console.log(minMax);
    var center = ol.proj.fromLonLat(
        (minMax.maxLon + minMax.minLon) / 2,
        (minMax.maxLat + minMax.minLat) / 2)
        .transform(fromProjection, toProjection);

    var extent = vector.getSource().getExtent();
    map.getView().fit(extent, map.getSize());

    // map.setCenter(center, zoom);
}
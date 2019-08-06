var lat = 48.91962;
var lon = 24.71187;

var reloadInterval = 10000;
var defaultZoom = 19;

var raster = new ol.layer.Tile({
    source: new ol.source.OSM()
});

var style = {
    'Point': new ol.style.Style({
        image: new ol.style.RegularShape({
            fill: new ol.style.Fill({
                color: '#00f'
            }),
            points: 5,
            radius1: 8,
            radius2: 4,
            stroke: new ol.style.Stroke({
                color: '#00f',
                width: 1
            })
        })
    }),
    'LineString': new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: '#0f0',
            width: 3
        })
    }),
    'MultiLineString': new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: '#f00',
            width: 5
        })
    })
};

var gpxFormat = new ol.format.GPX();

function getTrack() {
    const Http = new XMLHttpRequest();
    const url = 'http://localhost:8080/v1/online/track';
    Http.open("GET", url);
    Http.send();

    Http.onreadystatechange = (e) => {
        var segs = Http.responseText;
        console.log(segs.substring(0, 100) + " " + segs.length);
        var features = gpxFormat.readFeatures(segs, {
            dataProjection: 'EPSG:4326',
            featureProjection: 'EPSG:3857'
        });

        var vectorLayer = map.getLayers().getArray()[1];
        var src = vectorLayer.getSource();
        src.clear(true);
        src.addFeatures(features);
        src.refresh(true);
    }
}

var gpxSegments = gpxFormat.readFeatures(segments, {
    dataProjection : 'EPSG:4326',
    featureProjection : 'EPSG:3857'
});

var vectorSource = new ol.source.Vector({
    format: gpxFormat,
    features: gpxSegments,
});

var vector = new ol.layer.Vector({
    source: vectorSource,
    style: function (feature) {
        return style[feature.getGeometry().getType()];
    }
});

var map = new ol.Map({
    target: 'basicMap',
    layers: [raster, vector],
    view: new ol.View({
        center: ol.proj.fromLonLat([lon, lat]),
        zoom: 16
    })
});

var extent = vector.getSource().getExtent();
if (isFinite(extent[0])) {
    map.getView().fit(extent, map.getSize());
}
if (map.getView().getZoom() > defaultZoom) {
    map.getView().setZoom(defaultZoom);
}

setInterval(getTrack, reloadInterval);

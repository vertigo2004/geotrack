var lat = 48.91962;
var lon = 24.71187;

var raster = new ol.layer.Tile({
    source: new ol.source.OSM()
});

var style = {
    'Point': new ol.style.Style({
        image: new ol.style.Circle({
            fill: new ol.style.Fill({
                color: 'rgba(0,0,255,0.5)'
            }),
            radius: 5,
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

map.getView().fit(extent, map.getSize());
if (map.getView().getZoom() > 19) {
    map.getView().setZoom(19);
}

var coordinates = [];
var needInitialZoom = true;
var sse = null;
var url = "http://localhost:8080/v1/sse/trackme";

var defaultZoom = 15;


var styles = {
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
            color: '#f00',
            width: 5
        })
    }),
    'MultiLineString': new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: '#f00',
            width: 5
        })
    }),
    'geoMarker': new ol.style.Style({
        image: new ol.style.Circle({
            radius: 7,
            fill: new ol.style.Fill({color: 'black'}),
            stroke: new ol.style.Stroke({
                color: 'white', width: 2
            })
        })
    })
};

var lineGeometry = new ol.geom.LineString([]);

var feature = new ol.Feature(lineGeometry);

var source = new ol.source.Vector({
    wrapX: false,
    features: [feature]
});
var vector = new ol.layer.Vector({
    source: source,
    style: function (feature) {
        return styles[feature.getGeometry().getType()];
    }
});

var map = new ol.Map({
    layers: [
        new ol.layer.Tile({
            source: new ol.source.OSM()
        }),
        vector
    ],
    target: 'basicMap',
    loadTilesWhileAnimating: true,
    view: new ol.View({
        center: [0, 0],
        zoom: 2
    })
});
connect();

function zoom2Fit() {
    var extent = vector.getSource().getExtent();
    map.getView().fit(extent, map.getSize());
    if (map.getView().getZoom() > defaultZoom) {
        map.getView().setZoom(defaultZoom);
    }
}

function updateInfoDiv(data) {
    document.getElementById('info')
        .innerHTML =
            "Position: " + data.lat.toString().substring(0, 9) + " " + data.lon.toString().substring(0, 9) + "<br>" +
            "Time: " + data.time + "<br>" +
            "Elevation: " + data.elevation + "<br>" +
            "Temperature: " + data.bmeTemperature + "<br>" +
            "Humidity: " + data.bmeHumidity + "<br>" +
            "Pressure: " + data.bmePressure + "<br>" +
            "Illuminance: " + data.alsIlluminance + "<br>" +
            "DirectSunLight: " + data.alsDirectSunLight + "<br>" +
            "ShockDetected: " + data.shockDetected + "<br>" +
            "BatteryPower: " + data.batteryPower + "<br>" +
            "SignalStrength: " + data.signalStrength;
}

fitButton.onclick = function () {
    zoom2Fit();
};


function dynamicDraw(data) {
    var coord = ol.proj.fromLonLat([data.lon, data.lat]);
    // var geomP = new ol.geom.Point(coord);
    lineGeometry.appendCoordinate(coord);
    feature.setGeometry(lineGeometry);

    if (needInitialZoom) {
        zoom2Fit();
        needInitialZoom = false;
    }
}

function connect() {

    sse = new EventSource(url);

    sse.addEventListener("thing-event", function(event) {
        var data = JSON.parse(event.data)
        dynamicDraw(data);
        updateInfoDiv(data);
        });

    sse.onopen = function (event) {
        needInitialZoom = true;
        console.log("Connected");
    };

    sse.onmessage = function (event) {
        console.log(event.lastEventId)
        console.log(event.data);
    };

    sse.onerror = e => {
        if (e.readyState == EventSource.CLOSED) {
            console.log('close');
        }
        else {
            console.log(e);
        }
    };

}

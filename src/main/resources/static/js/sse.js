var coordinates = [];

var sse = null;
var url = "http://localhost:8080/v1/sse/trackme";


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

var map = new ol.Map({
    layers: [
        new ol.layer.Tile({
            source: new ol.source.OSM()
        })
    ],
    target: 'basicMap',
    view: new ol.View({
        center: [0, 0],
        zoom: 2
    })
});

var imageStyle = new ol.style.Style({
    image: new ol.style.Circle({
        radius: 5,
        fill: new ol.style.Fill({color: 'yellow'}),
        stroke: new ol.style.Stroke({color: 'red', width: 1})
    })
});

var headInnerImageStyle = new ol.style.Style({
    image: new ol.style.Circle({
        radius: 2,
        fill: new ol.style.Fill({color: 'blue'})
    })
});

var headOuterImageStyle = new ol.style.Style({
    image: new ol.style.Circle({
        radius: 5,
        fill: new ol.style.Fill({color: 'black'})
    })
});

var source = new ol.source.Vector({
    wrapX: false
});
var vector = new ol.layer.Vector({
    source: source,
    style: function (feature) {
        return style[feature.getGeometry().getType()];
    }
});

source.on('addfeature', function(e) {
    // flash(e.feature);
    map.render();
});

map.addLayer(vector);
connect();

function dynamicDraw(data) {
    if (data != null) {
        var geom = new ol.geom.Point(ol.proj.fromLonLat([data.lon, data.lat]));
        var feature = new ol.Feature(geom);
        source.addFeature(feature);
    }
}

function connect()
{
    sse = new EventSource(url);

    sse.addEventListener("thing-event", function(event) {
        dynamicDraw(JSON.parse(event.data));
        });

    sse.onopen = function (event) {
        log("Connected");
    };

    sse.onmessage = function (event) {
        console.log(event.lastEventId)
        log(event.data);
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

function log(message)
{
    var logConsole = document.getElementById('logging');
    var p = document.createElement('p');
    p.appendChild(document.createTextNode(message));
    logConsole.appendChild(p);
    console.log(message);
}
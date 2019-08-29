var sse = null;
var url = "http://localhost:8080/v1/sse/trackme";

function connect()
{
    sse = new EventSource(url);

    sse.addEventListener("thing-event", function(event) {
        console.log(event.lastEventId)
        log(event.data)
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
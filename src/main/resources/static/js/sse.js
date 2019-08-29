var sse = null;
var url = "http://localhost:8080/v1/sse/trackme";

function connect()
{
    sse = new EventSource(url);

    sse.onmessage = function (event) {
        log(event.data);
    };

    sse.doOnNext = function (event) {
        log(event.data);
    }

}

function log(message)
{
    var console = document.getElementById('logging');
    var p = document.createElement('p');
    p.appendChild(document.createTextNode(message));
    console.appendChild(p);
}
const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/spring-batch'
});

stompClient.onConnect = (frame) => {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/job/event', (jobEvent) => {
        appendEvent(JSON.parse(jobEvent.body));
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    console.log("Disconnected");
}

function appendEvent(jobEvent) {
    /*
        {
            ts: "2024-04-04T18:00:00",
            event: "Job start",
            status: "STARTED"
        }
    */
    $("#job-event").append("<tr><td>" + jobEvent.ts + "</td><td>" + jobEvent.event + "</td><td>" + jobEvent.status +
    "</td></tr>");
}

function startJob() {
    const jobName = $("#job-name").val();
    fetch(`http://localhost:8080/api/v1/job/run/${jobName}`, {
        method: 'POST'
    }).then(() => {
        console.log('Job start request sent.');
    });
}

function listHistory() {
    const jobName = $("#job-name").val();
    window.location = `/job/${jobName}/history`;
}

$(function () {
    connect();
    $("form").on('submit', (e) => e.preventDefault());
    $("#start-job").click(() => startJob());
    $("#list-history").click(() => listHistory())
    $(window).on('unload', function () {
        disconnect();
    });
});

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head>
    <title>Hello WebSocket</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link href="/main.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7.0.0/bundles/stomp.umd.min.js"></script>
    <script src="<c:url value="/app.js"/>"></script>
</head>
<body>
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being
    enabled. Please enable
    Javascript and reload this page!</h2></noscript>
<div id="main-content" class="container">
    <div class="row">
        <h1>Job: <c:out value="${jobName}"/></h1>
    </div>
    <div class="row">
        <form class="form-inline">
            <div class="form-group">
                <input id="job-name" type="hidden" value="<c:out value="${jobName}"/>"/>
                <button id="start-job" class="btn btn-default" type="submit">Start Job</button>
                <button id="list-history" class="btn btn-default" type="submit">Job History</button>
            </div>
        </form>
    </div>
    <div class="row">
        <h3>Execution status</h3>
        <div class="col-md-12">
            <table id="conversation" class="table table-striped">
                <thead>
                    <tr>
                        <th>Timestamp</th>
                        <th>Event</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody id="job-event">

                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
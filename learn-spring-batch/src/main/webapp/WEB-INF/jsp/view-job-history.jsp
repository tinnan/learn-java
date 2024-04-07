<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head>
    <title>Hello WebSocket</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link href="/main.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7.0.0/bundles/stomp.umd.min.js"></script>
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
        <h3>Execution history</h3>
        <div class="col-md-12">
            <table id="conversation" class="table table-striped">
                <thead>
                    <tr>
                        <th>Instance ID</th>
                        <th>Execution ID</th>
                        <th>End time</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${jobSummaries}" var="jobSummary">
                    <tr>
                        <td><c:out value="${jobSummary.instanceId()}"/></td>
                        <td><c:out value="${jobSummary.executionId()}"/></td>
                        <td><c:out value="${jobSummary.endTime()}"/></td>
                        <td><c:out value="${jobSummary.status()}"/></td>
                    </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
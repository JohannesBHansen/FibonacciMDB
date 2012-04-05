<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>JMS Load Testing Utility</title>
    <style type="text/css">
        body {
            font-family: verdana, tahoma, sans-serif;
            text-align: center;
        }
        h1 {
            margin-top: 50px;
            color: #cc6666;
        }
        .message_text {
            color: #996666;
        }
    </style>
</head>
<body>
<h1>JMS Load Testing Utility</h1>
<form method=post action="FibonacciMDBServletClient">
    <p class="message_text">This utility will send the specified number of messages to a JMS
        <br>service, which will concurrently process each message in the
        <br>time it takes to calculate the specified Fibonacci sequence.</p>
    <p>Fibonacci value to compute: <input type=text name=value value="35" size=5></p>
    <p>Number of times to compute it (concurrent MessageDriven EJBs): <input type=text name=iterations value="40" size=5> </p>
    <input type=submit value="Submit"></p>
</form>
<BR><BR>

</body>
</html>
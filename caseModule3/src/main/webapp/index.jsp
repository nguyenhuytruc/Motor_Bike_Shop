<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<%
    String redirectURL = "/login";
    response.sendRedirect(redirectURL);
%>
</body>
</html>
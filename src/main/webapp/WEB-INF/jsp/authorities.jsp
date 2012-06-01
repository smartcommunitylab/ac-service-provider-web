<%-- 
    Document   : authorities
    Created on : Jun 1, 2012, 2:52:43 PM
    Author     : vic
--%>

<%@page contentType="text/html" pageEncoding="UTF8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF8">
        <title>Authentication required</title>
    </head>
    <body>
        <h1>Authentication required</h1>
        <p>Please choose an authority to login:</p><br>
    <c:forEach var="entry" items="${authorities}">
        <a href="${entry.value}">${entry.key}</a>
    </c:forEach>

</body>
</html>

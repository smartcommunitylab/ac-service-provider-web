<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Portfolio Manager</title>
<script>
</script>
</head>
<body onload="xmlhttp = new XMLHttpRequest();xmlhttp.open('POST','./validateCode/'+window.document.location.hash,false);xmlhttp.send();alert(xmlhttp.responseText);">
    <div id="main">
    </div>
</body>
</html>
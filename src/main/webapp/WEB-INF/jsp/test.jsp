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
<body onload="code = window.document.location.hash.substr(1);xmlhttp = new XMLHttpRequest();xmlhttp.open('POST','./validateCode/'+code,true);       xmlhttp.onreadystatechange = function (e) {if (xmlhttp.readyState == 4) {if(xmlhttp.status == 200){ var token = xmlhttp.responseText; alert(token); } else { document.documentElement.innerHTML=xmlhttp.responseText;}}}; xmlhttp.send();">
    <div id="main">
    </div>
</body>
</html>
<%--

       Copyright 2012-2013 Trento RISE

       Licensed under the Apache License, Version 2.0 (the "License");
       you may not use this file except in compliance with the License.
       You may obtain a copy of the License at

           http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing, software
       distributed under the License is distributed on an "AS IS" BASIS,
       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       See the License for the specific language governing permissions and
       limitations under the License.

--%>

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
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

<%@page contentType="text/html" pageEncoding="UTF8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">
<link href="../css/style.css" rel="stylesheet" type="text/css">
<title>SmartCampus Authentication</title>
</head>
<body>
	<img class="logo" src="../img/ls_logo.png" alt="SmartCampus" />
	<div class="clear"></div>
	<div class="authorities">
		<p>Please choose the provider for your login</p>
		<ul>
			<c:forEach var="entry" items="${authorities}">
				<li><a
					href="<%=request.getContextPath() %>/ac/getToken/${entry.value}?redirect=${redirect}${browser != null ? '&browser=' : ''}${code != null ? '&code=' : ''}">${entry.key}</a></li>
			</c:forEach>
		</ul>
	</div>
</body>
</html>

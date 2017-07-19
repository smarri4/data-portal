<%@ taglib prefix="portal" tagdir="/WEB-INF/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Forbidden</title>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/css/base.css">
</head>
<body>
<portal:header/>
<portal:menu/>
<div id="content">
<h1 style="color:red">Access to specified resource is denied</h1>
<p style="text-align:center">${exception.message}</p>
</div>
</body>
</html>
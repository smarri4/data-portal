<%@ taglib prefix="portal" tagdir="/WEB-INF/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List, java.util.HashMap, edu.uic.cri.portal.model.entities.User, edu.uic.cri.arvados.ArvadosAPI,
	edu.uic.cri.portal.model.entities.arvados.Group, edu.uic.cri.portal.AdministrationServlet" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Projects</title>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/css/base.css">
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/css/base.css">
<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
	<script type="text/javascript">
	$(function(){
		 $( document ).on( 'click','#addAdminButton', function(){
				$("#addAdmin").toggle();
				$("#addAdminButton").toggle();
			});
	});
	</script>
</head>
<body>
<portal:header/>
<portal:menu active="users"/>
<div id="content">
<% 
	if ( request.getAttribute("message") != null ) { 
%><div class="message"><%= (String) request.getAttribute("message") %></div>
<%	}%>
<h2>Project Users</h2><BR>
<table border="1" class="table">
	<tr>
		<th>User Name</th>
		<th>UserId</th>
		<th>Affiliation</th>
		<th>Role</th>
	</tr>
<% List<User> userList= (List<User>)request.getAttribute("portal_users");
	
for ( User user : userList ) {
	%><tr>
<td><%= user.getName()%></td>
<td><%= user.getUserid() %></td>
<td><%= user.getAffiliation() %></td>
<td><%= user.getRole() %></td></tr>
<%	} %>
</table>

<h2>Admin Users</h2><BR>
<table border="1" class="table">
	<tr>
		<th>User Name</th>
		<th>UserId</th>
		<th>Affiliation</th>
	</tr>
<%
List<User> adminList= (List<User>)request.getAttribute("admin_users");
	
for ( User user : adminList ) {
	%><tr>
<td><%= user.getName()%></td>
<td><%= user.getUserid() %></td>
<td><%= user.getAffiliation() %></td>
<%	} %>
</table>
</div>

<div id="addAdmin" style="display:none">
	<form id="addAdminForm" action="<%=request.getContextPath() %>/users/add-admin" method="post">
		User ID: <input id="userid" name="userid" required>
		Name: <input id="name" name="name">
		Affiliation: <input id="affiliation" name="affiliation">
		<input type="submit"/>
	</form>
</div>
<button id="addAdminButton" style="display:block;margin:45px;">Add Admin</button>

</body>
</html>
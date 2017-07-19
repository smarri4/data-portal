<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.HashMap, edu.uic.cri.portal.model.entities.arvados.User, edu.uic.cri.arvados.ArvadosAPI,
edu.uic.cri.portal.model.entities.arvados.Group, edu.uic.cri.portal.ProjectManagementServlet,
edu.uic.cri.portal.model.entities.arvados.Collection, edu.uic.cri.portal.AdministrationServlet" %>
<%@ taglib prefix="portal" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Projects</title>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/css/base.css">
<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
</head>
<body>
<portal:header/>
<portal:menu active="projects"/>
<div id="content">
<% ArvadosAPI arv = AdministrationServlet.getArv(request);
	HashMap<String, User> userMap = new HashMap<String, User>();
	Group project = AdministrationServlet.getCurrentProject(request); 
	
	if ( project != null ) { %>
<div id="heading">
<h2><%= project.getName() %>
	<a href="<%= request.getContextPath() %>/projects/<%= project.getUuid() %>" class="btn btn-primary btn-sm" style="float:right">
			view as user
	</a>
</h2>
</div>

<div style="border: 1px solid gray;"><%= (project.getDescription()!=null)?project.getDescription():"" %></div>

<h3>Collections</h3>
<table class="table">
<tr><th>Collection</th><th>Created</th><th>Modified</th><th>Modified By</th></tr>
<% for ( Collection collection : AdministrationServlet.getCollectionsForProject(request, project.getUuid()) ) { 
	if ( ! userMap.containsKey(project.getModifiedByUserUUID()) ) 
		userMap.put(project.getModifiedByUserUUID(), arv.getUser(project.getModifiedByUserUUID()));
%><tr>
<td><a href="<%= request.getContextPath() %>/manage-projects/<%= project.getUuid() %>/<%= collection.getUuid() %>"><%= collection.getName() %></a></td>
<td><%= collection.getCreatedAt() %></td>
<td><%= collection.getModified() %></td>
<td><%= userMap.get(project.getModifiedByUserUUID()) %></td>
</tr>
<% } } %></table>

<h3>Users</h3>
<portal:usertable users="<%= ProjectManagementServlet.getCurrentMembers(request) %>" project="<%= project.getUuid() %>"/>
</div>
</body>
</html>
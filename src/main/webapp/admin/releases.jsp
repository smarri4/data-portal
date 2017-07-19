<%@ taglib prefix="portal" tagdir="/WEB-INF/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List, java.util.HashMap, edu.uic.cri.portal.model.entities.Release, edu.uic.cri.arvados.ArvadosAPI,
edu.uic.cri.portal.dao.ProjectDao,edu.uic.cri.portal.dao.ProjectDaoImpl,java.text.DateFormat, java.text.SimpleDateFormat,
edu.uic.cri.portal.listener.ProjectSyncScheduleListener,edu.uic.cri.portal.model.entities.arvados.Group" %>
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
<portal:menu active="releases"/>
<div id="content">
<h1>Releases</h1>
<form method="post">
<table border="0"  class="table">
	<tr>
		<th>Project name</th>
		<th>Released by</th>
		<th>Release Date</th>
		<th>Description</th>
	</tr>
<% List<Release> releaseList= (List<Release>)request.getAttribute("releases");
DateFormat df = new SimpleDateFormat("EEE, MMM d, hh:mm a,yyyy.");
ProjectDao projectDao = new ProjectDaoImpl();
for ( Release release : releaseList ) {
	String projectForRelease = projectDao.getProjectForRelease(release.getReleaseid());
	 Group proj = ProjectSyncScheduleListener.getProject(projectForRelease);
	%><tr>
<td><%= proj.getName()%></td>
<td><%= release.getRelease_by()%></td>
<td><a href="<%= request.getContextPath() %>/show-release/<%= release.getReleaseid() %>"><%= df.format(release.getRelease_date()) %></a></td>
<td><%= release.getDescription() %></td>
<td><input type="checkbox" name="revokeList" value="<%= release.getReleaseid() %>"></td>
</tr>
<%	} %>
</table>
<br>
<button type="submit" name="action" value="revoke-release">Revoke Release</button>
</form>
</div>
</body>
</html>
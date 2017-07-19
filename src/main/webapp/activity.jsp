<%@ taglib prefix="portal" tagdir="/WEB-INF/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="edu.uic.cri.portal.model.entities.Release,java.util.List,java.util.ArrayList,java.util.HashMap,java.util.Set,java.util.HashSet,
edu.uic.cri.portal.dao.ReleaseDao,edu.uic.cri.portal.dao.ReleaseDaoImpl,
edu.uic.cri.arvados.ArvadosAPI,edu.uic.cri.portal.AdministrationServlet,edu.uic.cri.portal.model.entities.arvados.Group,
edu.uic.cri.portal.dao.ProjectDao,edu.uic.cri.portal.dao.ProjectDaoImpl,edu.uic.cri.portal.listener.ProjectSyncScheduleListener,
java.text.DateFormat, java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Activity</title>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/css/base.css">
<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
</head>
<body>
<portal:header/>
<portal:menu active="activity"/>
<h1 style="margin:30px">Activity</h1>
<table border="0" class="table" style="margin: 20px;">
<tr>
	<th>Project Name</th>
	<th>Released By</th>
	<th>Released At</th>
	<th>Description</th>
</tr>
<tr>
<% 
DateFormat df = new SimpleDateFormat("EEE, MMM d, hh:mm a,yyyy.");
boolean isAdmin = request.isUserInRole("admin");
ReleaseDao releaseDao = new ReleaseDaoImpl();
ProjectDao projectDao = new ProjectDaoImpl();
List<Release> releaseList = releaseDao.getOverallActivityForUser(request.getRemoteUser());
 for ( Release release : releaseList ) {
	 String projectForRelease = projectDao.getProjectForRelease(release.getReleaseid());
	 Group proj = ProjectSyncScheduleListener.getProject(projectForRelease);
	%><tr>
<td><%= proj.getName()%></td>
<td><%= release.getRelease_by()%></td>
<td><a href="<%= request.getContextPath() %>/release/<%= release.getReleaseid() %>"><%= df.format(release.getRelease_date()) %></a></td>
<td><%= release.getDescription() %></td></tr>
<%	} %>
</table>
</body>
</html>
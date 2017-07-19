<%@ taglib prefix="portal" tagdir="/WEB-INF/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="edu.uic.cri.portal.model.entities.Release,edu.uic.cri.portal.model.entities.ProjectFiles,
java.text.DateFormat,java.text.SimpleDateFormat,java.util.List,
edu.uic.cri.portal.dao.ReleaseDao,edu.uic.cri.portal.dao.ReleaseDaoImpl" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Release</title>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/css/base.css">
<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
</head>
<body>
<portal:header/>
<portal:menu/>
<h1 style="margin:30px">Release</h1>
<table border="0" class="table" style="margin:20px">
<tr>
	<th>File Name</th>
	<th>File Type</th>
	<th>Released By</th>
	<th>Modified At</th>
	<th>Modified By</th>
	<th>Description</th>
</tr>
<tr>
	<% 
	DateFormat df = new SimpleDateFormat("EEE, MMM d, hh:mm a,yyyy.");
	List<ProjectFiles> files= (List<ProjectFiles>)request.getAttribute("project_files");
	ReleaseDao releaseDao = new ReleaseDaoImpl();
	for ( Object elem: files ) {
		if ( elem instanceof ProjectFiles ) { 
			ProjectFiles afile = (ProjectFiles) elem;
			String[] filepath = afile.getID().split("/");
			Release release = releaseDao.getReleaseById(afile.getReleaseid());
	%>
	<tr><td><a href="<%=request.getContextPath()%>/file/<%= afile.getID() %>"><%=(filepath.length>1)?filepath[1]:filepath[0] %></a></td>
	<td><%= afile.getType() %></td>
	<td><%= release.getRelease_by() %></td>
	<td><%=  df.format(afile.getModified_at()) %></td>
	<td><%= afile.getModified_by() %></td>
	<td><%= afile.getDescription() %></td>
</tr>			
	<%		}
		}
	%>
</table>
<button type="button" name="back" style="margin:20px" onclick="history.back()">back</button>
</body>
</html>

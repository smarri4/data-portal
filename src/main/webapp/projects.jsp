<%@ taglib prefix="portal" tagdir="/WEB-INF/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Set, java.util.ArrayList,java.util.List,java.util.HashMap,edu.uic.cri.portal.model.entities.arvados.User,
	java.util.Map,java.util.Map.Entry, edu.uic.cri.arvados.ArvadosAPI, edu.uic.cri.portal.AdministrationServlet,
	edu.uic.cri.portal.model.entities.arvados.Group,java.text.DateFormat,java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Projects</title>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/css/base.css">
<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="<%= request.getContextPath() %>/resources/js/tables.js"></script>
</head>
<body>
<portal:header/>
<portal:menu active="projects"/>
<div id="content">
<table class="table">
<thead>
		<tr>
			<th class="sortable" onClick='sortTable(this,"string")'>Project Name</th>
			<th class="sortable" onClick='sortTable(this,"date")'>Created At</th>
			<th>Description</th>
		</tr>
</thead>
<tbody>
<% 
		// RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher("/projects");
		// reqDispatcher.include(request, response);
		ArvadosAPI arv = AdministrationServlet.getArv(request);
		List<Group> groupList = (List<Group>)request.getAttribute("GROUP_LIST");
		HashMap<String,Group> hm = new HashMap<String,Group>();
		DateFormat df = new SimpleDateFormat("EEE, MMM d, hh:mm a,yyyy.");
		for(Group group : groupList){
			hm.put(group.getUuid(),group);
			String description = group.getDescription();
			if ( description == null ) {
				description = "";
			} else if ( description.length() > 150 ) {
				description = description.substring(0, 150).concat("...");
			}
%>
<tr>
<td><a href="<%= request.getContextPath() %>/projects/<%= group.getUuid() %>"><%= group.getName() %></a></td>
<td><%= df.format(group.getCreatedAt()) %></td>
<td><%= description %></td></tr>
<%		}
		session.setAttribute("GROUP_MAP", hm);  %>
</tbody></table>
</div>
</body>
</html>
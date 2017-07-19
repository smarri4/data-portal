<%@ taglib prefix="portal" tagdir="/WEB-INF/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List,java.util.Set,java.util.HashSet, java.util.HashMap, edu.uic.cri.portal.model.entities.arvados.User, edu.uic.cri.arvados.ArvadosAPI,
	edu.uic.cri.portal.model.entities.arvados.Group, edu.uic.cri.portal.AdministrationServlet,java.text.DateFormat,java.text.SimpleDateFormat" %>
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
<table class="table">
	<tr>
		<th>Project Name</th>
		<th>Analyst</th>
		<th>Created At</th>
		<th>Modified By</th>
		<th>Modified At</th>
		<th>Description</th>
	</tr>
<% ArvadosAPI arv = AdministrationServlet.getArv(request);
	HashMap<String, User> userMap = new HashMap<String, User>();
	DateFormat df = new SimpleDateFormat("EEE, MMM d, hh:mm a,yyyy.");
	List<Group> groupList = AdministrationServlet.getAllProjects(request);
	Set<String> portalProjectSet = new HashSet<String>(AdministrationServlet.getPortalProjectList());
for ( Group group : groupList ) {
	if ( ! userMap.containsKey(group.getOwnerUUID()) )
		userMap.put(group.getOwnerUUID(), arv.getUser(group.getOwnerUUID()));
	if ( ! userMap.containsKey(group.getModifiedByUserUUID()) ) 
		userMap.put(group.getModifiedByUserUUID(), arv.getUser(group.getModifiedByUserUUID()));
	String formattedDate = df.format(group.getModified());
	String description = group.getDescription();
	if ( description == null ) {
		description = "";
	} else if ( description.length() > 150 ) {
		description = description.substring(0, 150).concat("...");
	}
%><tr>
<td>
<% if ( portalProjectSet.contains(group.getUuid())) { %>&#9989;<% } %>
<a href="<%= request.getContextPath() %>/manage-projects/<%= group.getUuid() %>"><%= group.getName() %></a></td>
<td><%= userMap.get(group.getOwnerUUID()).getFirstName()+" "+ userMap.get(group.getOwnerUUID()).getLastName() %></td>
<td><%= df.format(group.getCreatedAt()) %></td>
<td><%= userMap.get(group.getModifiedByUserUUID()).getFirstName()+" "+  userMap.get(group.getModifiedByUserUUID()).getLastName()%></td>
<td><%=formattedDate%></td>
<td><%= description %></td></tr>
<%	} %>
</table>

</div>
</body>
</html>
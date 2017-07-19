<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="edu.uic.cri.portal.model.entities.ProjectFiles,java.util.List,java.util.HashMap,java.util.Set,java.util.HashSet,
edu.uic.cri.portal.dao.ProjectDao,edu.uic.cri.portal.dao.ProjectDaoImpl,
edu.uic.cri.arvados.ArvadosAPI,edu.uic.cri.portal.AdministrationServlet,edu.uic.cri.portal.model.entities.arvados.Group,
edu.uic.cri.portal.model.arvados.GroupManager,java.text.DateFormat, java.text.SimpleDateFormat" %>
<table border="0" class="table">
<tr>
	<th>Project Name</th>
	<th>Created At</th>
	<th>Description</th>
</tr>
<tr>
<% 
boolean isAdmin = request.isUserInRole("admin");
ProjectDao projectDao = new ProjectDaoImpl();
List<String> projectList = projectDao.getRecentProjectsForUser(request.getRemoteUser());
Set<String> projectset = new HashSet<String>(projectList);
		GroupManager grpMgr = new GroupManager();
		List<Group> groupList = grpMgr.listGroups();
		DateFormat df = new SimpleDateFormat("EEE, MMM d, hh:mm a,yyyy.");
		int i=0;
		for(Group group : groupList){
			if(!projectset.contains(group.getUuid())&&!isAdmin)
				continue;
			String description = group.getDescription();
			if ( description == null ) {
				description = "";
			} else if ( description.length() > 150 ) {
				description = description.substring(0, 150).concat("...");
			}
			i++;
			if(i>10)
				break;
%>
<tr>
<% if(!isAdmin){%>
	<td><a href="<%= request.getContextPath() %>/projects/<%= group.getUuid() %>"><%= group.getName() %></a></td>
<%}
else{%>
	<td><a href="<%= request.getContextPath() %>/manage-projects/<%= group.getUuid() %>"><%= group.getName() %></a></td>
<%}%>
<td><%= df.format(group.getCreatedAt()) %></td>
<td><%= description %></td></tr>
<%} %>
</table>


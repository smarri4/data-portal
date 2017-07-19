<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="edu.uic.cri.portal.model.entities.Release,java.util.List,java.util.ArrayList,java.util.HashMap,java.util.Set,java.util.HashSet,
edu.uic.cri.portal.dao.ReleaseDao,edu.uic.cri.portal.dao.ReleaseDaoImpl,
edu.uic.cri.portal.dao.ProjectDao,edu.uic.cri.portal.dao.ProjectDaoImpl,java.text.DateFormat, java.text.SimpleDateFormat,
edu.uic.cri.portal.listener.ProjectSyncScheduleListener,edu.uic.cri.portal.model.entities.arvados.Group" %>
<table border="0" class="table">
<tr>
	<th>Project Name</th>
	<th>Created At</th>
	<th>Description</th>
</tr>
<tr>
<% 
DateFormat df = new SimpleDateFormat("EEE, MMM d, hh:mm a,yyyy.");
boolean isAdmin = request.isUserInRole("admin");
ReleaseDao releaseDao = new ReleaseDaoImpl();
ProjectDao projectDao = new ProjectDaoImpl();
List<Release> releaseList = null;
if(request.isUserInRole("admin")){
	releaseList = releaseDao.getAllReleases();
}
else{
	releaseList = releaseDao.getRecentActivityForUser(request.getRemoteUser());
}
int count=0;
 for ( Release release : releaseList ) {
	 if(count++==10)
		 break;
	 String projectForRelease = projectDao.getProjectForRelease(release.getReleaseid());
	 Group proj = ProjectSyncScheduleListener.getProject(projectForRelease);
	%><tr>
<td><%= proj.getName()%></td>
<% if(request.isUserInRole("admin")){%>
	<td><a href="<%= request.getContextPath() %>/show-release/<%= release.getReleaseid() %>"><%= df.format(release.getRelease_date()) %></a></td>
<%} else{ %>
<td><a href="<%= request.getContextPath() %>/release/<%= release.getReleaseid() %>"><%= df.format(release.getRelease_date()) %></a></td>
<% }%>
<td><%= release.getDescription() %></td></tr>
<%	} %>
</table>


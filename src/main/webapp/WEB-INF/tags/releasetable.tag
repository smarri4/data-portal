<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="files" required="true" type="java.util.List" %>
<%@ tag import="java.util.List, edu.uic.cri.portal.model.entities.ProjectFiles,java.text.DateFormat,java.text.SimpleDateFormat,
edu.uic.cri.portal.dao.ReleaseDao,edu.uic.cri.portal.dao.ReleaseDaoImpl,edu.uic.cri.portal.model.entities.Release" %>
<table border="1" style="margin: 0px 0px 0px 40px">
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
	List<ProjectFiles> files= (List<ProjectFiles>)request.getAttribute("release_files");
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


<%@ taglib prefix="portal" tagdir="/WEB-INF/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="edu.uic.cri.portal.ProjectManagementServlet, edu.uic.cri.portal.model.entities.arvados.Group" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><%request.getAttribute("project_name");%></title>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/css/base.css">
<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script type="text/javascript">
function validateDownload() {
    if (!$('input[name="zipList"]').is(':checked')) {
        alert("Please check atleast one file to download");
        return false;
    }
}
<%Group project = ProjectManagementServlet.getCurrentProject(request); %>
$(function(){
    $("#file-upld-btn").click(function(){
        $("#sub-content").load("<%= request.getContextPath() %>/fileupload.jsp?projectid=<%= project.getUuid() %>");
        $("#file-upld-btn").remove();
    });
});
</script>
</head>
<body>
	<portal:header/>
	<portal:menu active="projects"/>
	<body>
		<div id="content">
			<% 
				if ( project != null ) {
				String view = ProjectManagementServlet.getCurrentView(request); 
				String description = project.getDescription();
				if ( description == null ) {
					description = "";
					}%>
			<h1><%=project.getName()%></h1>
			<p id=description><%=description%></p>
			
			<% if ( request.isUserInRole("admin") ) { %>
			<p><a href="<%= request.getContextPath() %>/manage-projects/<%= project.getUuid() %>">Manage project</a></p>
			<% } %>
			<div id="sub-content">
			<portal:collectiontabs active="<%= view %>" project="<%= project.getUuid() %>"/>
				<% if ( view.equalsIgnoreCase(ProjectManagementServlet.PROJECT_USERS_ATTR) ) { %>
				<portal:usertable users="<%= ProjectManagementServlet.getCurrentMembers(request) %>" project="<%= project.getUuid() %>"/>
				<% } else if ( ProjectManagementServlet.getProjectFiles(request) != null ) { %>
				<form id="fileForm" method="post" action="<%= request.getContextPath() %>/zip/<%= project.getName()%>">
					<portal:filetable files="<%= ProjectManagementServlet.getProjectFiles(request) %>"/>
					<button type="submit" class="btn btn-primary btn-sm" onclick="return validateDownload()" >Download Files</button>
					<button id="file-upld-btn" class="btn btn-primary btn-sm">Upload files</button>
				</form>
				<% } } %>
			</div>
		</div>
		<div id="upload-files"></div>
	</body>
</html>
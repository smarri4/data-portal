<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="active" required="false" %>
<nav>
<ul  class="nav nav-tabs" role="tablist">
<%if(request.isUserInRole("admin")) {%>
<li role="presentation" <%= "projects".equals(active) ? " class='active'" : ""%>><a href="<%=request.getContextPath() %>/manage-projects">Projects</a></li>	
<li role="presentation" <%= "users".equals(active) ? " class='active'" : ""%>><a href="<%=request.getContextPath() %>/manage-users">Users</a></li>
<li role="presentation" <%= "releases".equals(active) ? " class='active'" : ""%>><a href="<%=request.getContextPath() %>/show-releases">Releases</a></li>
<li role="presentation" <%= "view-stg-files".equals(active) ? " class='active'" : ""%>><a href="<%=request.getContextPath() %>/view-stg-files" method="head">Staged files</a></li>
<%} else { %>
<li role="presentation" <%= "projects".equals(active) ? " class='active'" : ""%>><a href="<%=request.getContextPath() %>/projects">Projects</a></li>
<li role="presentation" <%= "activity".equals(active) ? " class='active'" : ""%>><a href="<%=request.getContextPath() %>/activity">Activity</a></li>
<% } %>
<!-- <li><a href="<%= request.getContextPath() %>/logout.jsp">Logout</a></li> -->
<jsp:doBody/>
</ul>
</nav>
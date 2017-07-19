<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="active" required="false" %>
<%@ attribute name="project" required="true" %>
<nav>
<ul class="nav nav-tabs" role="tablist">
<li role="presentation" <%= "rawdata".equals(active) ? " class='active'" : ""%>><a href="<%= request.getContextPath() %>/projects/<%= project %>/rawdata">Raw Data</a></li>
<li role="presentation" <%= "results".equals(active) ? " class='active'" : ""%>><a href="<%= request.getContextPath() %>/projects/<%= project %>/results">Results</a></li>
<li role="presentation" <%= "reports".equals(active) ? " class='active'" : ""%>><a href="<%= request.getContextPath() %>/projects/<%= project %>/reports">Reports</a></li>
<li role="presentation" <%= "project_users".equals(active) ? " class='active'" : ""%>><a href="<%= request.getContextPath() %>/projects/<%= project %>/project_users">Users</a></li>
<jsp:doBody/>
</ul>
</nav>
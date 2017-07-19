<%@ taglib prefix="portal" tagdir="/WEB-INF/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="edu.uic.cri.portal.ProjectManagementServlet, edu.uic.cri.portal.model.entities.arvados.Group,
edu.uic.cri.portal.model.entities.User,edu.uic.cri.portal.dao.UserDao,edu.uic.cri.portal.dao.UserDaoImpl" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>CRI Data Portal</title>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/css/base.css">
<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
</head>
<body>
<portal:header/>
<portal:menu/>
<body>
<%
UserDao userDao = new UserDaoImpl();
User user = userDao.getUser(request.getRemoteUser());
System.out.println("affiliation:"+user.getAffiliation());
System.out.println("user:"+user.getName());

if(user.getAffiliation()==null|| user.getName()==null||user.getAffiliation().equals("")|| user.getName().equals("")){
	System.out.println("forwarding to updateprofile page");
	RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher("/updateprofile.jsp");
	reqDispatcher.forward(request, response);
}

%>
<div id="content">
<h2>Welcome, <%= user.getName() %></h2>
<div class="section">
<h3>Recent Projects</h3>
<portal:recentprojects/>
</div>
<div class="section">
<h3>Recent Activity</h3>
<portal:recentActivity/>
</div>
</div>
</body>
</html>

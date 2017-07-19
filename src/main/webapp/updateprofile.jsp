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
<style>
.login-page {
	width: 360px;
	padding: 8% 0 0;
	margin: auto;
}

.form {
	position: relative;
	z-index: 1;
	background: #FFFFFF;
	max-width: 360px;
	margin: 0 auto 100px;
	padding: 45px;
	text-align: center;
	box-shadow: 0 0 20px 0 rgba(0, 0, 0, 0.2), 0 5px 5px 0
		rgba(0, 0, 0, 0.24);
}

.form input {
	width: 50%;
}
</style>
<script type="text/javascript">

function checkform() {
    if(document.getElementById("fullname").value.equals("")||document.getElementById("affiliation").value.equals("")) {
    	document.getElementById("error").style.display="inline";
    	alert('error');
        return false;
    }else {
    alert('success');
        document.updateProfile.submit();
        return true;
    }
}


</script>
</head>
<body>
<portal:header/>
<body>
<% UserDao userDao = new UserDaoImpl();
User user = userDao.getUser(request.getRemoteUser()); %>y
<h1 align= "center">Update Profile</h1>
	<div class="form">
		<form name="updateProfile" action="<%=request.getContextPath() %>/update-profile" method="post">
		<p id="error" align='center' style='color: red; display: none'>Please enter all the necessary details!</p>
			<div align= "left" >User ID : <input type="text" name="userid" id="userid" value="<%=request.getRemoteUser() %>" readonly /></div>
			<div align= "left" >Full Name : <input type="text" name="fullname" id="fullname" <%if(user.getName()==null){ %>placeholder="Full Name" <%}else{ %>  value="<%=user.getName()%>" readonly <%} %>/></div>
			<div align= "left" >Affiliation : <input type="text" name="affiliation" id="affiliation" <%if(user.getAffiliation()==null||user.getAffiliation().equals("")){ %>placeholder="Affiliation" <%}else{ %>  value="<%=user.getAffiliation()%>" readonly <%} %>/></div>
			<button type='submit' onsubmit="return checkform();">Submit</button>
		</form>
	</div>
</body>
</html>
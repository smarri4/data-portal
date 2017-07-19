<%@ taglib prefix="portal" tagdir="/WEB-INF/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>CRI Data Portal</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/base.css">
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
	width: 90%;
}
</style>
</head>
<body style="background-color: #ddd">
	<portal:header />
	<div id="content">
		<div class="login-page">
		<p align='center' style='color: red'>Username or password is incorrect!</p>
			<h2 align='center'>Please Login</h2>
			<div class="form">
				<form class="login-form" method="POST"
					action='<%=response.encodeURL("j_security_check")%>'>
					<input type="text" name="j_username" placeholder="username" /> <input
						type="password" name="j_password" placeholder="password" />
					<button type='submit'>Login</button>
				</form>
			</div>
		</div>
	</div>
</body>
</html>
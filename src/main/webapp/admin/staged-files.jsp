<%@page import="edu.uic.cri.portal.FileServlet"%>
<%@ taglib prefix="portal" tagdir="/WEB-INF/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="edu.uic.cri.portal.ProjectManagementServlet,com.google.gson.JsonObject,com.google.gson.JsonElement,
java.util.Set, java.util.Map,edu.uic.cri.portal.listener.ProjectSyncScheduleListener" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Release</title>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/css/base.css">
<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script>

var table = document.createElement("TABLE");

function listProjects(root){
	var div = document.getElementById('table1');
	table.setAttribute("class", "table");
	table.parent=div;
	<% JsonObject root = (JsonObject)request.getAttribute(ProjectManagementServlet.PROJECT_FILES_ATTR);
	Set<Map.Entry<String,JsonElement>> entrySet = root.	entrySet();
	for(Map.Entry<String,JsonElement> entry: entrySet){%>
		var row = table.insertRow(-1);
		var cell = row.insertCell(-1);
		var elem = document.createElement("A");
		elem.innerHTML = '<%=ProjectSyncScheduleListener.projectMap.get(entry.getKey()).getName()%>';
		elem.align='center';
		elem.onclick = function() {
			listFiles('<%=entry.getKey()%>', '<%= entry.getValue().toString()%>'); 
			};
		cell.appendChild(elem);
	<%}%>
	div.appendChild(table);
}

function listFiles(projid, fileListJson){
	var arr = eval('(' + fileListJson + ')');
	var div = document.getElementById('table1');
	var newTable = document.createElement("TABLE");
	newTable.setAttribute("class", "table");
	for(var i in arr){
		var row = newTable.insertRow(-1);
		var cell = row.insertCell(-1);
		var elem = document.createElement("A");
		elem.href='<%=request.getContextPath()%>'+'/view-file/'+projid+'/'+arr[i];
		elem.innerHTML = arr[i];
		elem.align='center';
		cell.appendChild(elem);
	}
	table.parent.replaceChild(newTable, table);
}
</script>
</head>
<body>
<portal:header/>
<portal:menu/>
<h1 id = "header" style="margin: 40px 0px 0px 40px">Staged Files</h1>
<div id = "table1" ></div>
<a href="#" onclick= 'location.reload();'>
<font size="+1">&#x21E6;</font></a>
<div id="preparetable">
	<script> listProjects(<%=((JsonObject)request.getAttribute(ProjectManagementServlet.PROJECT_FILES_ATTR)).toString()%>);</script>
	 Back
</div>
</body>
</html>

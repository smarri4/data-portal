<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Map, edu.uic.cri.portal.model.entities.arvados.Collection, 
edu.uic.cri.portal.model.entities.arvados.Collection.CollectionFile, 
edu.uic.cri.portal.model.entities.ProjectFiles,
edu.uic.cri.portal.AdministrationServlet, com.google.gson.JsonObject, com.google.gson.Gson" %>
<%@ taglib prefix="portal" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Projects</title>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/css/base.css">
<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
	<%
	Collection collection = AdministrationServlet.getCurrentCollection(request);
	JsonObject collectionRoot = collection.fetchJsonOfCollectionFiles(request);
	collectionRoot = collection.fillCheckBoxes(collectionRoot, request);
	String root = collectionRoot.toString();
	System.out.println(collectionRoot);
	%>

<script>
function validateRevoke() {
    if (!$('input[name="revokeList"]').is(':checked')) {
        alert("Please check atleast one file to revoke");
        return false;
    }
}
function validateRelease() {
    if (!$('input[name="fileList"]').is(':checked')) {
        alert("Please check atleast one file to release");
        return false;
    }
}
// var root;
var currDir;

function prepareCollectionTable(root){
	currDir = root;
	var obj = root;
	
	var div = document.getElementById('table1');
	var table = document.createElement("TABLE");
	table.setAttribute("class","table");
	var oldNode = div.childNodes[0];
	if(oldNode!=undefined){
		div.replaceChild(table,oldNode);
	}
	else{
		div.appendChild(table);
	}
	var row = table.insertRow(-1);
	row.insertCell(-1);
	var cell = row.insertCell(-1);
	cell.innerHTML = "Name";
	cell = row.insertCell(-1);
	cell.innerHTML = "Size";
	cell = row.insertCell(-1);
	cell.innerHTML = "Hide";
	cell = row.insertCell(-1);
	cell.innerHTML = "Revoke";
	var dirs = obj.dirs;
	if(dirs!=undefined){
		for(var i=0;i< dirs.length;i++){
			
			if (obj.dirs[i].parent == undefined ) {
				obj.dirs[i].parent = obj;
			}
			
			var row = table.insertRow(-1);
			var cell = row.insertCell(-1);
			var released = isReleased(dirs[i]);
			if(released>0)
				cell.innerHTML = '&#9989';
			else {
				elem = document.createElement("INPUT");
				elem.type = "checkbox";
				elem.name = i;
				elem.value = obj.files[i].fileid;
				if(released<0){
					elem.indeterminate = true;
				}
				elem.onclick = function() {
					onChangeCheckReleased(currDir.dirs[this.name], this.checked);
				};
				cell.appendChild(elem);
			}
			
			var cell = row.insertCell(-1);
			elem = document.createElement("A")
			elem.innerHTML = '<img src=\'<%= request.getContextPath() %>/resources/images/folder.png\'  height= 20>  ' + obj.dirs[i].name;
			elem.align='center';
			elem.class='folder';
			
			
			elem.name = i;
			elem.onclick = function() {
					prepareCollectionTable(currDir.dirs[this.name]); 
				};
			cell.appendChild(elem);
			
			cell = row.insertCell(-1);
			cell.innerHTML = "";
			
			cell = row.insertCell(-1);
			elem = document.createElement("INPUT");
			elem.type = "checkbox";
			elem.name = i;
			elem.onclick = function() {
				onChangeCheckHidden(currDir.dirs[this.name], this.checked);
			};
			cell.appendChild(elem);
			
			cell = row.insertCell(-1);
			elem = document.createElement("INPUT");
			elem.type = "checkbox";
			elem.name = i;
			elem.onclick = function() {
				onChangeCheckRevoke(currDir.dirs[this.name], this.checked);
			};
			cell.appendChild(elem);
		}
	}
	
	var files = obj.files;
	if(files!=undefined){
		for(var i=0;i< files.length;i++){
			
			var row = table.insertRow(-1);
			var cell = row.insertCell(-1);
			var released = isReleased(files[i]);
			if(released>0)
				cell.innerHTML = '&#9989';
			else if(released<0){
				elem = document.createElement("INPUT");
				elem.type = "checkbox";
				elem.name = "fileList";
				elem.value = obj.files[i].fileid;
				elem.indeterminate = true;
				cell.appendChild(elem);
			}
			else{
				elem = document.createElement("INPUT");
				elem.type = "checkbox";
				elem.name = "fileList";
				elem.value = obj.files[i].fileid;
				cell.appendChild(elem);
			}
			
			cell = row.insertCell(-1);
			elem = document.createElement("A");
			elem.name = i;
			elem.innerHTML = '<img src=\'<%= request.getContextPath() %>/resources/images/file.png\'  height= 20>  ' +obj.files[i].name;
			elem.href = "/file/" + files[i].fileid;
			cell.appendChild(elem);
			cell.class='file';
			
			
			cell = row.insertCell(-1);
			cell.innerHTML = obj.files[i].size;
			//cell.class = file;
			
			cell = row.insertCell(-1);
			elem = document.createElement("INPUT");
			elem.type = "checkbox";
			elem.value = obj.files[i].fileid;
			elem.name = "hidden";
		/*	elem.onclick = function() {
				onChangeCheckHidden(currDir.dirs[this.name], this.checked);
			};
		*/
			cell.appendChild(elem);
			
			cell = row.insertCell(-1);
			elem = document.createElement("INPUT");
			elem.type = "checkbox";
			elem.value = obj.files[i].fileid;
			elem.name = "revokeList";
			//elem.name = i;
	/*		elem.onclick = function() {
				var ele = document.createElement("INPUT");
				ele.type = "hidden";
				ele.name = "revokeList";
				ele.value = obj.files[elem.name].fileid
				ele.style="display:none"
				ele.checked = this.checked;
				var oForm = document.getElementById('collectionForm');
				oForm.appendChild(ele);
			};
		*/
			cell.appendChild(elem);
		}
	}
	
}

function onChangeCheckHidden(root, flag){
	var dirs = root.dirs;
	if(dirs!=undefined){
		for(var i=0;i< dirs.length;i++){
			onChangeCheckHidden(dirs[i],flag);
		}
	}
	var files = root.files;
	if(files!=undefined){
		for(var i=0;i< files.length;i++){
			if(files[i].hidden)
				continue;
			var elem = document.createElement("INPUT");
			elem.type = "hidden";
			elem.name = "hidden";
			elem.value = files[i].fileid;
			elem.style="display:none"
				elem.checked = flag;
			var oForm = document.getElementById('collectionForm');
			oForm.appendChild(elem);
		}
	}
}

function onChangeCheckReleased(root, flag){
	var dirs = root.dirs;
	if(dirs!=undefined){
		for(var i=0;i< dirs.length;i++){
			onChangeCheckReleased(dirs[i],flag);
		}
	}
	var files = root.files;
	if(files!=undefined){
		for(var i=0;i< files.length;i++){
			if(files[i].released)
				continue;
			var elem = document.createElement("INPUT");
			elem.type = "hidden";
			elem.name = "fileList";
			elem.value = files[i].fileid;
			elem.style="display:none"
				elem.checked = flag;
			var oForm = document.getElementById('collectionForm');
			oForm.appendChild(elem);
		}
	}
}

function onChangeCheckRevoke(root, flag){
	var dirs = root.dirs;
	if(dirs!=undefined){
		for(var i=0;i< dirs.length;i++){
			onChangeCheckRevoke(dirs[i],flag);
		}
	}
	var files = root.files;
	if(files!=undefined){
		for(var i=0;i< files.length;i++){
			if(!files[i].released)
				continue;
			var elem = document.createElement("INPUT");
			elem.type = "hidden";
			elem.name = "revokeList";
			elem.value = files[i].fileid;
			elem.style="display:none"
				elem.checked = flag;
			var oForm = document.getElementById('collectionForm');
			oForm.appendChild(elem);
		}
	}
}

function isReleased(root){
	
	if(!("dirs" in root)&&!("files" in root)){
		if(root.released)
			return 1;
		else
			return 0;
	}
		
	var count = 0;
	var dirs = root.dirs;
	if(("dirs" in root)){
		for(var i=0;i< dirs.length;i++){
			if(isReleased(dirs[i]))
				count++;
		}
	}
	var files = root.files;
	if(("files" in root)){
		for(var i=0;i< files.length;i++){
			if(files[i].released){
				count++;
			}
		}
	}
	var fileLength=0;
	var dirLength =0;
	if(("files" in root)){
		fileLength = files.length;
	}
	if(("dirs" in root)){
		dirLength = dirs.length;
	}
		
	if(count==(dirLength + fileLength))
		return 1;// all the subdirectories and files or current file is released
	else if(count==0)
		return 0;// no files or directories released
	else
		return -1;// few of the files/directories released
}
</script>
</head>

<body onload = 'prepareCollectionTable(<%=root%>)'>
<portal:header/>
<portal:menu active="projects"/>
<script type="text/javascript" src="https://code.jquery.com/jquery-3.1.1.min.js"></script>

<div id="content">
<% 
	if ( request.getAttribute("message") != null ) { 
%><div class="message"><%= (String) request.getAttribute("message") %></div>
<%	}

	//Collection collection = AdministrationServlet.getCurrentCollection(request);
	if ( collection != null ) { %>
<h2><%= collection.getName() %></h2>
<% String description = (collection.getDescription()!=null)?collection.getDescription():""; %>	
<div style="border: 1px solid gray; padding: 10px; margin: 10px; "><%= description %></div>
	<%} %>
<h3>Files</h3>
<span>
	<a href="#" onclick= 'back();'><img src="<%= request.getContextPath() %>/resources/images/back.png"  height= 20></a>
	<script>function back() {
					if(currDir.parent!=undefined){
						prepareCollectionTable(currDir.parent);
						}
					else{
						 window.history.back();
					}
					return false;
					}
	</script>
</span>
<form id="collectionForm" method="post" >

<div id="table1"></div>

<p>Data type: <select name="data_type">
<option value="raw">Raw data</option>
<option value="report">Reports</option>
<option value="result">Results</option>
</select></p>
<p><b>Release notes</b><br>
<textarea rows="5" cols="40" name="release_notes"></textarea>
</p>
<button type="submit" name="action" value="release-files" onclick="return validateRelease()">Release Files</button>
<button type="submit" name="action" value="revoke-files" onclick="return validateRevoke()" >Revoke Files</button>
</form>
</div>
</body>
</html>
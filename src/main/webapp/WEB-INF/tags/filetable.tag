<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="files" required="true" type="com.google.gson.JsonObject" %>
<%@ tag import="edu.uic.cri.portal.model.entities.ProjectFiles,java.text.DateFormat,java.text.SimpleDateFormat,
edu.uic.cri.portal.dao.ReleaseDao,edu.uic.cri.portal.dao.ReleaseDaoImpl,edu.uic.cri.portal.model.entities.Release" %>
<script>

var currDir;

function prepareCollectionTable(root){
	currDir = root;
	var obj = root;
	
	var div = document.getElementById('table1');
	var table = document.createElement("TABLE");
	table.setAttribute("class", "table");
	var oldNode = div.childNodes[0];
	if(oldNode!=undefined){
		div.replaceChild(table,oldNode);
	}
	else{
		div.appendChild(table);
	}
	var row = table.insertRow(-1);
	var cell = row.insertCell(-1);
	
	cell = row.insertCell(-1);
	cell.innerHTML = "File Name";
	cell = row.insertCell(-1);
	cell.innerHTML = "Released By";
	cell = row.insertCell(-1);
	cell.innerHTML = "Modified At";
	cell = row.insertCell(-1);
	cell.innerHTML = "Modified By";
	cell = row.insertCell(-1);
	cell.innerHTML = "Description";
	var dirs = obj.dirs;
	if(dirs!=undefined){
		for(var i=0;i< dirs.length;i++){
			
			if (obj.dirs[i].parent == undefined ) {
				obj.dirs[i].parent = obj;
			}
			var row = table.insertRow(-1);
			var cell = row.insertCell(-1);
			
			elem = document.createElement("INPUT");
			elem.type = "checkbox";
			elem.name = i;
			elem.value = obj.files[i].fileid;
			elem.onclick = function() {
				onChangeCheck(currDir.dirs[this.name], this.checked);
			};
			cell.appendChild(elem);
			
			//filename
			cell = row.insertCell(-1);
			elem = document.createElement("A")
			elem.innerHTML = '<img src=\'<%= request.getContextPath() %>/resources/images/folder.png\'  height= 20>  ' + obj.dirs[i].name;
			elem.align='center';
			elem.class='folder';
			elem.name = i;
			elem.onclick = function() {
					prepareCollectionTable(currDir.dirs[this.name]); 
				};
			cell.appendChild(elem);
			
			//releasedby
			cell = row.insertCell(-1);
			cell.innerHTML = obj.dirs[i].releasedby;
			
			//modfiedat
			cell = row.insertCell(-1);
			cell.innerHTML = obj.dirs[i].modifiedat;
			
			//modifiedby
			cell = row.insertCell(-1);
			cell.innerHTML = obj.dirs[i].modifiedby;
			
			//description
			cell = row.insertCell(-1);
			cell.innerHTML = obj.dirs[i].description;
		}
	}
	
	var files = obj.files;
	if(files!=undefined){
		for(var i=0;i< files.length;i++){
			
			var row = table.insertRow(-1);
			
			cell = row.insertCell(-1)
			elem = document.createElement("INPUT");
			elem.type = "checkbox";
			elem.name = "zipList";
			elem.value = obj.files[i].fileid;
			cell.appendChild(elem);
			
			
			cell = row.insertCell(-1);
			elem = document.createElement("A");
			elem.name = i;
			elem.innerHTML = '<img src=\'<%= request.getContextPath() %>/resources/images/file.png\'  height= 20>  ' +obj.files[i].name;
			elem.href = "<%= request.getContextPath() %>/file/" + files[i].fileid;
			cell.appendChild(elem);
			cell.class='file';
			

			//releasedby
			cell = row.insertCell(-1);
			cell.innerHTML = obj.files[i].releasedby;
			
			//modfiedat
			cell = row.insertCell(-1);
			cell.innerHTML = obj.files[i].modifiedat;
			
			//modifiedby
			cell = row.insertCell(-1);
			cell.innerHTML = obj.files[i].modifiedby;
			
			//description
			cell = row.insertCell(-1);
			cell.innerHTML = obj.files[i].description;
		}
	}
	
}

function onChangeCheck(root, flag){
	var dirs = root.dirs;
	if(dirs!=undefined){
		for(var i=0;i< dirs.length;i++){
			onChangeCheck(dirs[i],flag);
		}
	}
	var files = root.files;
	if(files!=undefined){
		for(var i=0;i< files.length;i++){
			var elem = document.createElement("INPUT");
			elem.type = "hidden";
			elem.name = "zipList";
			elem.value = files[i].fileid;
			elem.style="display:none"
				elem.checked = flag;
			var oForm = document.getElementById('fileForm');
			oForm.appendChild(elem);
		}
	}
}

function back() {
	if(currDir.parent!=undefined){
		prepareCollectionTable(currDir.parent);
		}
	else{
		 window.history.back();
	}
	return false;
	}
// script="javascript:prepareTable(files)"
</script>

<div id = "table1" ></div>
<a href="#" onclick= 'back();'>
<!--  <img src="<%= request.getContextPath() %>/resources/images/back.png"  height= 25> -->
<font size="+1">&#x21E6;</font></a>
<div id="preparetable">
	<script> prepareCollectionTable(<%=files.toString()%>);</script>
	 Back
</div>


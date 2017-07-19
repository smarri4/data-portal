<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ tag import="edu.uic.cri.portal.model.entities.ProjectMembership,java.text.DateFormat,edu.uic.cri.portal.UsersServlet" %>
<%@ attribute name="users" required="true" type="java.util.List" %>
<%@ attribute name="project" required="true" %>
<%
	boolean isAdmin = request.isUserInRole("admin");
	boolean isOwner = false;
	if ( ! isAdmin ) {
		ProjectMembership currentUser = UsersServlet.getMember(project, request.getRemoteUser());
        isOwner = currentUser.getOwner() == 1;
	}
    if ( isAdmin || isOwner ) {
%>
<script>
$(document).ready(function(){
    $("#addUserButton").click(function(){
        $("#addUser").load("<%= request.getContextPath() %>/addUser.jsp?projectId=<%= project %>");
    });
});

function updateUsers(users) {
	$("#project_users tbody").empty();
	var len = users.length;
	for ( var i = 0; i < len; i++ ) {
		var user = users[i];
		var row = "<tr><td>" + user.id + "</td><td>" + (user.owner ? "Owner" : "") + "</td><td>"
		if ( user.owner ) {
			row += '<button name="revoke-owner" value="' + user.id + '" type="button" class="btn btn-primary btn-sm">Revoke ownership</button>';
		} else {
			row += '<button name="make-owner" value="' + user.id + '"  type="button" class="btn btn-primary btn-sm">Make owner</button>';			
		}
		$("#project_users tbody").append(row + '<button name="remove-user" value="' + user.id + '"  type="button" class=btn btn-primary btn-sm>remove</button></td></tr>');		
	}
	$("#userForm button").click(modifyUsersAction);
}

function modifyUsersAction(e) {
	var action = this.form.action + "/" + this.name;
	$.ajax({
		url: action,
		type: "POST",
		data: { 'userid' : this.value }, 
		success: function (data, status, http) {
			if ( http.status == 200 ) {
				updateUsers(data.users);
			}
		}, 
		error: function (http, status, error) {
			alert(status);
		}
	});
	e.preventDefault();
//	e.unbind();
}

function addUsersAction(e) {
	var data = $( this.form ).serializeArray();
	$.ajax({
		url: this.form.action,
		type: "POST",
		data: data, 
		success: function (data, status, http) {
			if ( http.status == 200 ) {
				updateUsers(data.users);
			}
		}, 
		error: function (http, status, error) {
			alert(status);
		}
	});
	e.preventDefault();
//	e.unbind();
	$("#addUser").empty();
}
</script>
<% } %>
<form id="userForm" action="<%= request.getContextPath() %>/users/<%= project %>" method="post">
	<table id="project_users" class="table">
		<thead>
		<tr><th>Users</th>
		</thead>
		<tbody>
			<% 	for ( Object elem: users ) {
					if ( elem instanceof ProjectMembership ) { 
						ProjectMembership member = (ProjectMembership) elem;
			%>
			<tr>		
				<td><%= member.getId().getUserid() %></td>
				<td><%= member.getOwner() == 1 ? "Owner" : "" %></td>
				<% if ( isAdmin || isOwner) { %>
				<td><button name="<%= member.getOwner() == 1 ? "revoke-owner" : "make-owner" %>" value="<%= member.getId().getUserid() %>" type="button" class="btn btn-primary btn-sm"><%= member.getOwner() == 1 ? "Revoke ownership" : "Make owner" %></button>
				<button name="remove-user" value="<%= member.getId().getUserid() %>" type="button" class="btn btn-primary btn-sm">remove</button></td>
				<% } %>
			</tr>	
			<% } } %>
		</tbody>
	</table>
</form>
<script>
// Bind the click-event on all input with type=submit
$("#userForm button").click(modifyUsersAction);
</script>
<% if ( isAdmin || isOwner ) {
	System.out.println(isOwner);%>
<div id="addUser"></div>
<button id="addUserButton" class="btn btn-primary btn-sm">add user</button>
<% } %>
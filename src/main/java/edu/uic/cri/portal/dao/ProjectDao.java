package edu.uic.cri.portal.dao;

import java.util.List;

import javax.servlet.ServletException;

import edu.uic.cri.portal.model.entities.ProjectMembership;
import edu.uic.cri.portal.model.entities.User;

/**
 * This Interface is used to query the project_files and project_membership tables in the database. 
 * @author SaiSravith
 *
 */
public interface ProjectDao {

	List<String> getProjectOwners(String projectid);

	List<String> getProjectsForUser(String userid);
	
	List<String> getProjectUsers(String userid);
	
	String addProjectMembership(ProjectMembership projectMembership);

	List<ProjectMembership> getProjectMembershipById(String projectid);

	List<User> getAllUsers();

	void removeUsersFromProject(String projectid, String[] users);

	List<String> getAllPortalProjects();

	void addUsersToProject(ProjectMembership projectMembership) throws ServletException;

	List<String> getRecentProjectsForUser(String userid);

	String getProjectForRelease(long releaseid);

	List<User> getAdminUsers();

}

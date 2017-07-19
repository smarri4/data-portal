package edu.uic.cri.portal.dao;

import java.util.List;
import java.util.Map;

import edu.uic.cri.portal.model.entities.ProjectFiles;

/**
 * This Interface is used to query the project_files and project_membership tables in the database. 
 * @author SaiSravith
 *
 */
public interface FileDao {

	//String addProjectFiles(ProjectFiles projectFiles);
	List<ProjectFiles> getProjectFile(String projectId);
	List<ProjectFiles> getFiles(String projectId, String type);
	
	Map<String,ProjectFiles> getFileMap(String projectId);
	int revokeFiles(String[] fileIDs);
	int revokeReleaseFiles(Long releaseId);
}

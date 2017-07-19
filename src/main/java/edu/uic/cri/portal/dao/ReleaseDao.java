package edu.uic.cri.portal.dao;

import java.util.List;

import edu.uic.cri.portal.model.entities.ProjectFiles;
import edu.uic.cri.portal.model.entities.Release;

/**
 * This interface is used to query the project_release tables in the database. 
 * @author SaiSravith
 *
 */
public interface ReleaseDao {

	List<Release> getAllReleases();
	
	Long addRelease (Release release, List<ProjectFiles> files);

	List<Release> getRecentActivityForUser(String userid);

	List<Release> getOverallActivityForUser(String userid);

	Release getReleaseById(long releaseid);

	List<ProjectFiles> getProjectFilesForRelease(long releaseid);

	int removeReleases(List<Long> releaseList);
}

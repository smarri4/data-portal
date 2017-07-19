package edu.uic.cri.portal.model.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
/**
 * A JPA (hibernate) entity for the "project_files" table
 * @author SaiSravith
 *
 */
@Entity
@Table(name = "project_files")
public class ProjectFiles {
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="releaseid")
	private List<Release> releases;	

	@NotNull
	@Id
	@Column(name = "fileid")
	private String fileid;
	
	@NotNull
	@Column(name = "projectid")
	private String projectid;

	@NotNull
	@Column(name = "releaseid")
	private long releaseid;

	@NotNull
	@Column(name = "type")
	private String type;
	
	@NotNull
	@Column(name = "modified_at")
	private Date modified_at;
	
	@NotNull
	@Column(name = "modified_by")
	private String modified_by;
	
	@NotNull
	@Column(name = "description")
	private String description;
	
	@NotNull
	@Column(name = "hidden")
	private Boolean hidden;
	
	
	ProjectFiles() {
		
	}

	/**
	 * 
	 * @param projectID
	 * @param fileID
	 * @param releaseID
	 * @param type
	 * @param description
	 * @param modified_by
	 */
	public ProjectFiles(String projectID, String fileID, long releaseID, String type, String description, String modified_by) {
		this.fileid = fileID;
		this.projectid = projectID;
		
		this.releaseid = releaseID;
		this.type = type;
		this.description = description;
		
		this.modified_by = modified_by;
		this.modified_at = new Date();
	}
	/**
	 * 
	 * @param projectID
	 * @param fileID
	 * @param type
	 * @param description
	 * @param modified_by
	 * @param hidden
	 */
	public ProjectFiles(String projectID, String fileID, String type, String description, String modified_by, Boolean hidden) {
		this.fileid = fileID;
		this.projectid = projectID;
		
		this.releaseid = 0;
		this.type = type;
		this.description = description;
		this.hidden = hidden;
		
		this.modified_by = modified_by;
		this.modified_at = new Date();
	}
	/**
	 * 
	 * @return fileid
	 */
	public String getID() {
		return this.fileid;
	}
	/**
	 * 
	 * @return projectid
	 */
	public String getProjectID() {
		return this.projectid;
	}
	/**
	 * 
	 * @return releaseid
	 */
	public long getReleaseid() {
		return releaseid;
	}
	/**
	 * 
	 * @param releaseid
	 */
	public void setReleaseid(long releaseid) {
		this.releaseid = releaseid;
	}
	/**
	 * 
	 * @return type
	 */
	public String getType() {
		return type;
	}
	/**
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * 
	 * @return modified_at
	 */
	public Date getModified_at() {
		return modified_at;
	}
	/**
	 * 
	 * @param modified_at
	 */
	public void setModified_at(Date modified_at) {
		this.modified_at = modified_at;
	}
	/**
	 * 
	 * @return modified_by
	 */
	public String getModified_by() {
		return modified_by;
	}
	/**
	 * 
	 * @param modified_by
	 */
	public void setModified_by(String modified_by) {
		this.modified_by = modified_by;
	}
	/**
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * 
	 * @return releasesList
	 */
	public List<Release> getReleases(){
		return this.releases;
	}
	/**
	 * 
	 * @param releases
	 */
	public void setReleases(List<Release> releases){
		this.releases = releases;
	}

}

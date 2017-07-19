package edu.uic.cri.portal.model.entities.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * Primary Key for ProjectFiles entity class
 * @author SaiSravith
 *
 */
@Embeddable
public class project_files_pk implements Serializable { 
	@NotNull
    @Column(name="projectid")
    private String projectid;

    @NotNull
    @Column(name="fileid")
    private String fileid;

	public String getProjectid() {
		return projectid;
	}

	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}

	public String getFileid() {
		return fileid;
	}

	public void setFileid(String fileid) {
		this.fileid = fileid;
	}
}
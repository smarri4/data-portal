package edu.uic.cri.portal.model.entities.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * Primary Key for ProjectMembership entity class
 * @author SaiSravith
 *
 */
@Embeddable
public class project_membership_pk implements Serializable { 
	@NotNull
    @Column(name="projectid")
    private String projectid;

    @NotNull
    @Column(name="userid")
    private String userid;

	public String getProjectid() {
		return projectid;
	}

	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

}
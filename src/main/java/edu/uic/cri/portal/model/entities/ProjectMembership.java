package edu.uic.cri.portal.model.entities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import edu.uic.cri.portal.model.entities.pk.project_membership_pk;
/**
 * A JPA (hibernate) entity for the "project_membership" table
 * @author SaiSravith
 *
 */
@Entity
@Table(name = "project_membership")
public class ProjectMembership {

	public ProjectMembership() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @param projectid
	 * @param userid
	 * @param owner
	 */
	public ProjectMembership(String projectid, String userid, int owner) {
		this.id = new project_membership_pk();
		this.id.setProjectid(projectid);
		this.id.setUserid(userid);
		this.owner = owner;
	}

	@NotNull
	@EmbeddedId
	project_membership_pk id;

	@NotNull
	@Column(name = "owner")	  
	private int owner;

	/**
	 * 
	 * @return id
	 */
	public project_membership_pk getId() {
		return id;
	}
	/**
	 * 
	 * @param id
	 */
	public void setId(project_membership_pk id) {
		this.id = id;
	}
	/**
	 * 
	 * @return owner
	 */
	public int getOwner() {
		return owner;
	}
	/**
	 * 
	 * @param owner
	 */
	public void setOwner(int owner) {
		this.owner = owner;
	}
}

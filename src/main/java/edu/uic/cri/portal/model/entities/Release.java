package edu.uic.cri.portal.model.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
/**
 * A JPA (hibernate) entity for the "project_release" table
 * @author SaiSravith
 *
 */
@Entity
@Table(name = "project_release")
public class Release {

	public Release() { }


	public Release(Date release_date, String description) {
		this.release_date = release_date;
		this.description = description;
	}

	public Release(String released_by,String description) {
		this.release_date = new Date();
		this.description = description;
		this.released_by = released_by;
	}

	@NotNull
	@Id @GeneratedValue
	@Column(name = "releaseid")
	private long releaseid;
	
	@NotNull
	@Column(name = "released_by")
	private String released_by;

	@NotNull
	@Column(name = "release_date")
	private Date release_date;

	@NotNull
	@Column(name = "description")
	private String description;

	public long getReleaseid() {
		return releaseid;
	}
	public void setReleaseid(long releaseid) {
		this.releaseid = releaseid;
	}
	public Date getRelease_date() {
		return release_date;
	}
	public void setRelease_date(Date release_date) {
		this.release_date = release_date;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getRelease_by() {
		return released_by;
	}


	public void setRelease_by(String release_by) {
		this.released_by = release_by;
	}


	public String toString(){
		return " releaseid: " + releaseid + " release_date: " + release_date + " description: " + description;
	}
}

package edu.uic.cri.portal.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


/**
 * A JPA (hibernate) entity for the "users" table
 * @author SaiSravith
 *
 */
@Entity
@Table(name = "users")
public class User {

  // ------------------------
  // PRIVATE FIELDS
  // ------------------------
   
  // The user's email
  @NotNull
  @Id
  @Column(name = "userid")
  private String userid;
  
  // The user's name
  
  @Column(name = "name")
  private String name;
  
  @Column(name = "pass")
  private String pass;

  @Column(name = "affiliation")
  private String affiliation;
  
  @NotNull
  @Column(name = "role")
  private String role;
  // ------------------------
  // PUBLIC METHODS
  // ------------------------
  
  public User() { }

  /**
   * 
   * @param userid
   */
  public User(String userid) { 
    this.userid = userid;
  }
  /**
   * 
   * @param userid
   * @param role
   */
  public User(String userid, String role) {
	    this.userid = userid;
	    this.role = role;
}
  /**
   * 
   * @param userid
   * @param name
   * @param role
   * @param affiliation
   */
  public User(String userid, String name, String role, String affiliation) {
	    this.userid = userid;
	    this.name = name;
	    this.role = role;
	    this.affiliation = affiliation;
  }
  /**
   * 
   * @param userid
   * @param name
   * @param affiliation
   */
  public User(String userid, String name, String affiliation) {
	    this.userid = userid;
	    this.name = name;
	    this.role = "customer";
	    this.affiliation = affiliation;
  }

  // Getter and setter methods

  /**
   * 
   * @return userid
   */
  public String getUserid() {
    return userid;
  }
  /**
   * 
   * @param value
   */
  public void setUserid(String value) {
    this.userid = value;
  }
  /**
   * 
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * 
   * @param value
   */
  public void setName(String value) {
    this.name = value;
  }
/**
 * 
 * @return affiliation
 */
public String getAffiliation() {
	return affiliation;
}
/**
 * 
 * @param affiliation
 */
public void setAffiliation(String affiliation) {
	this.affiliation = affiliation;
}
/**
 * 
 * @return pass
 */
public String getPass() {
	return pass;
}
/**
 * 
 * @param pass
 */
public void setPass(String pass) {
	this.pass = pass;
}
/**
 * 
 * @return role
 */
public String getRole() {
	return role;
}
/**
 * 
 * @param role
 */
public void setRole(String role) {
	this.role = role;
}
/**
 * String representation of user fields
 */
public String toString(){
	return "userid: "+ userid+" name : "+name+" affiliation: "+affiliation+" role: "+role;
}
  
}
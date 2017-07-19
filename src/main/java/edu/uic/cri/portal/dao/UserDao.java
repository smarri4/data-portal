package edu.uic.cri.portal.dao;

import edu.uic.cri.portal.model.entities.User;

/**
 * This interface is used to query the user tables in the database. 
 * @author SaiSravith
 *
 */
public interface UserDao{
  /**
   * Return the user having the passed email or null if no user is found.
   * 
   * @param email the user email.
   */
public User getUser(String userid);

public int updateUser(User user);

void removeMembers(String projectid, String[] users);

void addAdmin(User user) throws Exception;

}

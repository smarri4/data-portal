package edu.uic.cri.portal.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import edu.uic.cri.portal.model.entities.ProjectMembership;
import edu.uic.cri.portal.model.entities.User;
import edu.uic.cri.portal.model.entities.pk.project_membership_pk;

/**
 * This DAO is used to query the project_files and project_membership tables in the database. 
 * @author SaiSravith
 *
 */

public class ProjectDaoImpl implements ProjectDao {
	
	private static SessionFactory factory; 
	
    public ProjectDaoImpl(){
   	 try{
   		 Configuration configuration = new Configuration();
   		 configuration.addAnnotatedClass(User.class);
            factory = configuration.configure().buildSessionFactory();
         }catch (Throwable ex) { 
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex); 
         }
   }

//	public ProjectMembership getProjectMembershipById(String projectid){
//		Session session = factory.openSession();
//		ProjectMembership projectMembership = new ProjectMembership();
//		session.load(projectMembership, projectid);
//		
//		return projectMembership;
//	}
    /**
     * adds the project membership to the membership table, also adds the user to the user table if the user doesn't exist yet
     * @param projectMembership
     */
    @Override
    public void addUsersToProject(ProjectMembership projectMembership){
    	Session session = factory.openSession();
    	Transaction tx =null;
    	try {
    		tx = session.beginTransaction();
    	String userid = projectMembership.getId().getUserid();
    	SQLQuery query = session.createSQLQuery("SELECT userid from users where userid=:userid");
    	query.setParameter("userid", userid);
    	List<User> userList = (List<User>)(Object)query.list();
    	if(userList.isEmpty()){
    		User user = new User(userid,"customer");
    		session.save(user);
    	}
    	session.save(projectMembership);
    	tx.commit();
    	} catch (HibernateException e) {
			if ( tx != null ) tx.rollback();
			e.printStackTrace();
		}finally{
			session.close();
		}
    }
    /**
     * @param projectid
     * @return ProjectMembership list for the given projectid
     */
    @Override
	public List<ProjectMembership> getProjectMembershipById(String projectid){		
		Query query = factory.openSession().createQuery("from ProjectMembership where projectid = :projectid");
		query.setParameter("projectid", projectid);
		return (List<ProjectMembership>)(Object)query.list();
	}
    
    /**
     * @param projectid
     * @return Project owner list
     */
	@Override
	public List<String> getProjectOwners(String projectid){
		Query query = factory.openSession().createQuery("SELECT id.userid from ProjectMembership where projectid = :projectid and owner = :owner");
		query.setParameter("projectid", projectid);
		query.setParameter("owner", 1);
		return (List<String>)(Object)query.list();
	}
	/**
	 * @param projectid
	 * @return ProjectUserList
	 */
	@Override
	public List<String> getProjectUsers(String projectid){
		Query query = factory.openSession().createQuery("SELECT id.userid from ProjectMembership where projectid = :projectid");
		query.setParameter("projectid", projectid);
		return (List<String>)(Object)query.list();
	}
	/**
	 * @param userid
	 * @return UserList
	 */
	@Override
	public List<String> getRecentProjectsForUser(String userid){
		SQLQuery query = factory.openSession().createSQLQuery("select distinct pf.projectid from project_files pf where pf.projectid in (select pm.projectid from project_membership pm where pm.userid = :userid) order by pf.releaseid desc limit 10");
		query.setParameter("userid", userid);
		return (List<String>)(Object)query.list();
	}
	
	/**
	 * @return userList
	 */
	@Override
	public List<User> getAllUsers(){
		Query query = factory.openSession().createQuery("from User where userid in (SELECT id.userid from ProjectMembership)");
		return (List<User>)(Object)query.list();
	}
	
	/**
	 * @return userList
	 */
	@Override
	public List<User> getAdminUsers(){
		Query query = factory.openSession().createQuery("from User where role = 'admin'");
		return (List<User>)(Object)query.list();
	}
	
	/**
	 * @param userId
	 * @return List of projects for the user
	 */
	public List<String> getProjectsForUser(String userId){
		Query query = factory.openSession().createQuery("SELECT id.projectid from ProjectMembership where userid = :userid");
		query.setParameter("userid", userId);
		return (List<String>)(Object)query.list();
	}
	
	/**
	 * @param projectMembership
	 * @return projectId
	 */
	public String addProjectMembership(ProjectMembership projectMembership){
		Session session = factory.openSession();
		Transaction tx = null;
		project_membership_pk  project_membership_pk= null;
		//String projectid = null;
		try{
	        tx = session.beginTransaction();
	        project_membership_pk = (project_membership_pk) session.save(projectMembership);
	        tx.commit();
	        System.out.println("ProjectMembership saved successfully.....!!");
	    }catch (HibernateException e) {
	       if (tx!=null) tx.rollback();
	       e.printStackTrace(); 
	    }finally {
	       session.close(); 
	    }
		return project_membership_pk.getProjectid();
	}

	/**
	 * @param projectid
	 * @param users List
	 */
	@Override
	public void removeUsersFromProject(String projectid,String[] users) {
		String hql = "DELETE FROM ProjectMembership WHERE projectId= :projectId AND userid= :userid";
        Query query = factory.openSession().createQuery(hql);
        query.executeUpdate();
	}
	
	/**
	 * @return List of all portal Project
	 */
	@Override
	public List<String> getAllPortalProjects() {
		Query query = factory.openSession().createQuery("select distinct id.projectid FROM ProjectMembership");
		return (List<String>)(Object)query.list();
	}
	/**
	 * @param releaseid
	 * @return Projects  for release
	 */
	@Override
	public String getProjectForRelease(long releaseid) {
		Query query = factory.openSession().createQuery("select distinct id.projectid FROM ProjectFiles where releaseid = :releaseid");
		query.setParameter("releaseid", releaseid);
		query.setMaxResults(1);
		return ((List<String>)(Object)query.list()).get(0);
	}
	
	
}



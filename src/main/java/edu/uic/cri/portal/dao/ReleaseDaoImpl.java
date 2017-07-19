package edu.uic.cri.portal.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import edu.uic.cri.portal.model.entities.ProjectFiles;
import edu.uic.cri.portal.model.entities.Release;

/**
 * This DAO is used to query the project_release tables in the database. 
 * @author SaiSravith
 *
 */
public class ReleaseDaoImpl implements ReleaseDao {
	
	private static SessionFactory factory; 
	
	public ReleaseDaoImpl(){
	   	 try{
	   		 Configuration configuration = new Configuration();
	            factory = configuration.configure().buildSessionFactory();
	      		 configuration.addAnnotatedClass(Release.class);
	         }catch (Throwable ex) { 
	            System.err.println("Failed to create sessionFactory object." + ex);
	            throw new ExceptionInInitializerError(ex); 
	         }
	   }
	/**
	 * @param releaseid
	 * @return the Release object for the given releaseid
	 */
	@Override
	public Release getReleaseById(long releaseid){
		Query query = factory.openSession().createQuery("from Release where releaseid = :releaseid");
		query.setParameter("releaseid", releaseid);
		return ((List<Release>)(Object)query.list()).get(0);
	}

	/**
	 * @return List of all releases
	 */
	@Override
	public List<Release> getAllReleases(){
		SQLQuery query = factory.openSession().createSQLQuery("select * from project_release pr where pr.releaseid in( select distinct pf.releaseid from project_files pf) order by pr.releaseid desc");
		query.addEntity(Release.class);
		return (List<Release>)(Object)query.list();
	}
	
	/**
	 * @param userid
	 * @return List of releases for the given user 
	 */
	@Override
	public List<Release> getOverallActivityForUser(String userid){
		SQLQuery query = factory.openSession().createSQLQuery("select * from project_release pr where pr.releaseid in( select distinct pf.releaseid from project_files pf inner join project_membership pm on pf.projectid=pm.projectid where userid = :userid) order by pr.releaseid desc");
		query.setParameter("userid", userid);
		query.addEntity(Release.class);
		return (List<Release>)(Object)query.list();
	}
	
	/**
	 * @param userid
	 * @return List of 10 recent releases for the user
	 */
	@Override
	public List<Release> getRecentActivityForUser(String userid){
		SQLQuery query = factory.openSession().createSQLQuery("select * from project_release pr where pr.releaseid in( select distinct pf.releaseid from project_files pf inner join project_membership pm on pf.projectid=pm.projectid where userid = :userid) order by pr.releaseid desc limit 10");
		query.setParameter("userid", userid);
		query.addEntity(Release.class);
		return (List<Release>)(Object)query.list();
	}
	
	/**
	 * @param releaseid
	 * @return List of files for the given release 
	 */
	@Override
	public List<ProjectFiles> getProjectFilesForRelease(long releaseid){
		SQLQuery query = factory.openSession().createSQLQuery("select * from project_files  where releaseid= :releaseid");
		query.setParameter("releaseid", releaseid);
		query.addEntity(ProjectFiles.class);
		return (List<ProjectFiles>)(Object)query.list();
	}

	/**
	 * adds the Release to the database and the files that correspond to the release
	 * @param Release
	 */
	public Long addRelease (Release release, List<ProjectFiles> files) {
		Session session = factory.openSession();
		Transaction tx = null;
		Long id = new Long(-1);
		try {
	        tx = session.beginTransaction();
	        id = (Long) session.save(release);
	        for ( ProjectFiles file : files ) {
	        	file.setReleaseid(id);
	        	session.save(file);
	        }        
	        tx.commit();
	    } catch (HibernateException e) {
	       if ( tx != null ) tx.rollback();
	       e.printStackTrace(); 
	    } finally {
	       session.close(); 
	    }
		return id;

	}
	@Override
	public int removeReleases(List<Long> releaseList) {
		Session session = factory.openSession();
		Transaction tx = null;
		int rowcount = -1;		try {
	        tx = session.beginTransaction();
	        String queryString = "";
	        for(Long relId : releaseList){
	        	queryString+="delete from project_release where releaseid = " +relId+";";
//	        	queryString+="delete from project_files where releaseid = " +relId+";";
	        }
        	SQLQuery query = factory.openSession().createSQLQuery(queryString);
			query.addEntity(Release.class);
			query.addEntity(ProjectFiles.class);
			rowcount = query.executeUpdate();
	        tx.commit();
	    } catch (HibernateException e) {
	       if ( tx != null ) tx.rollback();
	       e.printStackTrace(); 
	    } finally {
	       session.close(); 
	    }
		return rowcount;

		
	}
}

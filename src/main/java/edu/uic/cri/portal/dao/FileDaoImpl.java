package edu.uic.cri.portal.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import edu.uic.cri.portal.model.entities.ProjectFiles;
import edu.uic.cri.portal.model.entities.Release;
import edu.uic.cri.portal.model.entities.User;

/**
 * This DAO is used to query the project_files and project_membership tables in the database. 
 * @author SaiSravith
 *
 */

public class FileDaoImpl implements FileDao {
	
	private static SessionFactory factory; 
	
    public FileDaoImpl(){
   	 try{
   		 Configuration configuration = new Configuration();
   		 configuration.addAnnotatedClass(User.class);
            factory = configuration.configure().buildSessionFactory();
         }catch (Throwable ex) { 
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex); 
         }
   }

/*	public String addProjectFiles(ProjectFiles projectFiles){
		Session session = factory.openSession();
		Transaction tx = null;
		String projectid = null;
		try{
	        tx = session.beginTransaction();
	        projectid = (String) session.save(projectFiles);
	        tx.commit();
	        System.out.println("ProjectFiles saved successfully.....!!");
	    }catch (HibernateException e) {
	       if (tx!=null) tx.rollback();
	       e.printStackTrace(); 
	    }finally {
	       session.close(); 
	    }
		return projectid;
	}*/
	/**
	 * @param projectid
	 * @return Project files list
	 */
	@Override
	public List<ProjectFiles> getProjectFile(String projectId) {
		System.out.println("projectId" + projectId);
		Query query = factory.openSession().createQuery("from ProjectFiles where projectId = :projectId");
		query.setParameter("projectId", projectId);
		return (List<ProjectFiles>)query.list();
	}

	/**
	 * @param projectId
	 * @param file type
	 * @return list of project files of given type
	 */
	@Override
	public List<ProjectFiles> getFiles(String projectId, String type) {
		Query query = factory.openSession().createQuery("from ProjectFiles where projectId = :projectId and type = :type and hidden = 0");
		query.setParameter("projectId", projectId);
		query.setParameter("type", type);
		return (List<ProjectFiles>)query.list();
	}
	
	/**
	 * @param projectId
	 * @return A map that holds the fileId and the files.
	 */
	@Override
	public Map<String,ProjectFiles> getFileMap(String projectId) {
		List<ProjectFiles> files = this.getProjectFile(projectId);
		Map<String, ProjectFiles> fileMap = new HashMap<String, ProjectFiles>(files.size());
		for ( ProjectFiles file : files ) {
			fileMap.put(file.getID(), file);
		}
		return fileMap;
	}

	@Override
	public int revokeFiles(String[] fileIDs) {
		Session session = factory.openSession();
		Transaction tx = null;
		int rowcount = -1;		
		try {
	        tx = session.beginTransaction();
	        String queryString = "";
	        for(String fid : fileIDs){
//	        	Query query1 = factory.openSession().createQuery("releaseid from ProjectFiles where fileid = :fileId");
//	        	query1.setParameter("fileId", fid);
	        	queryString="delete from project_files where fileid = :fileId";
	        	SQLQuery query = factory.openSession().createSQLQuery(queryString);
	        	query.setParameter("fileId", fid);
				query.addEntity(ProjectFiles.class);
				rowcount = query.executeUpdate();
			}
        	
	        tx.commit();
	    } catch (HibernateException e) {
	       if ( tx != null ) tx.rollback();
	       e.printStackTrace(); 
	    } finally {
	       session.close(); 
	    }
		return rowcount;
		
	}
	public int revokeReleaseFiles(Long releaseId) {
		Session session = factory.openSession();
		Transaction tx = null;
		int rowcount = -1;		
		try {
	        tx = session.beginTransaction();
	        String queryString="delete from project_files where releaseid = " +releaseId+";";
        	SQLQuery query = factory.openSession().createSQLQuery(queryString);
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



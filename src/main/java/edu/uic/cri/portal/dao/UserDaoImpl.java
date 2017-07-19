package edu.uic.cri.portal.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import edu.uic.cri.portal.model.entities.User;

/**
 * This DAO is used to query the user tables in the database. 
 * @author SaiSravith
 *
 */
public class UserDaoImpl implements UserDao{

	private static SessionFactory factory; 

    public UserDaoImpl(){
    	 try{
    		 Configuration configuration = new Configuration();
    		 configuration.addAnnotatedClass(User.class);
             factory = configuration.configure().buildSessionFactory();
          }catch (Throwable ex) { 
             System.err.println("Failed to create sessionFactory object." + ex);
             throw new ExceptionInInitializerError(ex); 
          }
    }
	@Override
	public void addAdmin(User user) throws Exception {
		Transaction tx =null;
		Session session = null;
		try{
		session = factory.openSession();
		tx = session.beginTransaction();
		session.save(user);
		tx.commit();
		}catch (HibernateException ex) { 
			if ( tx != null ) tx.rollback();
            throw new Exception("Failed to add admin user to the portal.");
         }finally{
 			session.close();
 		}
	}
    @Override
	public User getUser(String userid){
		Session session = factory.openSession();
		User user = new User();
		session.load(user, userid);
		return user;
		
	}
	@Override
	public int updateUser(User user) {
		
		Query query = factory.openSession().createQuery("update User set name = :name, affiliation = :affiliation, role = :role" +
				" where userid = :userid");
		query.setParameter("userid", user.getUserid());
		query.setParameter("name", user.getName());
		query.setParameter("affiliation", user.getAffiliation());
		query.setParameter("role", user.getRole());
		int result = query.executeUpdate();
		
		return result;
	}
	@Override
	public void removeMembers(String projectid, String[] users) {
		String hql = "DELETE FROM ProjectMembership WHERE projectId= :projectId AND userid= :userid";
        Query query = factory.openSession().createQuery(hql);
        query.executeUpdate();
	}
		
}

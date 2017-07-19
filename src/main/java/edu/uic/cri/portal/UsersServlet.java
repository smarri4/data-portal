package edu.uic.cri.portal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import edu.uic.cri.portal.dao.ProjectDao;
import edu.uic.cri.portal.dao.ProjectDaoImpl;
import edu.uic.cri.portal.dao.UserDao;
import edu.uic.cri.portal.dao.UserDaoImpl;
import edu.uic.cri.portal.mail.EmailType;
import edu.uic.cri.portal.model.entities.ProjectMembership;
import edu.uic.cri.portal.model.entities.User;

/**
 * Servlet implementation class UsersServlet
 */
@WebServlet({"/user", "/users/*"})
public class UsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	static SessionFactory factory;      

	static ProjectDao projectDao = new ProjectDaoImpl();
	static UserDao userDao = new UserDaoImpl();

	@Override
	public void init() throws ServletException {
		super.init();
		if ( factory  == null ) {
			Configuration configuration = new Configuration();
			factory = configuration.configure().buildSessionFactory();
		}
	}
	
	static Session getSession() {
		if ( factory  == null ) {
			Configuration configuration = new Configuration();
			factory = configuration.configure().buildSessionFactory();
		}
		return factory.openSession();
	}
	
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();

		// If there is more to the path, then there is a request for a project.
		if ( path != null && path.length() > 1 ) {
			String[] pathparts = path.split("/", 3);
			String projectid = pathparts[1];    	
		
			// Checks to see if the user is an admin or member of the project.  Otherwise send 403 message.			
			if ( ! req.isUserInRole("admin") ) {
				ProjectMembership member = getMember(projectid, req.getRemoteUser());
				if ( member == null ) {
					resp.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
			}
			
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			out.print(getJSON(ProjectManagementServlet.getProjectMembers(projectid)));
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();

		// If there is more to the path, then there is a request for a project.
		if ( path != null && path.length() > 1 ) {
			String[] pathparts = path.split("/", 3);
			if(pathparts[1].equals("add-admin")){
				try {
					userDao.addAdmin( new User(req.getParameter("userid"),req.getParameter("name"),"admin",req.getParameter("affiliation")));
					resp.sendRedirect("/manage-users");
				} catch (Exception e) {
					req.setAttribute("message", e.getMessage());
				}
				return;
			}
			String projectid = pathparts[1];    	

			// Checks to see if the user is an admin or owner of the project.  Otherwise send 403 message.			
			if ( ! req.isUserInRole("admin") ) {
				ProjectMembership member = getMember(projectid, req.getRemoteUser());
				if ( member == null || member.getOwner() == 0 ) {
					resp.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
			}

			// Check to see the action performed on the users.
			if ( pathparts.length == 3 ) {
				Session session = getSession();
				Transaction tx = null;

				try {
					tx = session.beginTransaction();
					if ( pathparts[2].equalsIgnoreCase("remove-user") ) {
						removeUsersFromProject(projectid, req.getParameter("userid"));
					} else if ( pathparts[2].equalsIgnoreCase("add-user") ) {
						projectDao.addUsersToProject(new ProjectMembership(projectid, req.getParameter("userid"), req.getParameter("owner") != null ? 1 : 0));
						//sending email to user saying that he/she has been added to the project
						ArrayList<String> userList = new ArrayList<String>();
						userList.add(req.getParameter("userid"));
						req.setAttribute("projectid", projectid);
						AdministrationServlet.sendEmail(req, resp, userList, EmailType.USER_ADD);
					} else if ( pathparts[2].equalsIgnoreCase("revoke-owner") ) {
						ProjectMembership member = getMember(projectid, req.getParameter("userid"));
						member.setOwner(0);
						session.update(member);
					} else if ( pathparts[2].equalsIgnoreCase("make-owner") ) {
						ProjectMembership member = getMember(projectid, req.getParameter("userid"));
						member.setOwner(1);
						session.update(member);
					}
					tx.commit();
				} catch (HibernateException e) {
					if ( tx != null ) tx.rollback();
					throw new ServletException(e); 
				} finally {
					session.close(); 
				}
			}
			
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			out.print(getJSON(ProjectManagementServlet.getProjectMembers(projectid)));
		}
	}
    /**
     * 
     * @param projectid
     * @param users
     */
	public static void removeMembers(String projectid, String[] users) {
		UserDao userDao = new UserDaoImpl();
		userDao.removeMembers(projectid, users);
	}
	/**
	 * 
	 * @param members
	 * @return ProjectMembershipList in JSON format
	 */
	public static String getJSON(List<ProjectMembership> members) {
		StringBuffer output = new StringBuffer("{ \"users\": [");
		Iterator<ProjectMembership> iter = members.iterator();
		
		if ( iter.hasNext() ) {
			output.append(getJSON(iter.next()));
			while ( iter.hasNext() ) {
				output.append(",");
				output.append(getJSON(iter.next()));
			}
		}
		output.append("] }");
		return output.toString();
	}
	
	/**
	 * 
	 * @param member
	 * @return ProjectMembership in JSON format
	 */
	public static String getJSON(ProjectMembership member) {
		StringBuffer output = new StringBuffer("{ \"id\" : \"");
		output.append(member.getId().getUserid());
		output.append("\", \"project\" : \"");
		output.append(member.getId().getProjectid());
		output.append("\", \"owner\" : ");
		if ( member.getOwner() == 1 ) {
			output.append("true }");
		} else {
			output.append("false }");
		}
		return output.toString();
		
	}
	/**
	 * 
	 * @param projectid
	 * @param userid
	 */
	public static void removeUsersFromProject(String projectid,String userid) {
		String hql = "DELETE FROM ProjectMembership WHERE id.projectid= :projectid AND userid= :userid";
        Query query = getSession().createQuery(hql);
        query.setParameter("userid", userid);
        query.setParameter("projectid", projectid);
        query.executeUpdate();
	}

	/**
	 * 
	 * @param projectid
	 * @param userid
	 * @return ProjectMembership
	 */
	public static ProjectMembership getMember(String projectid, String userid) {
		Query query = getSession().createQuery("FROM ProjectMembership WHERE id.projectid = :projectid AND userid = :userid");
		query.setParameter("projectid", projectid);
		query.setParameter("userid", userid);
		return (ProjectMembership)query.uniqueResult();
	}

}

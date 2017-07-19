package edu.uic.cri.portal;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import edu.uic.cri.portal.dao.ProjectDao;
import edu.uic.cri.portal.dao.ProjectDaoImpl;
import edu.uic.cri.portal.dao.UserDao;
import edu.uic.cri.portal.dao.UserDaoImpl;
import edu.uic.cri.portal.model.arvados.GroupManager;
import edu.uic.cri.portal.model.entities.User;
import edu.uic.cri.portal.model.entities.arvados.Group;

@WebServlet("/show-projects/*")
public class ArvadosPortalServlet extends HttpServlet {
	
	  private static final Logger logger = Logger.getLogger(Arvados.class);

	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException	{
		try {
		
		GroupManager grpMgr = new GroupManager();
		List<Group> groupList = new ArrayList<Group>();
		    
		if(req.isUserInRole("admin")){
			System.out.println("user is Admin");
			groupList = grpMgr.listGroups();
		    
		}
		else if(req.isUserInRole("customer")){
			System.out.println("user is customer");
			// get project list for this user from db

			ProjectDao projectDao = new ProjectDaoImpl();
			List<String> projectList = projectDao.getProjectsForUser(req.getRemoteUser());
			for(String project : projectList){
				groupList.add(grpMgr.getGroup(project));
			}
		}

		req.setAttribute("GROUP_LIST", groupList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	  private static void printResponse(Map response){
		    Set<Entry<String,Object>> entrySet = (Set<Entry<String,Object>>)response.entrySet();
		    for (Map.Entry<String, Object> entry : entrySet) {
		      if ("items".equals(entry.getKey())) {
		        List items = (List)entry.getValue();
		        for (Object item : items) {
		          System.out.println("    " + item);
		        }            
		      } else {
		        System.out.println(entry.getKey() + " = " + entry.getValue());
		      }
		    }
		  }
	
	 public static List<String> getProjectList(HttpServletRequest req) {
		 List<String> projlist = (List<String>) req.getAttribute("PROJECT_LIST");
		 if ( projlist == null ) {
			 User user = getUser(req);
			 ProjectDao projectDao = new ProjectDaoImpl();
			 projlist = projectDao.getProjectsForUser(user.getUserid()); 
			 req.setAttribute("PROJECT_LIST", projlist);
		 }
		 return projlist;
	 }
	  
	 public static User getUser(HttpServletRequest req) {
		 User user = (User) req.getAttribute("CURRENT_USER");
		 if ( user == null ) {
			 UserDao userDao = new UserDaoImpl();
			 user = userDao.getUser(req.getRemoteUser());
			 req.setAttribute("CURRENT_USER", user);
		 }
		 return user;
	 }
	  
	public List<User> getUsers(Connection conn){
		String sql = "select * from users";
		List<User> userList = null;
		
		try {
			userList = new ArrayList<User>();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				User user = new User();
				user.setName(rs.getString("name"));
				user.setUserid(rs.getString("userid"));
				user.setPass(rs.getString("pass"));
				user.setAffiliation(rs.getString("affiliation"));
				userList.add(user);
			}
			ps.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
			
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
		return userList;
	}

	
public void addUser(User user,Connection conn){
		
		String sql = "INSERT INTO users " +
				"(userid,pass, name, affiliation) VALUES (?, ?, ?, ?)";
		
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, user.getUserid());
			ps.setString(2, user.getPass());
			ps.setString(3, user.getName());
			ps.setString(4, user.getAffiliation());
			ps.executeUpdate();
			ps.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
			
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
	}
}

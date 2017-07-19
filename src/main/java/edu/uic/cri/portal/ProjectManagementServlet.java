package edu.uic.cri.portal;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import edu.uic.cri.arvados.ArvadosAPI;
import edu.uic.cri.portal.dao.FileDao;
import edu.uic.cri.portal.dao.FileDaoImpl;
import edu.uic.cri.portal.dao.ProjectDao;
import edu.uic.cri.portal.dao.ProjectDaoImpl;
import edu.uic.cri.portal.dao.ReleaseDao;
import edu.uic.cri.portal.dao.ReleaseDaoImpl;
import edu.uic.cri.portal.dao.UserDao;
import edu.uic.cri.portal.dao.UserDaoImpl;
import edu.uic.cri.portal.listener.AppRequestListener;
import edu.uic.cri.portal.listener.ProjectSyncScheduleListener;
import edu.uic.cri.portal.model.entities.ProjectFiles;
import edu.uic.cri.portal.model.entities.ProjectMembership;
import edu.uic.cri.portal.model.entities.Release;
import edu.uic.cri.portal.model.entities.User;
import edu.uic.cri.portal.model.entities.arvados.Group;
import edu.uic.cri.portal.model.entities.pk.project_membership_pk;

/**
 * This servlet acts as a controller for all the requests pertaining to portal customer activities like displaying the projects the customer is a part of
 * Project details, adding user to a project,  recent activity , releases, update profile etc.
 * @author SaiSravith
 *
 */
@WebServlet(urlPatterns = {"/projects/*", "/projects","/add-user","/activity", "/release/*", "/update-profile"})
public class ProjectManagementServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String PROJECT_ID_ATTR = "projectid";
	public static final String PROJECT_ATTR = "current_group";
	public static final String PROJECT_FILES_ATTR = "project_files";
	public static final String PROJECT_RELEASE_FILES_ATTR = "project_rel_files";
	public static final String PROJECT_USERS_ATTR = "project_users";
	public static final String PROJECT_VIEW_ATTR = "project_view";

	public static final String USERS_VIEW = "project_users";
	public static final String USERS_ADD = "add_user";
	public static final String RAWDATA_VIEW = "rawdata";
	public static final String RESULTS_VIEW = "results";
	public static final String REPORTS_VIEW = "reports";

	public static final String RELEASE_FILES_ATTR = "release_files";
	public static final String RELEASE_VIEW_ATTR = "release_view";

	static SessionFactory factory = null;
	static ReleaseDao releaseDao = new ReleaseDaoImpl();
	static UserDao userDao = new UserDaoImpl();
	static ProjectDao projectDao = new ProjectDaoImpl();
	static FileDao fileDao = new FileDaoImpl();

	@Override
	public void init() throws ServletException {
		super.init();
		if ( factory == null ) {
			Configuration configuration = new Configuration();
			factory = configuration.configure().buildSessionFactory();
		}
	}
	/**
	 * 
	 * @return Hibernate Session
	 */
	static Session getSession() {
		if ( factory  == null ) {
			Configuration configuration = new Configuration();
			factory = configuration.configure().buildSessionFactory();
		}
		return factory.openSession();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();

		System.out.println("ServletPath: " + request.getServletPath());
		// If there is more to the path, then there is a request for a project.
		if ( request.getServletPath().equals("/projects") && path != null && path.length() > 1 ) {
			String[] pathparts = path.split("/", 3);
			String projectid = pathparts[1];    	

			if ( ! request.isUserInRole("admin") ) {
				ProjectMembership member = UsersServlet.getMember(projectid, request.getRemoteUser());
				if ( member == null ) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
			}

			request.setAttribute(PROJECT_ID_ATTR, projectid);
			try {
				ArvadosAPI arv = AppRequestListener.getArvadosApi(request);
				request.setAttribute(PROJECT_ATTR, arv.getGroup(projectid));

				if ( pathparts.length > 2 && pathparts[2] != null ) {
					// TODO should change subview to return JSON that will be used by browser to update view (AJAX)
					String subview = pathparts[2];
					if ( subview.equals(RAWDATA_VIEW) ) { 
						request.setAttribute(PROJECT_FILES_ATTR, getFiles(projectid,"raw"));
					} else if ( subview.equals(REPORTS_VIEW) ) {
						request.setAttribute(PROJECT_FILES_ATTR, getFiles(projectid, "report"));
					} else if ( subview.equals(RESULTS_VIEW) ) {
						request.setAttribute(PROJECT_FILES_ATTR, getFiles(projectid, "result"));
					} else if ( subview.equals(USERS_VIEW) ) {
						request.setAttribute(PROJECT_USERS_ATTR, getProjectMembers(projectid));
					} else if ( subview.equals(USERS_ADD) ) {
						addProjectMembership(new ProjectMembership(projectid,request.getParameter("userid"),Integer.parseInt(request.getParameter("owner"))));
						request.setAttribute(PROJECT_USERS_ATTR, getProjectMembers(projectid));
					} else {
						subview = REPORTS_VIEW;
						request.setAttribute(PROJECT_FILES_ATTR, getFiles(projectid, "report"));
					}
					request.setAttribute(PROJECT_VIEW_ATTR, subview);
				} else {
					request.setAttribute(PROJECT_FILES_ATTR, getFiles(projectid, "report"));
					request.setAttribute(PROJECT_VIEW_ATTR, REPORTS_VIEW);
				}

				RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher("/project.jsp");
				reqDispatcher.forward(request, response);
			} catch (Exception e) {
				throw new ServletException(e);
			}
		} else if(request.getServletPath().equals("/projects")){
			try {
				request.setAttribute("GROUP_LIST", getMyProjects(request));
				RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher("/projects.jsp");
				reqDispatcher.forward(request, response);		
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}else if(request.getServletPath().equals("/activity")){
			try {
				RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher("/activity.jsp");
				reqDispatcher.forward(request, response);		
			} catch (Exception e) {
				throw new ServletException(e);
			}
		} else if(request.getServletPath().equals("/release")){
			try {
				String[] parts = path.split("/", 3);
				System.out.println(Arrays.deepToString(parts));
			String view = "/release.jsp";
			long releaseid = Long.parseLong(parts[1]);
			List<ProjectFiles> projectFiles = releaseDao.getProjectFilesForRelease(releaseid);
			String projid = projectDao.getProjectForRelease(releaseid);
			String projname = ProjectSyncScheduleListener.getProject(projid).getName();
			request.setAttribute("projectname", projname);
			request.setAttribute("releaseid", releaseid);
			request.setAttribute(PROJECT_RELEASE_FILES_ATTR, projectFiles);
			
			if ( parts.length > 2 && parts[2] != null ) {
				// TODO should change subview to return JSON that will be used by browser to update view (AJAX)
				String subview = parts[2];
				if ( subview.equals(RAWDATA_VIEW) ) { 
					request.setAttribute(RELEASE_FILES_ATTR, getFilesForRelease(releaseid,"raw"));
				} else if ( subview.equals(REPORTS_VIEW) ) {
					request.setAttribute(RELEASE_FILES_ATTR, getFilesForRelease(releaseid, "report"));
				} else if ( subview.equals(RESULTS_VIEW) ) {
					request.setAttribute(RELEASE_FILES_ATTR, getFilesForRelease(releaseid, "result"));
				} else {
					subview = REPORTS_VIEW;
					request.setAttribute(RELEASE_FILES_ATTR, getFilesForRelease(releaseid, "report"));
				}
				request.setAttribute(RELEASE_VIEW_ATTR, subview);
			} else {
				request.setAttribute(RELEASE_FILES_ATTR, getFilesForRelease(releaseid, "report"));
				request.setAttribute(RELEASE_VIEW_ATTR, REPORTS_VIEW);
			}
			RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher(view);
			reqDispatcher.forward(request, response);	
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		if(req.getServletPath().equals("/update-profile")){
			try {
			User user = new User(req.getRemoteUser(), req.getParameter("fullname"), req.getParameter("affiliation"));
			int result = userDao.updateUser(user);
			if(result < 0)
				throw new ServletException("Update Profile Failed!");
			RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher(req.getContextPath()+"/");
			reqDispatcher.forward(req, resp);
			} catch (Exception e) {
				throw new ServletException(e);
			}
		} else if ( path != null && path.length() > 1 ) {
			String[] pathparts = path.split("/", 3);
			String projectid = pathparts[1];   
			if ( ! req.isUserInRole("admin") ) {
				ProjectMembership member = UsersServlet.getMember(projectid, req.getRemoteUser());
				if ( member == null || member.getOwner() == 0 ) {
					resp.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
			}

			String subview = pathparts[2];
			if ( subview.equals(USERS_ADD) ) {
				//add user to the project
				addProjectMembership(new ProjectMembership(projectid,req.getParameter("userid"),Integer.parseInt(req.getParameter("owner"))));
				req.setAttribute(PROJECT_USERS_ATTR, getProjectMembers(projectid));
				RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher("/projects/"+ projectid+"/"+ USERS_VIEW );
				reqDispatcher.forward(req, resp);
			}
		}
	}

	/**
	 * 
	 * @param request
	 * @return JsonObject with the listed project files and directories
	 */
	@SuppressWarnings("unchecked")
	public static JsonObject getProjectFiles(HttpServletRequest request) {
		return (JsonObject) request.getAttribute(PROJECT_FILES_ATTR);
	}
	
	@SuppressWarnings("unchecked")
	public static List<ProjectFiles> getProjectReleaseFiles(HttpServletRequest request) {
		return (List<ProjectFiles>) request.getAttribute(PROJECT_RELEASE_FILES_ATTR);
	}

	/**
	 * 
	 * @param request
	 * @return Group
	 */
	public static Group getCurrentProject(HttpServletRequest request) {
		return (Group) request.getAttribute(PROJECT_ATTR);
	}

	/**
	 * 
	 * @param request
	 * @return ProjectMembershiplist
	 */
	@SuppressWarnings("unchecked")
	public static List<ProjectMembership> getCurrentMembers(HttpServletRequest request) {
		return (List<ProjectMembership>) request.getAttribute(PROJECT_USERS_ATTR);
	}

	/**
	 * 
	 * @param request
	 * @return current project_view
	 */
	public static String getCurrentView(HttpServletRequest request) {
		return (String) request.getAttribute(PROJECT_VIEW_ATTR);
	}
	
	/**
	 * 
	 * @param request
	 * @return currentReleaseView
	 */
	public static String getCurrentReleaseView(HttpServletRequest request) {
		return (String) request.getAttribute(RELEASE_VIEW_ATTR);
	}

	/**
	 * Get members of the project
	 * @param projectid Arvados UUID of the project
	 * @return ProjectMembers of the given project
	 */
	@SuppressWarnings("unchecked")
	public static List<ProjectMembership> getProjectMembers(String projectid) {
		Query query = getSession().createQuery("FROM ProjectMembership WHERE projectid = :projectid");
		query.setParameter("projectid", projectid);
		return (List<ProjectMembership>)query.list();
	}
	
	/**
	 * Get released files for a project and a given type ("raw","report","result")
	 * @param projectId Arvados UUID of the project
	 * @param type file type, i.e. "raw", "report", or "result"
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JsonObject getFiles(String projectId, String type) {
		List<ProjectFiles> pfList = fileDao.getFiles(projectId, type);
		DateFormat df = new SimpleDateFormat("EEE, MMM d, hh:mm a,yyyy.");
		JsonObject root = new JsonObject();
		for(ProjectFiles elem : pfList){
			Release release = releaseDao.getReleaseById(elem.getReleaseid());
			buildJson(root, elem.getID().split("/", 2)[1], elem.getID(), release.getRelease_by(), df.format(elem.getModified_at()), elem.getModified_by(), elem.getDescription(),elem.getType());
		}
		return root;
	}
	
	public static JsonObject buildJson(JsonObject root ,String path, String fileId, String releasedby, String modifiedat, String modifiedby, String description, String fileType){
		
		String[] parts = path.split("/");
		String name = parts[0];
		if(parts.length>=2){
			JsonArray subDirs = root.getAsJsonArray("dirs");
			if(subDirs==null){
        		JsonObject newDir = new JsonObject();
        		newDir.add("name", new JsonPrimitive(name));
        		newDir.add("releasedby", new JsonPrimitive(releasedby));
        		newDir.add("modifiedat", new JsonPrimitive(modifiedat));
        		newDir.add("modifiedby", new JsonPrimitive(modifiedby));
        		newDir.add("description", new JsonPrimitive(description));
        		 JsonArray array = new JsonArray();
        		 array.add(newDir);
        		 root.add("dirs",array);
        		 buildJson((JsonObject)newDir,path.substring(parts[0].length()+1), fileId, releasedby, modifiedat, modifiedby, description, fileType);
        		 return root;
        	}
			boolean contains=false;
			for(JsonElement elem : subDirs){
				if(((JsonObject)elem).get("name").getAsString().equals(name)){
					contains=true;
					buildJson((JsonObject)elem,path.substring(parts[0].length()+1), fileId, releasedby, modifiedat, modifiedby, description, fileType);
	    			return root;
				}
			}
			if(!contains){
				JsonObject newDir = new JsonObject();
        		newDir.add("name", new JsonPrimitive(name));
        		newDir.add("releasedby", new JsonPrimitive(releasedby));
        		newDir.add("modifiedat", new JsonPrimitive(modifiedat));
        		newDir.add("modifiedby", new JsonPrimitive(modifiedby));
        		newDir.add("description", new JsonPrimitive(description));
        		subDirs.getAsJsonArray().add(newDir);
        		buildJson((JsonObject)newDir,path.substring(parts[0].length()+1), fileId, releasedby, modifiedat, modifiedby, description, fileType);
        		return root;
			}
		}
		else{
			JsonArray files = root.getAsJsonArray("files");
			if(files!=null){
				JsonObject newFile = new JsonObject();
        		newFile.add("name", new JsonPrimitive(name));
        		newFile.add("filetype", new JsonPrimitive(fileType));
        		newFile.add("fileid", new JsonPrimitive(fileId));
        		newFile.add("releasedby", new JsonPrimitive(releasedby));
        		newFile.add("modifiedat", new JsonPrimitive(modifiedat));
        		newFile.add("modifiedby", new JsonPrimitive(modifiedby));
        		newFile.add("description", new JsonPrimitive(description));
        		files.getAsJsonArray().add(newFile);
			}
			else{
				JsonObject newFile = new JsonObject();
        		newFile.add("name", new JsonPrimitive(name));
        		newFile.add("filetype", new JsonPrimitive(fileType));
        		newFile.add("fileid", new JsonPrimitive(fileId));
        		newFile.add("releasedby", new JsonPrimitive(releasedby));
        		newFile.add("modifiedat", new JsonPrimitive(modifiedat));
        		newFile.add("modifiedby", new JsonPrimitive(modifiedby));
        		newFile.add("description", new JsonPrimitive(description));
        		JsonArray array = new JsonArray();
        		 array.add(newFile);
        		root.add("files",array);
			}
		}
		return root;
	}
	
	/**
	 * 
	 * @param releaseid
	 * @param type
	 * @return list of files for the given release and type of file
	 */
	@SuppressWarnings("unchecked")
	public static List<ProjectFiles> getFilesForRelease(long releaseid, String type) {
		Query query = getSession().createQuery("FROM ProjectFiles WHERE releaseid = :releaseid AND type = :type");
		query.setParameter("releaseid", releaseid);
		query.setParameter("type", type);
		return (List<ProjectFiles>)query.list();
	}

	/**
	 * 
	 * @param projectid
	 * @return ProjectFilesList
	 */
	public static JsonObject getRawData(String projectid) {
		return getFiles(projectid, "raw");
	}
	
	/**
	 * 
	 * @param projectid
	 * @return ProjectFilesList
	 */
	public static JsonObject getResults(String projectid) {
		return getFiles(projectid, "result");
	}

	/**
	 * 
	 * @param projectid
	 * @return ProjectFilesList
	 */
	public static JsonObject getReports(String projectid) {
		return getFiles(projectid, "report");
	}

	/**
	 * Get project for current user.
	 * @param request HttpServletRequest object
	 * @return list of current projects.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<Group> getMyProjects(HttpServletRequest request) throws Exception {
		Query query = getSession().createQuery("SELECT id.projectid FROM ProjectMembership WHERE userid = :userid");
		query.setParameter("userid", request.getRemoteUser());
		ArvadosAPI arv = AdministrationServlet.getArv(request);
		List<String> projectList = (List<String>)query.list();
		List<Group> myProjects = new ArrayList<Group>(projectList.size());
		for(String str: projectList){
			myProjects.add(arv.getGroup(str));
		}
		return myProjects;
	}	
	
	/**
	 * Add a project membership object. 
	 * @param projectMembership object to add.
	 * @return Project ID of project.
	 */
	// TODO Should move to UsersServlet
	public static String addProjectMembership(ProjectMembership projectMembership){
		Session session = factory.openSession();
		Transaction tx = null;
		project_membership_pk  project_membership_pk= null;
		//String projectid = null;
		try {
			tx = session.beginTransaction();
			project_membership_pk = (project_membership_pk) session.save(projectMembership);
			tx.commit();
		} catch (HibernateException e) {
			if (tx!=null) tx.rollback();
			throw e; 
		} finally {
			session.close(); 
		}
		return project_membership_pk.getProjectid();
	}
	/**
	 * If the user is a customer, checks to see if he is a part of the project and if the file is released to him.
	 * @param  request
	 * @param  path
	 * @return allowed Tells if the user is allowed to view the file or not
	 */
	public static boolean canReadFile(HttpServletRequest request, String path) {
		Session session = factory.openSession();
		boolean allowed=false;
		try {
			String sql = "SELECT pf.fileid FROM project_membership pm INNER JOIN project_files pf ON(pm.projectid=pf.projectid) WHERE userid = :userid AND fileid = :fileid";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter("userid", request.getRemoteUser());
			query.setParameter("fileid", path);
			allowed = query.list().size() > 0;
		} finally {
			session.close();
		}
		return allowed;
	}
	

}

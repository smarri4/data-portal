package edu.uic.cri.portal;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import edu.uic.cri.arvados.ArvadosAPI;
import edu.uic.cri.portal.dao.FileDao;
import edu.uic.cri.portal.dao.FileDaoImpl;
import edu.uic.cri.portal.dao.ProjectDao;
import edu.uic.cri.portal.dao.ProjectDaoImpl;
import edu.uic.cri.portal.dao.ReleaseDao;
import edu.uic.cri.portal.dao.ReleaseDaoImpl;
import edu.uic.cri.portal.dao.UserDao;
import edu.uic.cri.portal.dao.UserDaoImpl;
import edu.uic.cri.portal.listener.AppConfigListener;
import edu.uic.cri.portal.listener.AppRequestListener;
import edu.uic.cri.portal.listener.ProjectSyncScheduleListener;
import edu.uic.cri.portal.mail.EmailType;
import edu.uic.cri.portal.model.entities.ProjectFiles;
import edu.uic.cri.portal.model.entities.Release;
import edu.uic.cri.portal.model.entities.User;
import edu.uic.cri.portal.model.entities.arvados.Collection;
import edu.uic.cri.portal.model.entities.arvados.Filter;
import edu.uic.cri.portal.model.entities.arvados.Filter.Operator;
import edu.uic.cri.portal.model.entities.arvados.Group;

/**
 * This servlet acts as a controller for all the requests pertaining to portal administrative activities
 * @author Sai Sravith Reddy Marri
 */
@WebServlet(urlPatterns = {"/manage-projects", "/manage-projects/*" , "/show-release/*","/staged-files/*",
		"/remove-projects","/manage-users","/show-releases","/remove-users","/update-owner"})
@ServletSecurity(@HttpConstraint(rolesAllowed="admin"))
public class AdministrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String PORTAL_USERS = "portal_users";
	public static final String ADMIN_USERS = "admin_users";
	public static final String PROJECT_USERS_ATTR = "project_users";
	public static final String RELEASES = "releases";
	public static final String PROJECT_FILES = "project_files";
	private static Logger logger = Logger.getLogger(AdministrationServlet.class);

	static FileDao fileDao = new FileDaoImpl();
	static ProjectDao projectDao = new ProjectDaoImpl(); 
	static ReleaseDao releaseDao = new ReleaseDaoImpl();
	
	Context initCtx;
	Context envCtx;
	static Session session;
	
	@Override
	public void init() throws ServletException {
		super.init();
		try {
			initCtx = new InitialContext();
			envCtx = (Context) initCtx.lookup("java:comp/env");
			session = (Session) envCtx.lookup("mail/Session");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String servletPath = req.getServletPath();
		String pathInfo = req.getPathInfo();	
		System.out.println("Servletpath :"+ servletPath);
		System.out.println("pathInfo :"+ pathInfo);

		try {

			if (servletPath.equals("/remove-projects")) {

			} else if ( servletPath.equals("/manage-projects") ) {
				
				if ( pathInfo != null && pathInfo.length() > 1 ) {
					String[] parts = pathInfo.split("/", 3);
					ArvadosAPI arv = getArv(req);
					
					req.setAttribute("project", arv.getGroup(parts[1]));
					String view = "/admin/project.jsp";
					req.setAttribute(PROJECT_USERS_ATTR, projectDao.getProjectMembershipById(parts[1]));
					
					// It may be better to return the collection as a JSON and have the web client parse and display in page (AJAX)
					if ( parts.length == 3) {
						view = "/admin/collection.jsp";
						req.setAttribute("collection", arv.getCollection(parts[2]));
					}
					RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher(view);
					reqDispatcher.forward(req, resp);
				} else {  		
					RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher("/admin/projects.jsp");
					reqDispatcher.forward(req, resp);
				}
			} else if ( servletPath.equals("/manage-users") ) {
				String view = "/admin/users.jsp";
				List<User> users = projectDao.getAllUsers();
				req.setAttribute(PORTAL_USERS, users);
				List<User> adminUsers = projectDao.getAdminUsers();
				req.setAttribute(ADMIN_USERS, adminUsers);
				RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher(view);
				reqDispatcher.forward(req, resp);
			} else if ( servletPath.equals("/show-releases") ) {
				String view = "/admin/releases.jsp";
				List<Release> releases = releaseDao.getAllReleases();
				req.setAttribute(RELEASES, releases);
				RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher(view);
				reqDispatcher.forward(req, resp);
			} else if ( servletPath.equals("/show-release") ) {
				System.out.println(pathInfo);
				if ( pathInfo != null && pathInfo.length() > 1 ) {
					String[] parts = pathInfo.split("/", 3);
					System.out.println(Arrays.deepToString(parts));
				String view = "/admin/release.jsp";
				List<ProjectFiles> projectFiles = releaseDao.getProjectFilesForRelease(Long.parseLong(parts[1]));
				req.setAttribute(PROJECT_FILES, projectFiles);
				RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher(view);
				reqDispatcher.forward(req, resp);
				}
				else{
					throw new ServletException("Invalid URL, please specify releaseid");
				}
			} else if ( servletPath.equals("/staged-files") ) {
				logger.info("showing staged files");
				if ( pathInfo != null && pathInfo.length() > 1 ) {
					String[] parts = pathInfo.split("/", 3);
					System.out.println(Arrays.deepToString(parts));
				String view = "/admin/staged-files.jsp";
				List<ProjectFiles> projectFiles = releaseDao.getProjectFilesForRelease(Long.parseLong(parts[1]));
				req.setAttribute(PROJECT_FILES, projectFiles);
				RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher(view);
				reqDispatcher.forward(req, resp);
				}
				else{
					throw new ServletException("Invalid URL, please specify releaseid");
				}
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String servletPath = req.getServletPath();
		String pathInfo = req.getPathInfo();


		try {
			if ( servletPath.equals("/manage-projects") ) {

				if ( pathInfo != null && pathInfo.length() > 1 ) {
					String[] parts = pathInfo.split("/", 3);
					ArvadosAPI arv = getArv(req);

					req.setAttribute("project", arv.getGroup(parts[1]));
					String view = "/admin/project.jsp";
					req.setAttribute(PROJECT_USERS_ATTR, projectDao.getProjectMembershipById(parts[1]));

					// It may be better to return the collection as a JSON and have the web client parse and display in page (AJAX)
					if ( parts.length == 3) {
						view = "/admin/collection.jsp";
						req.setAttribute("collection", arv.getCollection(parts[2]));
						if ( "release-files".equalsIgnoreCase(req.getParameter("action")) ) {
							String[] fileIDs = req.getParameterValues("fileList");
							Set<String> fileIDSet = new HashSet<String>(Arrays.asList(fileIDs));
							List<ProjectFiles> files = new ArrayList<ProjectFiles>(fileIDSet.size());
							String[] hiddenVals = req.getParameterValues("hidden");
							Set<String> hidden=new HashSet<String>();
							if(hiddenVals!=null)
								hidden = new HashSet<String>(Arrays.asList(hiddenVals));
							for ( String fileID : fileIDSet ) {
								files.add(new ProjectFiles(parts[1], fileID, req.getParameter("data_type"), "", req.getRemoteUser(),hidden.contains(fileID)));
							}
							releaseDao.addRelease(new Release(req.getRemoteUser(),req.getParameter("release_notes")), files);
							req.setAttribute("message", "Added release to project");
							List<String> users = projectDao.getProjectUsers(parts[1]);
							sendEmail(req,resp,users,EmailType.FILE_RELEASE);
						}
						else if ( "revoke-files".equalsIgnoreCase(req.getParameter("action")) ) {
							String[] fileIDs = req.getParameterValues("revokeList");
							if(fileIDs==null)
								throw new Exception("Revoke list is empty");
							fileDao.revokeFiles(fileIDs);
							req.setAttribute("message", "Revoked files from project");
							//List<String> users = projectDao.getProjectUsers(parts[1]);
							//sendEmail(req,resp,users,EmailType.FILE_RELEASE);
						}
					}
					RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher(view);
					reqDispatcher.forward(req, resp);
				} else {  		
					RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher("/admin/projects.jsp");
					reqDispatcher.forward(req, resp);
				}
			}
			else if ( servletPath.equals("/show-releases") ) {
				String view = "/admin/releases.jsp";
				if ( "revoke-release".equalsIgnoreCase(req.getParameter("action")) ) {
					String[] releaseIDs = req.getParameterValues("revokeList");
					List<Long> releaseList = new ArrayList<Long>(releaseIDs.length);
					for(String relId : releaseIDs){
						releaseList.add(Long.parseLong(relId));
					}
					for(Long relId: releaseList){
						fileDao.revokeReleaseFiles(relId);
					}
					releaseDao.removeReleases(releaseList);
					
					req.setAttribute("message", "Removed releases from project");
					//send email to notify the release has been removed??
//					List<String> users = projectDao.getProjectUsers(parts[1]);
//					sendEmail(req,resp,users,EmailType.FILE_RELEASE);
					List<Release> releases = releaseDao.getAllReleases();
					req.setAttribute(RELEASES, releases);
					RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher(view);
					reqDispatcher.forward(req, resp);
				}
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	/**
	 * 
	 * @param request
	 * @return List of Projects in Arvados
	 * @throws Exception
	 */
	public static List<Group> getAllProjects(HttpServletRequest request) throws Exception { 
    	ArvadosAPI arv = AppRequestListener.getArvadosApi(request);
	    Filter[] filters = new Filter[1];
	    filters[0] = new Filter("group_class", Operator.EQUAL, "project");   
	    return arv.getGroups(filters);
    }
    
	/**
	 * 
	 * @param request
	 * @return ArvadosAPI
	 * @throws Exception
	 */
    public static ArvadosAPI getArv(HttpServletRequest request) throws Exception {
    	return AppRequestListener.getArvadosApi(request);
    }
    
    /**
     * 
     * @param request
     * @return current project
     */
    public static Group getCurrentProject(HttpServletRequest request) {
    	return (Group) request.getAttribute("project");
    }
    /**
     * 
     * @param request
     * @return collection
     */
    public static Collection getCurrentCollection(HttpServletRequest request) {
    	return (Collection) request.getAttribute("collection");
    }
    
    /**
     * 
     * @param request
     * @param uuid
     * @return project with this uuid
     * @throws Exception
     */
    public static Group getProject(HttpServletRequest request, String uuid) throws Exception {
    	ArvadosAPI arv = AppRequestListener.getArvadosApi(request);
    	return arv.getGroup(uuid);
    }
    /**
     * get the collections for the project
     * @param request
     * @param uuid
     * @return list of collections
     * @throws Exception
     */
    public static List<Collection> getCollectionsForProject(HttpServletRequest request, String uuid) throws Exception {
    	ArvadosAPI arv = AppRequestListener.getArvadosApi(request);
    	Filter[] filters = new Filter[1];
	    filters[0] = new Filter("owner_uuid", Operator.EQUAL, uuid);
    	return arv.getCollections(filters);
    }
    /**
     * Inmemory filemap that gives fast retrieval of files for a particular project
     * @param request
     * @return Project files map
     */
    public static Map<String,ProjectFiles> getCurrentFileMap(HttpServletRequest request) {
    	Group project = getCurrentProject(request);
    	return getFileMap(project.getUuid());
    }

    /**
     * 
     * @param projectID
     * @return filemap
     */
    public static Map<String,ProjectFiles> getFileMap(String projectID) {
    	return fileDao.getFileMap(projectID);
    }
    /**
     * get all projects in the portal
     * @return project list 
     */
    public static List<String> getPortalProjectList() {
    	return projectDao.getAllPortalProjects();
    }
    /**
     * Automatically sends email to the current user as
     * @param request
     * @param response
     * @param userList
     */
   static public void sendEmail(HttpServletRequest request, HttpServletResponse response, List<String> users, EmailType emailType){
	//get the resource bundle from mailtemplate.properties file
    	ResourceBundle bundle=null;
    	switch(emailType){
    	case FILE_RELEASE:
    		bundle = ResourceBundle.getBundle("files-release-template", request.getLocale());
    		break;
    	case USER_ADD:
    		bundle = ResourceBundle.getBundle("user-add-template", request.getLocale());
    		break;
    	}
	UserDao userDao = new UserDaoImpl();
	for(String userid : users){
		try {
			User user = userDao.getUser(userid);
			String url = request.getScheme()+"://"+request.getLocalName()+request.getContextPath()+"/manage-projects/"+request.getAttribute("projectid");
			Object[] arguments    = { (user.getName()==null)?"":user.getName(), ProjectSyncScheduleListener.getProject(request.getAttribute("projectid").toString()).getName(),url, AppConfigListener.getPORTAL_CONTACT_US()}; 
			String messageContent      = MessageFormat.format(
					bundle.getString("messageBody"), arguments);
			String messageSubject = MessageFormat.format(
					bundle.getString("messageSubject"), new Object[]{ProjectSyncScheduleListener.getProject(request.getAttribute("projectid").toString()).getName()});
			
			Message message = new MimeMessage(session);
			
			message.setFrom(new InternetAddress(AppConfigListener.getADMIN_EMAIL_ID()));//setting from address for the email
			InternetAddress to[] = new InternetAddress[1];
			to[0] = new InternetAddress(userid);//setting to address for the email
			message.setRecipients(Message.RecipientType.TO, to);
			message.setSubject(messageSubject);
			message.setContent(messageContent, "text/plain");
			Transport.send(message);
			System.out.println("Email sent successfully to : " + userid);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    }
}

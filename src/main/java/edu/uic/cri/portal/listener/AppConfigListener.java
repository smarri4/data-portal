package edu.uic.cri.portal.listener;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import edu.uic.cri.arvados.ArvadosAPI;

/**
 * Application Lifecycle Listener implementation class AppConfigListener
 *
 */
@WebListener
public class AppConfigListener implements ServletContextListener {

	private static final String DBNAME = "portaldb";
	private static final String DATASOURCE = "datasource";
	
	private static DataSource ds = null;
    
	private static String APINAME = null;
	private static String API_VERSION = null;
	private static String ARVADOS_API_TOKEN = null;
	private static String ARVADOS_API_HOST = null;
	private static String ARVADOS_API_HOST_INSECURE = null;
	private static String ADMIN_EMAIL_ID = null;
	private static String PORTAL_CONTACT_US = null;
	private static String FILE_PATH = null;


    public void contextInitialized(ServletContextEvent event)  { 
    	ServletContext context = event.getServletContext();

    	if ( APINAME == null )
    		APINAME = context.getInitParameter("apiName");    	
    	if ( API_VERSION == null )
    		API_VERSION = context.getInitParameter("apiVersion");
    	if ( ARVADOS_API_TOKEN == null )
    		ARVADOS_API_TOKEN = context.getInitParameter("ARVADOS_API_TOKEN");
    	if ( ARVADOS_API_HOST == null ) 
    		ARVADOS_API_HOST = context.getInitParameter("ARVADOS_API_HOST");
    	if ( ARVADOS_API_HOST_INSECURE == null ) 
    		ARVADOS_API_HOST_INSECURE = context.getInitParameter("ARVADOS_API_HOST_INSECURE");
    	if ( ADMIN_EMAIL_ID == null ) 
    		ADMIN_EMAIL_ID = context.getInitParameter("ADMIN_EMAIL_ID");
    	if ( PORTAL_CONTACT_US == null ) 
    		PORTAL_CONTACT_US = context.getInitParameter("PORTAL_CONTACT_US");
    	if ( FILE_PATH == null ) 
    		FILE_PATH = context.getInitParameter("FILE_PATH");
    	
    	try {
			Context initCtx = new InitialContext();
			
			if ( ds == null ) {
				ds  = (DataSource) initCtx.lookup("java:comp/env/jdbc/" + DBNAME);
			}
			context.setAttribute(DATASOURCE,ds);
    	} catch ( NamingException e) {
    		context.log("",e);
    	}
    	
   }    

    public void contextDestroyed(ServletContextEvent arg0)  { 

    }

    static DataSource getDatasource() { 
    	return ds;
    }

    public static Connection getConnection() throws SQLException { 
    	return ds.getConnection();
    }
	
    public static ArvadosAPI getArvadosApi() throws Exception{
    	return new ArvadosAPI(APINAME, API_VERSION, ARVADOS_API_TOKEN, ARVADOS_API_HOST, ARVADOS_API_HOST_INSECURE);
    }

	public static String getADMIN_EMAIL_ID() {
		return ADMIN_EMAIL_ID;
	}

	public static String getPORTAL_CONTACT_US() {
		return PORTAL_CONTACT_US;
	}    
}

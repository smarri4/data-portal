package edu.uic.cri.portal.listener;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

import edu.uic.cri.arvados.ArvadosAPI;


/**
 * Application Lifecycle Listener implementation class AppRequestListener
 *
 */
@WebListener
public class AppRequestListener implements ServletRequestListener {

	private static final String ARVADOS = "ARVADOS";
	private static final String DBCONNECTION = "dbconnection";
	
    /**
     * Default constructor. 
     */
    public AppRequestListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletRequestListener#requestDestroyed(ServletRequestEvent)
     */
    public void requestDestroyed(ServletRequestEvent arg0)  { 
    	//Close DB connection
    	Connection dbConn = (Connection) arg0.getServletRequest().getAttribute(DBCONNECTION);
    	try {
    		if ( dbConn != null)
			dbConn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    }

	/**
     * @see ServletRequestListener#requestInitialized(ServletRequestEvent)
     */
    public void requestInitialized(ServletRequestEvent arg0)  { 
 //   	ServletContext context = arg0.getServletContext();
			}
			
    public static Connection getDbConnection(HttpServletRequest request) throws SQLException {
    	Object conn = request.getAttribute(DBCONNECTION);
    	if ( conn == null ) {
    		conn = AppConfigListener.getConnection();
    		request.setAttribute(DBCONNECTION, conn);
		}
    	return (Connection) conn;
    }
    
    public static ArvadosAPI getArvadosApi(HttpServletRequest request) throws Exception {
    	Object arv = request.getAttribute(ARVADOS);
    	if ( arv == null ) {
    		arv = AppConfigListener.getArvadosApi();
    		request.setAttribute(ARVADOS, arv);
    }
    	return (ArvadosAPI) arv;
    }
}

package edu.uic.cri.portal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import edu.uic.cri.arvados.ArvadosAPI;
import edu.uic.cri.portal.listener.AppRequestListener;

/**
 * Servlet implementation class FileServlet handles request to serve the files from the arvados keep-web
 * @author SaiSravith
 *
 */
@WebServlet(value = {"/file/*","/file-upload/*","/view-stg-files","/view-file/*"}, initParams = {@WebInitParam(name="keep-web", value = ""), @WebInitParam(name="buffer-size", value="4096")})
public class FileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static String KEEP_WEB;
	private static int BUFFER_SIZE = 4096; // 4 kB
	private static String FILE_PATH;
	private long maxFileSize = 100000000 * 1024;
	private int maxMemSize = 1000000000 * 1024;
	private File file ;
	private static Logger logger = Logger.getLogger(FileServlet.class);
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String size = config.getInitParameter("buffer-size");
		if ( size != null ) {
			BUFFER_SIZE = Integer.parseInt(size);
			this.log(String.format("Set transfer buffer size to: %d B", BUFFER_SIZE));
		}
		KEEP_WEB = config.getServletContext().getInitParameter("keep-web");
		FILE_PATH = config.getServletContext().getInitParameter("FILE_PATH");
	}
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("FileServlet:: making a head request..");
		String path = request.getPathInfo();
//		if ( path == null ) {
//			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//			return;
//		}
				
		//path = path.substring(1);
		if(request.getServletPath().equals("/file-upload")){
			String[] parts = path.split("/",3);
			if(parts.length>=2){
				System.out.println("path: "+path);
				File projectDir = new File(FILE_PATH, parts[1]);
				if ( ! projectDir.exists() ) {
					response.setStatus(HttpServletResponse.SC_CONFLICT);
					return;
				}
				file = new File( projectDir, parts[2]) ;
				System.out.println(" file name" + file.getAbsolutePath());
				// If the file already exists, return a 409 (Conflict)				
				if ( file.exists() ) {
					response.setStatus(HttpServletResponse.SC_CONFLICT);
					return;
				}
				else{
					response.setStatus(HttpServletResponse.SC_OK);
					return;
				}
			}
		}
		
		logger.info("FileServlet:: done head request..");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if(request.getServletPath().equals("/file")){
			String path = request.getPathInfo();		
			if ( path == null ) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
			path = path.substring(1);
			// If the user is neither an admin or member of the project send 403 (FORBIDDEN)
			if ( ! request.isUserInRole("admin") ) {
				if ( ! ProjectManagementServlet.canReadFile(request, path) ) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;				
				}
			}
			String[] parts = path.split("/", 3);
			if ( parts.length < 2 ) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		try {
			ArvadosAPI arv = AppRequestListener.getArvadosApi(request);		
			CloseableHttpResponse keepResponse = arv.getCollectionResource(KEEP_WEB, parts[0], parts[1]);

			try {
				int status = keepResponse.getStatusLine().getStatusCode();

				response.setStatus(status);

				if ( status == 200 ) {

					HttpEntity entity = keepResponse.getEntity();

					OutputStream out = response.getOutputStream();
					InputStream fileIn = entity.getContent();			
					
					long length = entity.getContentLength();
					
					if (length <= Integer.MAX_VALUE) {
					  response.setContentLength((int)length);
					} else {
					  response.addHeader("Content-Length", Long.toString(length));
					}
					
					if ( entity.getContentType() != null ) {
						response.setContentType(entity.getContentType().getValue());
					}
					
					if ( entity.getContentEncoding() != null )
						response.setCharacterEncoding(entity.getContentEncoding().getValue());		

					byte[] buffer = new byte[BUFFER_SIZE];
					int count;
					while ( (count = fileIn.read(buffer)) > 0  ) {
						out.write(buffer, 0, count);
					}
					out.close();
				}

			} finally {
				keepResponse.close();
			}	
		} catch (Exception e) {
			throw new ServletException(e);
		}
		}
		else if(request.getServletPath().equals("/view-file")){
			String path = request.getPathInfo();		
			if ( path == null ) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			String[] parts = path.split("/", 3);
			byte[] buff = new byte[BUFFER_SIZE];
			File projectDir = new File(FILE_PATH, parts[1]);
			if ( ! projectDir.exists() ) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
			file = new File( projectDir, parts[2]) ;		
			if ( file.exists() ) {
				InputStream ip = new FileInputStream(file);
				OutputStream out = response.getOutputStream();
				try{
					while((ip.read(buff))>0){
						out.write(buff);
					}
				}
				catch(Exception e){
					logger.error(e.getMessage());
				}
				finally{
					ip.close();
					out.close();
				}
				response.setStatus(HttpServletResponse.SC_OK);
				return;
			}
			else{
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}
		else if(request.getServletPath().equals("/view-stg-files")){
			// If the user is neither an admin send 403 (FORBIDDEN)
			if ( ! request.isUserInRole("admin") ) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;				
			}
			File rootDir = new File(FILE_PATH);
			if ( ! rootDir.exists() ) {
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				return;
			}
			String[] projDirs = rootDir.list();
			JsonObject root = new JsonObject();
			for(String projName:projDirs){
				JsonArray fileArray = new JsonArray();
				File projectDir = new File(FILE_PATH, projName);
				System.out.println("filepath:"+FILE_PATH);

				System.out.println("proj dir:"+projectDir.getAbsolutePath());
				File[] files = projectDir.listFiles();
				if(files!=null){
					for(File f: files){
						fileArray.add(f.getName());
					}
					root.add(projName, fileArray);
				}
			}
			System.out.println(root);
			request.setAttribute(ProjectManagementServlet.PROJECT_FILES_ATTR, root);
			RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher("/admin/staged-files.jsp");
			reqDispatcher.forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getServletPath().equals("/file-upload")){
			System.out.println("FileServlet:: uploading files..");
			DiskFileItemFactory factory = new DiskFileItemFactory();
		      // maximum size that will be stored in memory
		      factory.setSizeThreshold(maxMemSize);
		      // Location to save data that is larger than maxMemSize.
		      factory.setRepository(new File("c:\\temp"));

		      // Create a new file upload handler
		      ServletFileUpload upload = new ServletFileUpload(factory);
		      // maximum file size to be uploaded.
		      upload.setSizeMax( maxFileSize );
		      
		      try{ 
		          // Parse the request to get file items.
		          List fileItems = upload.parseRequest(new ServletRequestContext(request));
		    	
		          // Process the uploaded file items
		          
		          for(Object o : fileItems){
		        	  FileItem fi = (FileItem)o;
		        	  if ( !fi.isFormField () )	
			             {
			                // Get the uploaded file parameters
			                String fieldName = fi.getFieldName();
			                String fileName = fi.getName();
			                String contentType = fi.getContentType();
			                boolean isInMemory = fi.isInMemory();
			                long sizeInBytes = fi.getSize();
			                // Write the file
			                if( fileName.lastIndexOf("\\") >= 0 ){
			                   file = new File( FILE_PATH + 
			                   fileName.substring( fileName.lastIndexOf("\\"))) ;
			                }else{
			                   file = new File( FILE_PATH + 
			                   fileName.substring(fileName.lastIndexOf("\\")+1)) ;
			                }
			                fi.write( file ) ;
			                System.out.println("Uploaded Filename: " + fileName + "<br>");
			             }
		          }

		       }catch(Exception ex) {
		           System.out.println(ex);
		       }
		}
		 
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * 
	 * The PUT method for the FileServlet is use to for file uploads.  All file upload paths should contain a project UUID and the name of the file.
	 * If the file already exists, the servlet will return a 409 (Conflict).
	 * If the file is created it will return a 201 (Created)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("FileServlet:: uploading files using PUT method!!");
		String path = request.getPathInfo();
		byte[] buff = new byte[BUFFER_SIZE];
		if(request.getServletPath().equals("/file-upload")){
			String[] parts = path.split("/");
			if(parts.length > 2){
				InputStream ip = request.getInputStream();
				// First check if the project directory exists.  If not create it.
				File projectDir = new File(FILE_PATH, parts[1]);
				if ( ! projectDir.exists() ) {
					projectDir.mkdirs();
				}
				file = new File( projectDir, parts[2]) ;
				// If the file already exists, return a 409 (Conflict)				
				if ( file.exists() ) {
					response.setStatus(HttpServletResponse.SC_CONFLICT);
					return;
				}
				System.out.println("writing file " + file.getAbsolutePath());
				FileOutputStream fo = new FileOutputStream(file);
				try{
					int bytes = 0;
					while((bytes=ip.read(buff))>0){
						fo.write(buff);
					}
				}
				catch(Exception e){
					logger.error(e.getMessage());
				}
				finally{
					fo.close();
				}
				response.setStatus(HttpServletResponse.SC_CREATED);
			}
			
		}
		logger.info("FileServlet:: uploaded files successfully using put..");
	}
}

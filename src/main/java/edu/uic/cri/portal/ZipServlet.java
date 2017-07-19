package edu.uic.cri.portal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;

import edu.uic.cri.arvados.ArvadosAPI;
import edu.uic.cri.portal.listener.AppRequestListener;

/**
 * Servlet implementation class ZipServlet
 */
@WebServlet("/zip/*")
public class ZipServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static int BUFFER_SIZE = 4096;
	private static String KEEP_WEB ;
       
    public void init(ServletConfig config) throws ServletException{
    	super.init(config);
    	String size = config.getInitParameter("buffer-size");
		if ( size != null ) {
			BUFFER_SIZE = Integer.parseInt(size);
			this.log(String.format("Set transfer buffer size to: %d B", BUFFER_SIZE));
		}
		KEEP_WEB = config.getServletContext().getInitParameter("keep-web");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();
		String projectID = path.split("/")[1];
		response.setContentType("Content-type: text/zip");
		response.setHeader("Content-Disposition",
				"attachment; filename="+projectID+".zip");
		ServletOutputStream out = response.getOutputStream();
		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(out));
		Map<String,Integer> fMap = new HashMap<String,Integer>();

		try{
			String[] files = request.getParameterValues("zipList");
			ArvadosAPI arv = AppRequestListener.getArvadosApi(request);
			for(String filePath : files){
				String[] parts= filePath.split("/");
				String fileName=filePath.substring(parts[0].length()+1);
				CloseableHttpResponse keepResponse = arv.getCollectionResource(KEEP_WEB, parts[0], filePath.substring(parts[0].length()));
				if(fMap.containsKey(fileName)){
					fMap.put(fileName, fMap.get(fileName)+1);
					String[] fSplit = fileName.split(".");
					String ext = fSplit[fSplit.length-1];
					fileName = fileName.substring(0, fileName.length()-ext.length()-2)+"(" + fMap.get(fileName)+")"+((!fSplit[1].equals(""))?"."+fSplit[1]:"");
				}
				else{
					fMap.put(fileName, 1);
				}
				zos.putNextEntry(new ZipEntry(fileName));
				InputStream inpStream = null;
				
				
				try {
					int status = keepResponse.getStatusLine().getStatusCode();

					response.setStatus(status);

					if ( status == 200 ) {
						this.log("Successfully retrieved file " + filePath);

						HttpEntity entity = keepResponse.getEntity();
						inpStream = entity.getContent();		
						BufferedInputStream bis = new BufferedInputStream(inpStream);
						
						byte[] buffer = new byte[BUFFER_SIZE];
						int count;
						while ( (count = bis.read(buffer)) > 0  ) {
							zos.write(buffer, 0, count);
						}
						bis.close();
						zos.closeEntry();
						this.log("Finished zipping file " + filePath);
					}
				}catch (FileNotFoundException fnfe) {
						// If the file does not exists, write an error entry instead of
						// file
						// contents
						zos.write(("Error could not find file " + filePath)
								.getBytes());
						zos.closeEntry();
						this.log("Could not find file "	+ filePath);
						continue;
					}
				finally {
					keepResponse.close();
				}
			}
			zos.close();
			out.flush();
		}
		catch(Exception e){
			throw new ServletException(e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// process the request as if it was a GET
		doGet(request, response);
	}

}

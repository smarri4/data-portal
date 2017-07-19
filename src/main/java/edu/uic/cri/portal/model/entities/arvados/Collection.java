/**
 * 
 */
package edu.uic.cri.portal.model.entities.arvados;

import java.text.ParseException;
import java.util.ArrayList;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import edu.uic.cri.portal.AdministrationServlet;
import edu.uic.cri.portal.model.entities.ProjectFiles;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import javax.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;



/**This class is user to represent a Collection arvados entity
 * @author George Chlipala
 *
 */
public class Collection extends ArvadosResource {
	
	static final String NAME_ATTR = "name";
	static final String DESCRIPTION_ATTR = "description";
	static final String DATA_HASH_ATTR = "portable_data_hash";
	static final String MANIFEST_ATTR = "manifest_text";
	static final String REP_DESIRED_ATTR = "replication_desired";
	static final String REP_CONFIRMED_ATTR = "replication_confirmed";
	static final String REP_CONFIRMED_AT_ATTR = "replication_confirmed_at";	
	
	String name;
	String description;
	String portableDataHash;
	String manifestText;
	int replicationDesired;
	int replicationConfirmed;
	Date replicationConfirmedAt;
	
	private List<CollectionFile> files = null;
	/**
	 * 
	 * @author George Chlipala
	 *
	 */
	public class CollectionFile {
		protected String path;
		protected List<String> locators = new ArrayList<String>(3);
		protected FileToken fileToken;

		/**
		 * 
		 * @return path
		 */
		public String getPath() {
			return this.path;
		}
		/**
		 * 
		 * @return fileTokens
		 */
		public FileToken getFileToken() {
			return this.fileToken;
		}
		/**
		 * 
		 * @return locators
		 */
		public List<String> getLocators() {
			return this.locators;
		}
		/**
		 * 
		 * @return file name
		 */
		public String getFilename() {
			return this.fileToken.name;
		}
		/**
		 * 
		 * @return size
		 */
		public long getSize() {
			return this.fileToken.size;
		}

		/**
		 * 
		 * @return Collection
		 */
		public Collection getCollection() { 
			return Collection.this;
		}
	}
	/**
	 * 
	 * @author George Chlipala
	 *
	 */
	public class FileToken {
		protected String name;
		protected long position;
		protected long size;
		/**
		 * 
		 * @param token
		 */
		FileToken(String token,String path) {
			String[] parts = token.split(":",3);
			this.position = Long.parseLong(parts[0]);
			this.size = Long.parseLong(parts[1]);
			// This is a kludge, but will need to change to spaces.  I am sure this a better way. - GC, 3 Nov 2016 
			if(path.length()<2)
				path = "";
			else
				path = path.substring(2) + "/";
			this.name =  path + parts[2].replace("\\040", " ");
		}
		/**
		 * 
		 * @return name
		 */
		public String getName() { 
			return this.name;
		}
		/**
		 * 
		 * @return size
		 */
		public long getSize() { 
			return this.size;
		}
		/**
		 * 
		 * @return position
		 */
		public long getPosition() {
			return this.position;
		}
		/**
		 * 
		 * @return Collection
		 */
		public Collection getCollection() { 
			return Collection.this;
		}
	}

	/**
	 * 
	 * @param parser
	 */
	public Collection(JsonParser parser) {
		this.handleParser(parser);
	}
	/**
	 * @param key
	 * @param event
	 * @param parser
	 * @throws ParseException
	 */
	protected boolean handleAttribute(String key, Event event, JsonParser parser) throws ParseException {
		boolean processed = super.handleAttribute(key, event, parser);
		if ( processed )
			return true;
		
		if ( key.equalsIgnoreCase(NAME_ATTR) ) {
			this.name = parser.getString();
			return true;
		} else if ( key.equalsIgnoreCase(DESCRIPTION_ATTR) ) {
			this.description = parser.getString();
			return true;
		} else if ( key.equalsIgnoreCase(DATA_HASH_ATTR) ) {
			this.portableDataHash = parser.getString();
			return true;
		} else if ( key.equalsIgnoreCase(MANIFEST_ATTR) ) {
			this.manifestText = parser.getString();
			return true;
		} else if ( key.equalsIgnoreCase(REP_CONFIRMED_ATTR) ) {
			this.replicationConfirmed = parser.getInt();
			return true;
		} else if ( key.equalsIgnoreCase(REP_DESIRED_ATTR) ) {
			this.replicationDesired = parser.getInt();
			return true;
		} else if ( key.equalsIgnoreCase(REP_CONFIRMED_AT_ATTR) ) {
			this.replicationConfirmedAt = DATE_FORMAT.parse(parser.getString());
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * 
	 * @return description
	 */
	public String getDescription() {
		return this.description;
	}
	/**
	 * 
	 * @return
	 */
	public String getPortableDataHash() {
		return this.portableDataHash;
	}
	/**
	 * 
	 * @return manifestText
	 */
	public String getManifestText() { 
		return this.manifestText;
	}
	/**
	 * 
	 * @return Collectionfile List
	 */
	public List<CollectionFile> getManifest() {
		//TreeNode root = new TreeNode();
		JsonObject root = new JsonObject();
		List<String> filenames = new ArrayList<String>();
		if ( this.files == null ) {
			
			String[] lines = this.getManifestText().split("\\n");
			this.files = new ArrayList<CollectionFile>(lines.length);
//			protected List<String> locators = new ArrayList<String>(3);
			for ( String line : lines ) {
				String[] parts = line.split(" ");
				boolean isLocator = true;		
				for ( int i = 1; i < (parts.length); i++ ) {
					Matcher amatch = null;
					if ( isLocator ) {
						amatch = KeepService.LOCATOR_FORMAT.matcher(parts[i]);
						isLocator = amatch.matches();
					}
					
					if ( !isLocator ) {
						CollectionFile collectionFile = new CollectionFile();
						collectionFile.path = parts[0];
						collectionFile.fileToken = new FileToken(parts[i],collectionFile.path);
						this.files.add(collectionFile);
						filenames.add(collectionFile.getFilename());
					}
				}
				
			}
		}
		Collections.sort(filenames);
	    for (String path : filenames) {
	        String[] parsedPath = path.split("/");
	        JsonObject curr = root;
	        for (int i = 0; i < parsedPath.length; i++) {
	        	// if it already has folder, add to that directory or file
	        	JsonArray subDirs = curr.getAsJsonArray("dirs");
	        	boolean contains = false;
	        	int size = (subDirs!=null)?subDirs.size():0;
		        	for (int j = 0; j < size; ++j) {
		        		JsonObject rec = (JsonObject) subDirs.get(j);
		        		String name = rec.get("name").getAsString();
		        		if(name.equals(parsedPath[i])){
		        			curr = rec;
		        			contains=true;
		        			break;
		        		}
		        	}
		        	JsonArray files = curr.getAsJsonArray("files");
		        	if((i==parsedPath.length-1) && files!=null&& curr!=null){
		        		String newFile = parsedPath[i];
		        		files.getAsJsonArray().add(newFile);
		        		curr=null;
		        	}
		        	else if((i==parsedPath.length-1)&&files==null&&curr!=null){
		        		String newFile = parsedPath[i];
		        		JsonArray array = new JsonArray();
		        		 array.add(newFile);
		        		curr.add("files",array);
		        		curr=null;
		        	}
		        	else if(!contains&&subDirs!=null){
		        		
	        			JsonObject newDir = new JsonObject();
		        		newDir.add("name", new JsonPrimitive(parsedPath[i]));
		        		subDirs.getAsJsonArray().add(newDir);
		        		curr = newDir;
	        		}
		        	else if(subDirs==null){
		        		JsonObject newDir = new JsonObject();
		        		newDir.add("name", new JsonPrimitive(parsedPath[i]));
		        		 JsonArray array = new JsonArray();
		        		 array.add(newDir);
		        		curr.add("dirs",array);
		        		curr=newDir;
		        	}
	        }
	    }
		
		return this.files;
	}
	
	public JsonObject fetchJsonOfCollectionFiles(HttpServletRequest request) {
		JsonObject root = new JsonObject();
		if ( this.files == null ) {
			String[] lines = this.getManifestText().split("\\n");
			this.files = new ArrayList<CollectionFile>(lines.length);
			for ( String line : lines ) {
				String[] parts = line.split(" ");
				boolean isLocator = true;		
				for ( int i = 1; i < (parts.length); i++ ) {
					Matcher amatch = null;
					//check if it matches the locator regex.If it doesn't, then it is a file.
					if ( isLocator ) {
						amatch = KeepService.LOCATOR_FORMAT.matcher(parts[i]);
						isLocator = amatch.matches();
					}
					
					if ( !isLocator ) {
						String absolutePath = parts[0];
						String token = parts[i];
						String[] split = token.split(":",3);
						
						//construct the filename with full path.
						if(absolutePath.length()<2)
							absolutePath = "";
						else
							absolutePath = absolutePath.substring(2) + "/";
						String fileName=  absolutePath + split[2].replace("\\040", " ");
						String fileId = this.getUuid()+"/"+fileName;
						root=buildJson(request,root,fileName,Long.parseLong(split[1]),fileId);
						System.out.println("fileId:"+ fileId);
						System.out.println("build json:"+ root);
				    
					}
				}
			}
		}
	    System.out.println("root:"+root);
		return root;
	}
	public JsonObject buildJson(HttpServletRequest request,JsonObject root,String path,Long size,String fileId){
		
		String[] parts = path.split("/");
		String name = parts[0];
		if(parts.length>=2){
			JsonArray subDirs = root.getAsJsonArray("dirs");
			if(subDirs==null){
        		JsonObject newDir = new JsonObject();
        		newDir.add("name", new JsonPrimitive(name));
        		 JsonArray array = new JsonArray();
        		 array.add(newDir);
        		 root.add("dirs",array);
        		 buildJson(request,(JsonObject)newDir,path.substring(parts[0].length()+1),size,fileId);
        		 return root;
        	}
			boolean contains=false;
			for(JsonElement elem : subDirs){
				if(((JsonObject)elem).get("name").getAsString().equals(name)){
					contains=true;
					buildJson(request,(JsonObject)elem,path.substring(parts[0].length()+1),size,fileId);
	    			return root;
				}
			}
			if(!contains){
				JsonObject newDir = new JsonObject();
        		newDir.add("name", new JsonPrimitive(name));
        		subDirs.getAsJsonArray().add(newDir);
        		buildJson(request,(JsonObject)newDir,path.substring(parts[0].length()+1),size,fileId);
        		return root;
			}
		}
		else{
			JsonArray files = root.getAsJsonArray("files");
			Map<String,ProjectFiles> projectFiles = AdministrationServlet.getCurrentFileMap(request);
			
			if(files!=null){
				JsonObject newFile = new JsonObject();
        		newFile.add("name", new JsonPrimitive(name));
        		newFile.add("size", new JsonPrimitive(size));
        		newFile.add("fileid", new JsonPrimitive(fileId));
        		if(projectFiles.containsKey(fileId))
        			newFile.add("released", new JsonPrimitive(true));
        		else
        			newFile.add("released", new JsonPrimitive(false));
        		files.getAsJsonArray().add(newFile);
			}
			else{
				JsonObject newFile = new JsonObject();
        		newFile.add("name", new JsonPrimitive(name));
        		newFile.add("size", new JsonPrimitive(size));
        		newFile.add("fileid", new JsonPrimitive(fileId));
        		if(projectFiles.containsKey(fileId))
        			newFile.add("released", new JsonPrimitive(true));
        		else
        			newFile.add("released", new JsonPrimitive(false));
        		JsonArray array = new JsonArray();
        		 array.add(newFile);
        		root.add("files",array);
			}
		}
		return root;
		
	}
	// adds the released attribute from the project files map at the server side
	public JsonObject fillCheckBoxes(JsonObject root, HttpServletRequest request){
		String fileId = this.getUuid()+"/";
		Map<String,ProjectFiles> projectFiles = AdministrationServlet.getCurrentFileMap(request);
		JsonArray subDirs = root.getAsJsonArray("dirs");
		JsonArray files = root.getAsJsonArray("files");
		int count=0;
		if(files!=null){
			for(JsonElement obj : files){
				if(projectFiles.containsKey(fileId+ ((JsonObject)obj).get("name").getAsString())){
					((JsonObject) obj).addProperty("released", true);
					count++;
				}
			}
			if(count==files.size())
				root.addProperty("released", true);
		}
		
		if(subDirs!=null){
			for(JsonElement elem : subDirs){
				fillCheckBoxes(((JsonObject)elem), request);
			}
		}
		return root;
	}

	/**
	 * 
	 * @return replicationDesired
	 */
	public int getReplicationDesired() {
		return this.replicationDesired;
	}
	/**
	 * 
	 * @return replicationConfirmed
	 */
	public int getReplicationConfirmed() {
		return this.replicationConfirmed;
	}
	/**
	 * 
	 * @return replicationConfirmedAt
	 */
	public Date getReplicationConfirmedAt() {
		return this.replicationConfirmedAt;
	}
}

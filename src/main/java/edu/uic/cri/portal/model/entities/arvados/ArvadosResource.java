/**
 * 
 */
package edu.uic.cri.portal.model.entities.arvados;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

/**A generic arvados resource base class that has fields a typical Arvados Resource should have
 * @author George Chlipala
 *@see Collection
 *@see Filter
 *@see Group
 *@see Job
 *@see KeepService
 *@see User
 */
public abstract class ArvadosResource {

	final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");

	static final String UUID_ATTR = "uuid";
	static final String HREF_ATTR = "href";
	static final String KIND_ATTR = "kind";
	static final String ETAG_ATTR = "etag";
	static final String SELF_LINK_ATTR = "self_link";
	static final String OWNER_ATTR = "owner_uuid";
	static final String CREATED_AT_ATTR = "created_at";
	static final String MODIFIED_BY_CLIENT_ATTR = "modified_by_client_uuid";
	static final String MODIFIED_BY_USER_ATTR = "modified_by_user_uuid";
	static final String MODIFIED_AT_ATTR = "modified_at";

	String uuid;
	String href;
	String kind;
	String etag;
	String selfLink;
	String ownerUUID;
	Date createdAt;
	String modifiedByClientUUID;
	String modifiedByUserUUID;
	Date modifiedAt;
	Date lastRetrieved = new Date();
	

	/**
	 * 
	 * @param key
	 * @param event
	 * @param parser
	 * @return
	 * @throws ParseException
	 */
	protected boolean handleAttribute(String key, Event event, JsonParser parser) throws ParseException {
		if ( key.equals(UUID_ATTR) ) {
			this.uuid = parser.getString();
			return true;
		} else if ( key.equals(HREF_ATTR) ) {
			this.href = parser.getString();
			return true;
		} else if ( key.equals(KIND_ATTR)) {
			this.kind = parser.getString();
			return true;
		} else if ( key.equals(ETAG_ATTR) ) {
			this.etag = parser.getString();
			return true;
		} else if ( key.equals(SELF_LINK_ATTR) ) {
			this.selfLink = parser.getString();
			return true;
		} else if ( key.equals(OWNER_ATTR) ) {
			this.ownerUUID = parser.getString();
			return true;
		} else if ( key.equals(CREATED_AT_ATTR) ) {
			this.createdAt = DATE_FORMAT.parse(parser.getString());
			return true;
		} else if ( key.equals(MODIFIED_BY_CLIENT_ATTR) ) {
			this.modifiedByClientUUID = parser.getString();
			return true;
		} else if ( key.equals(MODIFIED_BY_USER_ATTR) ) {
			this.modifiedByUserUUID = parser.getString();
			return true;
		} else if ( key.equals(MODIFIED_AT_ATTR) ) {
			this.modifiedAt = DATE_FORMAT.parse(parser.getString());
			return true;
		}
		return false;
	}

	/**
	 * Method to handle JSON arrays.  Should be overwritten by classes that have array attributes.
	 * 
	 * @param key
	 * @param parser
	 * @throws ParseException
	 */
	protected void handleArray(String key, JsonParser parser) throws ParseException {
		int depth = 0;
		while (parser.hasNext() ) {
			switch (parser.next()) {
			case START_ARRAY:
				depth++;
				break;
			case END_ARRAY:
				if ( depth == 0 ) return;
				depth--;
			default:
				break;
			}
		}
	}
	/**
	 * 
	 * @param key
	 * @param parser
	 * @throws ParseException
	 */
	protected void handleObject(String key, JsonParser parser) throws ParseException {
		int depth = 0;
		while ( parser.hasNext() ) {
			switch ( parser.next() ) {
			case END_OBJECT:
				if ( depth == 0 )
					return;
				depth--;
				break;
			case START_OBJECT:
				depth++;
			default:
				break;
			}
		}
	}

	/**
	 * 
	 * @param parser
	 */
	protected void handleParser(JsonParser parser) {
		String currentKey = null;
		while ( parser.hasNext() ) {
			Event event = parser.next();
			try {
				switch (event) {
				case START_OBJECT:
					this.handleObject(currentKey, parser); break;
				case END_OBJECT:
					return;
				case KEY_NAME:
					currentKey = parser.getString(); break;
				case VALUE_NULL:
				case VALUE_STRING:
				case VALUE_NUMBER:
				case VALUE_FALSE:
				case VALUE_TRUE:
					this.handleAttribute(currentKey, event, parser);
					break;
				case END_ARRAY:
					break;
				case START_ARRAY:
					this.handleArray(currentKey, parser);
					break;
				}
			} catch (Exception e) {
				// TODO do something with the exception.
			}
		}
	}

	/**
	 * 
	 * @return modifiedAt
	 */
	public Date getModified() { 
		return this.modifiedAt;
	}

	/**
	 * 
	 * @return uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * 
	 * @param uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * 
	 * @return href
	 */
	public String getHref() {
		return href;
	}

	/**
	 * 
	 * @param href
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * 
	 * @return etag
	 */
	public String getEtag() {
		return etag;
	}

	/**
	 * 
	 * @param etag
	 */
	public void setEtag(String etag) {
		this.etag = etag;
	}

	/**
	 * 
	 * @return createdAt
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * 
	 * @param createdAt
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	/**
	 * 
	 * @param createdAt
	 */
	public void setCreatedAt(String createdAt) {
		try {
			this.createdAt = DATE_FORMAT.parse(createdAt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return modifiedAt
	 */
	public Date getModifiedAt() {
		return modifiedAt;
	}

	/**
	 * 
	 * @param modifiedAt
	 */
	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
	
	/**
	 * 
	 * @param modifiedAt
	 */
	public void setModifiedAt(String modifiedAt) {
		try {
			this.modifiedAt = DATE_FORMAT.parse(modifiedAt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return kind
	 */
	public String getKind() {
		return this.kind;
	}

	/**
	 * 
	 * @param kind
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}

	/**
	 * 
	 * @return selfLink
	 */
	public String getSelfLink() { 
		return this.selfLink;
	}
	/**
	 * 
	 * @param selfLink
	 */
	public void setSelfLink(String selfLink) {
		this.selfLink = selfLink;
	}
	/**
	 * 
	 * @return ownerUUID
	 */
	public String getOwnerUUID() { 
		return this.ownerUUID;
	}

	/**
	 * 
	 * @param ownerUUID
	 */
	public void setOwnerUUID(String ownerUUID) {
		this.ownerUUID = ownerUUID;
	}

	/**
	 * 
	 * @return modifiedByClientUUID
	 */
	public String getModifiedByClientUUID() { 
		return this.modifiedByClientUUID;
	}

	/**
	 * 
	 * @param modifiedByClientUUID
	 */
	public void setModifiedByClientUUID(String modifiedByClientUUID) {
		this.modifiedByClientUUID = modifiedByClientUUID;
	}

	/**
	 * 
	 * @return modifiedByUserUUID
	 */
	public String getModifiedByUserUUID() { 
		return this.modifiedByUserUUID;
	}
	/**
	 * 
	 * @param modifiedByUserUUID
	 */
	public void setModifiedByUserUUID(String modifiedByUserUUID) {
		this.modifiedByUserUUID = modifiedByUserUUID;
	}

	/**
	 * 
	 * @return lastRetrieved
	 */
	public Date getRetrieved() {
		return lastRetrieved;
	}

} 

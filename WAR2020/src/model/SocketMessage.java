package model;

import java.io.Serializable;
import java.util.Date;

public class SocketMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String type;
	private Date date;
	private String message;
	
	public SocketMessage() {
		
	}
	
	public SocketMessage(String type, Date date, String message) {
		super();
		this.type = type;
		this.date = date;
		this.message = message;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}

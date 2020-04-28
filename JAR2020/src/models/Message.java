package models;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private User sender;
	private User reciever;
	private String messageContent;
	private String messageTitle;
	private Date dateSent;
	private Boolean toAll = false;
	
	public Message() {
		
	}
	
	public Message(User sender, User reciever, String messageContent, String messageTitle, Date dateSent) {
		this.sender = sender;
		this.reciever = reciever;
		this.messageContent = messageContent;
		this.messageTitle = messageTitle;
		this.dateSent = dateSent;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getReciever() {
		return reciever;
	}

	public void setReciever(User reciever) {
		this.reciever = reciever;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public Boolean getToAll() {
		return toAll;
	}

	public void setToAll(Boolean toAll) {
		this.toAll = toAll;
	}

	public String getMessageTitle() {
		return messageTitle;
	}

	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}

	public Date getDateSent() {
		return dateSent;
	}

	public void setDateSent(Date dateSent) {
		this.dateSent = dateSent;
	}
	
}

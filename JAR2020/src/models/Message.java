package models;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private User sender;
	private User reciever;
	private String messageContent;
	private Boolean toAll = false;
	
	public Message() {
		
	}
	
	public Message(User sender, User reciever, String messageContent) {
		this.sender = sender;
		this.reciever = reciever;
		this.messageContent = messageContent;
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
	
}

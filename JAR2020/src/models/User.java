package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private List<Message> sentMessages = new ArrayList<>();
	private List<Message> recievedMessages = new ArrayList<>();
	
	public User() {
		
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public User(String username, String password) {
		this.username = username;
		this.password = password;
		this.recievedMessages = new ArrayList();
		this.sentMessages = new ArrayList();
	}

	public List<Message> getSentMessages() {
		return sentMessages;
	}

	public void setSentMessages(List<Message> sentMessages) {
		this.sentMessages = sentMessages;
	}

	public List<Message> getRecievedMessages() {
		return recievedMessages;
	}

	public void setRecievedMessages(List<Message> recievedMessages) {
		this.recievedMessages = recievedMessages;
	}

	@Override
	public String toString() {
		return "User:" + username;
	}

}
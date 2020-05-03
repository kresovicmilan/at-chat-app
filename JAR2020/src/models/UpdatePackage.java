package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpdatePackage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<String> loggedInUsers;
	private Set<String> registeredUsers;
	
	public UpdatePackage() {
		this.loggedInUsers = new ArrayList<String>();
		this.registeredUsers = new HashSet<String>();
	}

	public List<String> getLoggedInUsers() {
		return loggedInUsers;
	}

	public void setLoggedInUsers(List<String> loggedInUsers) {
		this.loggedInUsers = loggedInUsers;
	}

	public Set<String> getRegisteredUsers() {
		return registeredUsers;
	}

	public void setRegisteredUsers(Set<String> registeredUsers) {
		this.registeredUsers = registeredUsers;
	}

}

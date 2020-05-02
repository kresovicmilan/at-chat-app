package beans;

import java.util.HashMap;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import models.User;

@Singleton
@LocalBean
@Startup
public class StorageBean {
	
	private Map<String, User> users;
	private Map<String, User> loggedInUsers;
	
	public StorageBean() {
		this.users = new HashMap<String, User>();
		this.loggedInUsers = new HashMap<String, User>();
	}

	public Map<String, User> getUsers() {
		return users;
	}

	public void setUsers(Map<String, User> users) {
		this.users = users;
	}

	public Map<String, User> getLoggedInUsers() {
		return loggedInUsers;
	}

	public void setLoggedInUsers(Map<String, User> loggedInUsers) {
		this.loggedInUsers = loggedInUsers;
	}
	
}
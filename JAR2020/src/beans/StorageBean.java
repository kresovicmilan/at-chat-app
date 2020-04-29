package beans;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import models.Host;
import models.User;

@Singleton
@LocalBean
@Startup
public class StorageBean {
	private Map<String, User> users;
	private Map<String, User> loggedInUsers;
	private Map<String, Host> hosts;
	
	public StorageBean() {
		this.users = new HashMap<String, User>();
		this.loggedInUsers = new HashMap<String, User>();
		this.hosts = new HashMap<String, Host>();
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

	public Map<String, Host> getHosts() {
		return hosts;
	}

	public void setHosts(Map<String, Host> hosts) {
		this.hosts = hosts;
	}
	
	
}

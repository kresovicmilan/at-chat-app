package beans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import implementation.RestHostBuilder;
import models.Host;
import models.User;

@Singleton
@LocalBean
@Startup
public class ManagerBean {
	
	private Map<String, User> users;
	private Map<String, User> loggedInUsers;
	private Map<String, Host> hosts;
	private String masterInfo;
	private String hostInfo;
	private Host masterHost;
	private Host currentSlaveHost;
	
	public ManagerBean() {
		this.users = new HashMap<String, User>();
		this.loggedInUsers = new HashMap<String, User>();
		this.hosts = new HashMap<String, Host>();
		
		findMasterIpAddress();
		setHosts();
	}
	
	@PostConstruct
	public void handshakeInit() {
		System.out.println("Krenuo handshake i master je " + masterHost.getIpAddress());
		
		if (!masterHost.equals(currentSlaveHost)) {
			System.out.println("Poslao masteru " + hostInfo);
			RestHostBuilder.registerNodeBuilder(this.currentSlaveHost, this.masterHost);
		}
		
	}
	
	public void setHosts() {
		String aliasMaster = this.masterInfo.split(":")[0];
		String ipMaster = this.masterInfo.split(":")[1];
		String portMaster = this.masterInfo.split(":")[2];
		
		String aliasSlave = this.hostInfo.split(":")[0];
		String ipSlave = this.hostInfo.split(":")[1];
		String portSlave = this.hostInfo.split(":")[2];
		
		Host masterHost = new Host(aliasMaster, ipMaster + ":" + portMaster, true);
		this.hosts.put(masterHost.getIpAddress(), masterHost);
		this.masterHost = masterHost;
		
		if (!ipMaster.equals(System.getProperty("jboss.bind.address"))) {
			Host slaveHost = new Host(aliasSlave, ipSlave + ":" + portSlave, false);
			this.hosts.put(slaveHost.getIpAddress(), slaveHost);
			this.currentSlaveHost = slaveHost;
		} else {
			this.currentSlaveHost = masterHost;
		}
	}
	
	public void findMasterIpAddress() {
		String masterIp = "";
		String hostIp = "";
		
		try {
		      File ipConfigFile = new File(User.class.getProtectionDomain().getCodeSource().getLocation().getPath() 
		    		  + File.separator + "META-INF" 
		    		  + File.separator + "ip_config.txt");
		      Scanner reader = new Scanner(ipConfigFile);
		      
		      if (reader.hasNextLine()) {
		    	  masterIp = reader.nextLine();
		      }
		      
		      if (reader.hasNextLine()) {
		    	  hostIp = reader.nextLine();
		      }
		      
		      reader.close();
		      
		    } catch (FileNotFoundException e) {
		      System.out.println("Config file is not found.");
		      e.printStackTrace();
		    }
		
		if (masterIp.equals("master:mLocalhost:8080") || hostIp.equals("host:hLocalhost:8080") || !hostIp.split(":")[1].equals(System.getProperty("jboss.bind.address"))) {
			throw new Error ("Set up ip_config.txt file in META-INF folder");
		}
		
		this.masterInfo = masterIp;
		this.hostInfo = hostIp;
		
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


	public String getMasterInfo() {
		return masterInfo;
	}


	public void setMasterInfo(String masterInfo) {
		this.masterInfo = masterInfo;
	}


	public String getHostInfo() {
		return hostInfo;
	}


	public void setHostInfo(String hostInfo) {
		this.hostInfo = hostInfo;
	}


	public Host getMasterHost() {
		return masterHost;
	}


	public void setMasterHost(Host masterHost) {
		this.masterHost = masterHost;
	}


	public Host getCurrentSlaveHost() {
		return currentSlaveHost;
	}


	public void setCurrentSlaveHost(Host currentSlaveHost) {
		this.currentSlaveHost = currentSlaveHost;
	}
	
}

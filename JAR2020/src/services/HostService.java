package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.websocket.Session;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import DTO.HandshakeDTO;
import beans.HostManagerBean;
import beans.StorageBean;
import implementation.RestHostBuilder;
import model.SocketMessage;
import models.Host;
import models.UpdatePackage;
import models.User;
import ws.WSEndPoint;

@Stateless
@Remote(HostServiceRemote.class)
@Path("/host")
@LocalBean
public class HostService implements HostServiceRemote {
	
	@EJB
	StorageBean storageBean;
	
	@EJB
	HostManagerBean hostManagerBean;
	
	@EJB
	WSEndPoint ws;
	
	@Override
    public void registerNode(Host newHost) {
		System.out.println("[INFO] [MASTER] First step - Master recieved registration from: " + newHost.getIpAddress());
		
        if (!hostManagerBean.getHosts().containsKey(newHost.getIpAddress())) {
        	hostManagerBean.getHosts().put(newHost.getIpAddress(), newHost);
        	System.out.println("[INFO] [MASTER] First step - FINISHED");
        	
        	System.out.println("[INFO] [MASTER] Second step - Send new host to other hosts");
        	for (Host h: hostManagerBean.getHosts().values()) {
        		if ((!h.getIpAddress().equals(newHost.getIpAddress())) && (!h.getIpAddress().equals(hostManagerBean.getMasterHost().getIpAddress()))) {
        			RestHostBuilder.sendNewHostToHostBuilder(h.getIpAddress(), newHost);
        			System.out.println("[INFO] [MASTER] Second step - Sent to: " + h.getIpAddress());
        		}
        	}
        	
        	System.out.println("[INFO] [MASTER] Second step - FINISHED");
        }
    }


    @Override
    public void sendNewHostToHost(Host newHost) {
        if (!hostManagerBean.getHosts().containsKey(newHost.getIpAddress())) {
        	hostManagerBean.getHosts().put(newHost.getIpAddress(), newHost);
        	System.out.println("[INFO] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] Received new host: " + newHost.getIpAddress());
        }
    }

    @Override
    public Collection<Host> sendHostsToNewHost(Host newHost) {
    	System.out.println("[INFO] [MASTER] Third step - Received request from host: " + newHost.getIpAddress());
    	List<Host> otherHosts = new ArrayList<Host>();
        for(Host h: hostManagerBean.getHosts().values()) {
        	if ((!h.getIpAddress().equals(newHost.getIpAddress())) && (!h.getIpAddress().equals(hostManagerBean.getMasterHost().getIpAddress()))) {
        		otherHosts.add(h);
        	}
        }
        
        System.out.println("[INFO] [MASTER] Third step - Sending list of other host with size: " + otherHosts.size());
        System.out.println("[INFO] [MASTER] Third step - FINISHED");
        return otherHosts;
    }

    @Override
    public UpdatePackage sendAllLoggedInUsersToNode(HandshakeDTO handshakeDTO) {
    	Host sender = handshakeDTO.getSender();
    	UpdatePackage updatePackage = handshakeDTO.getUpdatePackage();
    	int handshake = handshakeDTO.getHandshake();

		if (handshake == 1) {
			System.out.println("[INFO] [MASTER] Fourth step - Received request from host: " + sender.getIpAddress());
			UpdatePackage newUpdatePackage = new UpdatePackage();
			Map<String, List<String>> loggedInUsersByHosts = new HashMap<>();
			Map<String, Set<String>> registeredUsersByHosts = new HashMap<>();
			List<String> loggedInUsernamesOnMaster = new ArrayList<>();
			Set<String> registeredUsernamesOnMaster = new HashSet<>();
			
			//Logged in users directly from master
			for (User u: storageBean.getLoggedInUsers().values()) {
				loggedInUsernamesOnMaster.add(u.getUsername());
			}
			loggedInUsersByHosts.put(hostManagerBean.getCurrentSlaveHost().getIpAddress(), loggedInUsernamesOnMaster);
			System.out.println("[INFO] [MASTER] Fourth step - [DIRECTLY MASTER] Size of list of logged in users: " + loggedInUsernamesOnMaster.size());
			
			//Logged in users from other hosts
			for (Map.Entry<String, List<String>> entry : hostManagerBean.getForeignLoggedUsers().entrySet()) {
			    if (!entry.getKey().equals(sender.getIpAddress())) {
			    	loggedInUsersByHosts.put(entry.getKey(), entry.getValue());
			    }
			}
			String jsonLoggedIn = new Gson().toJson(loggedInUsersByHosts);
			System.out.println("[INFO] [MASTER] Fourth step - [ALL] Map of logged in users converted to JSON");
			newUpdatePackage.getLoggedInUsers().add(jsonLoggedIn);
			System.out.println("[INFO] [MASTER] Fourth step - [ALL] Map of logged in users added to package");
			
			//Registered users directly from master
			for (User u: storageBean.getUsers().values()) {
				registeredUsernamesOnMaster.add(u.getUsername());
			}
			registeredUsersByHosts.put(hostManagerBean.getCurrentSlaveHost().getIpAddress(), registeredUsernamesOnMaster);
			System.out.println("[INFO] [MASTER] Fourth step - [DIRECTLY MASTER] Size of set of registered users: " + registeredUsernamesOnMaster.size());
			
			//Registered users from other hosts
			for (Map.Entry<String, Set<String>> entry : hostManagerBean.getForeignRegisteredUsers().entrySet()) {
				if (!entry.getKey().equals(sender.getIpAddress())) {
					registeredUsersByHosts.put(entry.getKey(), entry.getValue());
				}
			}
			String jsonRegistered = new Gson().toJson(registeredUsersByHosts);
			System.out.println("[INFO] [MASTER] Fourth step - [ALL] Map of registered users converted to JSON");
			newUpdatePackage.getRegisteredUsers().add(jsonRegistered);
			System.out.println("[INFO] [MASTER] Fourth step - [ALL] Map of registered users added to package");
			
			System.out.println("[INFO] [MASTER] Fourth step - FINISHED");
			return newUpdatePackage;
			
		} else {
			System.out.println("[INFO] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] Got an user update from host: " + sender.getIpAddress());
			hostManagerBean.getForeignLoggedUsers().put(sender.getIpAddress(), updatePackage.getLoggedInUsers());
			hostManagerBean.getForeignRegisteredUsers().put(sender.getIpAddress(), updatePackage.getRegisteredUsers());
			updateUsersInSocket();
			
			return updatePackage;
		}
	}
    
    @Override
    public void deleteHost(@PathParam("alias") String alias) {
    	System.out.println("[DELETE] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] Deleting host: " + alias);
    	Host deletedHost = hostManagerBean.getHosts().remove(alias);
		if (deletedHost != null) {
			hostManagerBean.getForeignLoggedUsers().remove(alias);
			hostManagerBean.getForeignRegisteredUsers().remove(alias);
			System.out.println("[DELETE] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] Host {" + alias + "} is removed");
			
			updateUsersInSocket();
			
			if (hostManagerBean.getCurrentSlaveHost().getIpAddress().equals(hostManagerBean.getMasterHost().getIpAddress())) {
	    		for (Host h: hostManagerBean.getHosts().values()) {
	    			if (!h.getIpAddress().equals(hostManagerBean.getMasterHost().getIpAddress())) {
	    				System.out.println("[DELETE] [MASTER] Deleting {" + alias + "} from {" + h.getAlias() + "}");
	    				RestHostBuilder.deleteHostBuilder(h, deletedHost);
	    			}
	    		}
	    		System.out.println("[DELETE] [MASTER] All other host are purged from {" + alias + "}");
	    	}
		}
    }

    /*@DELETE
    @Path("/node/{alias}")
    public Response removeNode(@PathParam("alias") String alias) {
    	hostManagerBean.getHosts().remove(alias);
        return Response.status(200).entity("Host is registered").build();
    }

    @GET
    @Path("/node")
    public String heartbeat() {
        System.out.println("PERIODICNO PROVERAVAJ DA LI SU SVI CVOROVI AKTIVNI");
        return "OK";
    }*/
    
    public void updateUsersInSocket() {
    	System.out.println("[INFO] Updating sockets");
    	List<String> usernames = new ArrayList<>(ws.getUserSessions().keySet());
    	for (List<String> listOfForeignLoggedInUsers: hostManagerBean.getForeignLoggedUsers().values()) {
    		usernames.addAll(listOfForeignLoggedInUsers);
    	}
		SocketMessage message = new SocketMessage("logged", new Date(), new Gson().toJson(usernames));
		String jsonMessage = new Gson().toJson(message);
		try {
			for (Session s: ws.getSessions()) {
				s.getBasicRemote().sendText(jsonMessage);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		usernames = new ArrayList<>(ws.getRegisteredUsers());
		for (Set<String> setOfForeignRegisteredUsers: hostManagerBean.getForeignRegisteredUsers().values()) {
    		usernames.addAll(new ArrayList<String>(setOfForeignRegisteredUsers));
    	}
		message = new SocketMessage("registered", new Date(), new Gson().toJson(usernames));
		jsonMessage = new Gson().toJson(message);
		try {
			for (Session s: ws.getSessions()) {
				s.getBasicRemote().sendText(jsonMessage);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
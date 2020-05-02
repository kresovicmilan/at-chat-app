package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.HostManagerBean;
import beans.StorageBean;
import implementation.RestHostBuilder;
import models.Host;

@Stateless
@Remote(HostServiceRemote.class)
@Path("/host")
@LocalBean
public class HostService implements HostServiceRemote {
	
	@EJB
	StorageBean storageBean;
	
	@EJB
	HostManagerBean hostManagerBean;
	
	@Override
    public void registerNode(Host newHost) {
		System.out.println("[INFO] [MASTER] First step - Master recieved registration from: " + newHost.getIpAddress());
		
        if (!hostManagerBean.getHosts().containsKey(newHost.getIpAddress())) {
        	hostManagerBean.getHosts().put(newHost.getIpAddress(), newHost);
        	System.out.println("[INFO] [MASTER] First step - FINISHED" + newHost.getIpAddress());
        	
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

    /*@POST
    @Path("/users/loggedIn")
    public String sendAllLoggedInUsersToNewNode() {
        System.out.println("NOVOM CVORU SE SALJU SVI ULOGOVANI KORISNICI");
        return "OK";
    }

    @DELETE
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
}
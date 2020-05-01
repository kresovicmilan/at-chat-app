package services;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.ManagerBean;
import implementation.RestHostBuilder;
import models.Host;

@Stateless
@Path("/host")
@LocalBean
public class HostService {
	
	@EJB
	ManagerBean managerBean;
	
	@POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerNode(Host newHost) {
		System.out.println("Master primio" + newHost.getAlias() + ":" + newHost.getIpAddress());
        if (!managerBean.getHosts().containsKey(newHost.getIpAddress())) {
        	managerBean.getHosts().put(newHost.getIpAddress(), newHost);
        	
        	for (Host h: managerBean.getHosts().values()) {
        		if ((!h.getIpAddress().equals(newHost.getIpAddress())) && (!h.getIpAddress().equals(managerBean.getMasterHost().getIpAddress()))) {
        			System.out.println("Poslao new host " + newHost.getIpAddress() + " Na server " + h.getIpAddress());
        			RestHostBuilder.sendNewHostToHostBuilder(h.getIpAddress(), newHost);
        		}
        	}
        	
        	RestHostBuilder.sendHostsToNewHostBuilder(newHost.getIpAddress(), managerBean.getHosts().values());
        }
    }


    @POST
    @Path("/node")
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendNewHostToHost(Host newHost) {
        if (!managerBean.getHosts().containsKey(newHost.getIpAddress())) {
        	System.out.println("Primio novi host " + newHost.getIpAddress());
        	managerBean.getHosts().put(newHost.getIpAddress(), newHost);
        }
    }

    @POST
    @Path("/nodes")
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendHostsToNewHost(Collection<Host> otherHosts) {
    	System.out.println("Usao da doda nove hostove");
        for(Host h: otherHosts) {
        	if ((!h.getIpAddress().equals(managerBean.getCurrentSlaveHost().getIpAddress())) && (!h.getIpAddress().equals(managerBean.getMasterHost().getIpAddress()))) {
        		System.out.println("Dodao " + h.getIpAddress());
        		managerBean.getHosts().put(h.getIpAddress(), h);
        	}
        }
    }

    @POST
    @Path("/users/loggedIn")
    public String sendAllLoggedInUsersToNewNode() {
        System.out.println("NOVOM CVORU SE SALJU SVI ULOGOVANI KORISNICI");
        return "OK";
    }

    @DELETE
    @Path("/node/{alias}")
    public Response removeNode(@PathParam("alias") String alias) {
        managerBean.getHosts().remove(alias);
        return Response.status(200).entity("Host is registered").build();
    }

    @GET
    @Path("/node")
    public String heartbeat() {
        System.out.println("PERIODICNO PROVERAVAJ DA LI SU SVI CVOROVI AKTIVNI");
        return "OK";
    }
}

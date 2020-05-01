package services;

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
    public Response registerNode(Host h) {
		System.out.println("Master primio" + h.getAlias() + ":" + h.getIpAddress());
        if (!managerBean.getHosts().containsKey(h.getAlias())) {
        	managerBean.getHosts().put(h.getAlias(), h);
        	return Response.status(200).entity("Host is registered").build();
        }
        
        return Response.status(400).entity("Host already exists").build();
    }


    @POST
    @Path("/node")
    public Response notifyNodes(Host h) {
        if (!managerBean.getHosts().containsKey(h.getAlias())) {
        	managerBean.getHosts().put(h.getAlias(), h);
        }
        
        return Response.status(200).entity("Host is registered").build();
    }

    @POST
    @Path("/nodes")
    public String notifyNode() {
        System.out.println("MASTER CVOR SALJE NOVOM CVORU SVE NEMASTER CVOROVE KOJI POSTOJE");
        return "OK";
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

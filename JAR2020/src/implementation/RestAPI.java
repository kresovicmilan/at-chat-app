package implementation;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import models.Host;

public interface RestAPI {
	@POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
	public void registerNode(Host newHost);


	@POST
    @Path("/node")
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendNewHostToHost(Host newHost);
	
	@POST
    @Path("/nodes")
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendHostsToNewHost(Collection<Host> otherHosts);
    
    /*
    @POST
    @Path("/nodes")
    public String notifyNode();

    @POST
    @Path("/users/loggedin")
    public String sendAllLoggedInUsersToNewNode();

    @DELETE
    @Path("/node/{alias}")
    public Response removeNode(@PathParam("alias") String alias);

    @GET
    @Path("/node")
    public String heartbeat();*/
}

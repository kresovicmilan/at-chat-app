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
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import models.Host;
import models.UpdatePackage;
import models.User;

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
	@Produces(MediaType.APPLICATION_JSON)
    public Collection<Host> sendHostsToNewHost(Host newHost);

	@POST
    @Path("/users/loggedIn")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public UpdatePackage sendAllLoggedInUsersToNode(Host sender, UpdatePackage updatePackage);

    /*@DELETE
    @Path("/node/{alias}")
    public Response removeNode(@PathParam("alias") String alias);

    @GET
    @Path("/node")
    public String heartbeat();*/
}
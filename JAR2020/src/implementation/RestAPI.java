package implementation;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import DTO.HandshakeDTO;
import models.ForeignMessage;
import models.Host;
import models.UpdatePackage;

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
	public UpdatePackage sendAllLoggedInUsersToNode(HandshakeDTO handshakeDTO);
	
	@DELETE
    @Path("/node/{alias}")
    public void deleteHost(@PathParam("alias") String alias, String sendingIp);
	
	@POST
    @Path("/message")
    @Consumes(MediaType.APPLICATION_JSON)
	public int sendMessage(ForeignMessage foreignMessage);
	
	@GET
	@Path("/node")
    public int checkIfAlive();
}
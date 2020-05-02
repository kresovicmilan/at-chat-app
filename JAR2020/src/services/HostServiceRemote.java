package services;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import models.Host;

public interface HostServiceRemote {
	
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
}

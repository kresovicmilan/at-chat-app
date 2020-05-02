package implementation;

import java.util.Collection;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import DTO.HandshakeDTO;
import models.Host;
import models.UpdatePackage;

public class RestHostBuilder {
	
	public static void registerNodeBuilder(Host currentSlaveHost, Host masterHost) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://" + masterHost.getIpAddress() + "/WAR2020/rest/host");
		RestAPI rest = target.proxy(RestAPI.class);
		rest.registerNode(currentSlaveHost);
	}
	
	public static void sendNewHostToHostBuilder(String receivingHostIp, Host newHost) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://" + receivingHostIp + "/WAR2020/rest/host");
		RestAPI rest = target.proxy(RestAPI.class);
		rest.sendNewHostToHost(newHost);
	}
	
	public static Collection<Host> sendHostsToNewHostBuilder(Host currentSlaveHost, Host masterHost) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://" + masterHost.getIpAddress() + "/WAR2020/rest/host");
		RestAPI rest = target.proxy(RestAPI.class);
		return rest.sendHostsToNewHost(currentSlaveHost);
	}
	
	public static UpdatePackage sendAllLoggedInUsersToNodeBuilder(Host sender, Host receiver, UpdatePackage updatePackage, int handshake) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://" + receiver.getIpAddress() + "/WAR2020/rest/host");
		RestAPI rest = target.proxy(RestAPI.class);

		HandshakeDTO handshakeDTO = new HandshakeDTO();
    	handshakeDTO.setSender(sender);
    	handshakeDTO.setUpdatePackage(updatePackage);
    	handshakeDTO.setHandshake(handshake);
    	
		return rest.sendAllLoggedInUsersToNode(handshakeDTO);
	}
	
	public static void deleteHostBuilder(Host receiver, Host deletedHost) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://" + receiver.getIpAddress() + "/WAR2020/rest/host");
		RestAPI rest = target.proxy(RestAPI.class);
		rest.deleteHost(deletedHost.getAlias());
	}
}
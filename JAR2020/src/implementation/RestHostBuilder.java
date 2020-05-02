package implementation;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import models.Host;

public class RestHostBuilder {
	
	public static void registerNodeBuilder(Host currentSlaveHost, Host masterHost) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://" + masterHost.getIpAddress() + "/WAR2020/rest/host");
		RestAPI rest = target.proxy(RestAPI.class);
		rest.registerNode(currentSlaveHost);
	}
	
	public static void sendNewHostToHostBuilder(String receivingHostIp, Host newHost) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://" + receivingHostIp + "/WAR2020/rest/node");
		RestAPI rest = target.proxy(RestAPI.class);
		rest.sendNewHostToHost(newHost);
	}
	
	
	public static Collection<Host> sendHostsToNewHostBuilder(Host currentSlaveHost, Host masterHost) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://" + masterHost.getIpAddress() + "/WAR2020/rest/nodes");
		RestAPI rest = target.proxy(RestAPI.class);
		return rest.sendHostsToNewHost(currentSlaveHost);
	}
}
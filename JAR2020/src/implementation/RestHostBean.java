package implementation;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import beans.RestAPI;
import models.Host;

@Stateless
@LocalBean
public class RestHostBean {
	
	public void connectToMaster(Host currentSlaveHost, Host masterHost) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target(masterHost.getIpAddress() + "/WAR2020/rest");
		RestAPI rest = target.proxy(RestAPI.class);
		rest.registerNode(currentSlaveHost);
	}
}

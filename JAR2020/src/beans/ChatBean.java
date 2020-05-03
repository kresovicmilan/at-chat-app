package beans;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.websocket.Session;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import DTO.MessageDTO;
import implementation.RestHostBuilder;
import models.ForeignMessage;
import models.Host;
import models.Message;
import models.UpdatePackage;
import models.User;
import ws.WSEndPoint;

@Stateless
@Path("/chat")
@LocalBean
public class ChatBean implements ChatRemote {
	
	//private Map<String, User> users = new HashMap<String, User>();
	//private Map<String, User> loggedInUsers = new HashMap<String, User>();
	
	@EJB
	HostManagerBean hostManagerBean;
	
	@EJB
	StorageBean storageBean;
	
	@EJB
	WSEndPoint ws;
	
	/*
	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;
	
	@Resource(mappedName = "java:jboss/exported/jms/queue/mojQueue")
	private Queue queue;
	*/
	
	@POST
    @Path("/users/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response register(User u) {
		System.out.println("---- REGISTER ----");
		System.out.println("[INFO] Username: " + u.getUsername());
		System.out.println("[INFO] Password: " + u.getPassword());
		for (User userSpecific: storageBean.getUsers().values()) {
			if (userSpecific.getUsername().equals(u.getUsername())) {
				System.out.println("[REGISTER - FORBIDDEN] User already exist");
				return Response.status(400).entity("Username already exists").build();
			}
		}
		
		for (Map.Entry<String, Set<String>> entry : hostManagerBean.getForeignRegisteredUsers().entrySet()) {
			List<String> convertedToList = new ArrayList<>(entry.getValue());
			for (String s: convertedToList) {
				if (s.equals(u.getUsername())) {
					System.out.println("[REGISTER - FORBIDDEN] User already exist on another host");
					return Response.status(400).entity("Username already exists").build();
				}
			}
		}
		
		storageBean.getLoggedInUsers().put(u.getUsername(), u);
		storageBean.getUsers().put(u.getUsername(), u);
		System.out.println("[REGISTER - SUCCESSFUL] User registered and logged in");
		
		//Informing hosts about new registered and logged in users
		sendNewPackageToAllHosts();
		
		return Response.status(200).entity("User registered and logged in").build();
    }
	
	@POST
    @Path("/users/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(User u) {
		System.out.println("---- LOGIN ----");
		System.out.println("[INFO] Username: " + u.getUsername());
		System.out.println("[INFO] Password: " + u.getPassword());
		for (User userSpecific: storageBean.getUsers().values()) {
			if (userSpecific.getUsername().equals(u.getUsername()) && userSpecific.getPassword().equals(u.getPassword())) {
				storageBean.getLoggedInUsers().put(u.getUsername(), u);
				System.out.println("[LOGIN - SUCCESSFUL] User logged in");
				
				//Informing hosts about new registered and logged in users
				sendNewPackageToAllHosts();
				
				return Response.status(200).entity("User credentials are correct").build();
			}
		}
		
		System.out.println("[LOGIN - FORBIDDEN] User credentals are incorrect");
		return Response.status(400).entity("User credentials are incorrect").build();
    }
	
	@DELETE
    @Path("/users/loggedIn/{user}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response logout(@PathParam("user") String username) {
		System.out.println("---- LOGOUT ----");
		System.out.println("[INFO] Username: " + username);
		List<Session> activeSessions = new ArrayList<>(ws.getUserSessions().get(username));
		if (activeSessions.size() == 1) {
			for (User userSpecific: storageBean.getLoggedInUsers().values()) {
				if (userSpecific.getUsername().equals(username)) {
					storageBean.getLoggedInUsers().remove(userSpecific.getUsername());
					System.out.println("[LOGOUT - SUCCESSFUL] User logged out");
					
					//Informing hosts about new registered and logged in users
					sendNewPackageToAllHosts();
					
					return Response.status(200).entity("User is logged out").build();
				}
			}
		} else {
			System.out.println("[LOGOUT - SUCCESSFUL] User logged out");
			
			//Don't need to inform other hosts because there wasn't any changes in storage
			
			return Response.status(200).entity("User is logged out").build();
		}
		
		System.out.println("[LOGIN - ERROR] User either doesn't exist or is already logged out");
		return Response.status(400).entity("User is not logged in").build();
    }
	
	@GET
	@Path("/users/registered")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getRegistered() {
		System.out.println("---- ALL REGISTERED USERS ----");
		List<String> usernames = new ArrayList();
		
		for(User u: storageBean.getUsers().values()) {
			System.out.println(u.getUsername());
			usernames.add(u.getUsername());
		}
		
		System.out.println("- On other hosts -");
		for (Set<String> setOfForeignRegisteredUsers: hostManagerBean.getForeignRegisteredUsers().values()) {
    		usernames.addAll(new ArrayList<String>(setOfForeignRegisteredUsers));
    		System.out.println(setOfForeignRegisteredUsers);
    	}

		return usernames;
	}
	
	@GET
	@Path("/users/loggedIn")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getAllLoggedIn() {
		System.out.println("---- ALL LOGGED IN USERS ----");
		List<String> activeUsers = new ArrayList<>(ws.getUserSessions().keySet());
		List<String> usernames = new ArrayList();
		
		for(User u: storageBean.getLoggedInUsers().values()) {
			System.out.println(u.getUsername());
			usernames.add(u.getUsername());
		}
		
		System.out.println("- On other hosts -");
		for (List<String> listOfForeignLoggedInUsers: hostManagerBean.getForeignLoggedUsers().values()) {
    		usernames.addAll(listOfForeignLoggedInUsers);
    		System.out.println(listOfForeignLoggedInUsers);
    	}

		return usernames;
	}
	
	@POST
    @Path("/messages/user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendMessageToUser(MessageDTO messageDTO) {
		System.out.println("---- SEND MESSAGE TO USER ----");
		System.out.println("[INFO] Sender: " + messageDTO.getSenderUsername());
		System.out.println("[INFO] Receiver: " + messageDTO.getRecieverUsername());
		System.out.println("[INFO] Message title: " + messageDTO.getMessageTitle());
		System.out.println("[INFO] Message content: " + messageDTO.getMessageContent());
		
		if (isDirectUser(messageDTO.getRecieverUsername())) {
			Message message = new Message();
			
			message.setMessageContent(messageDTO.getMessageContent());
			
			User sender = storageBean.getUsers().get(messageDTO.getSenderUsername());
			if (sender == null) {
				System.out.println("[ERROR] Sender doesn't exist: " + messageDTO.getSenderUsername());
				return Response.status(400).entity("Sender doesn't exist").build();
			}
			
			User reciever = storageBean.getUsers().get(messageDTO.getRecieverUsername());
			if (reciever == null) {
				System.out.println("[ERROR] Reciever doesn't exist: " + messageDTO.getRecieverUsername());
				return Response.status(400).entity("Reciever doesn't exist").build();
			}
			
			message.setSender(sender);
			message.setReciever(reciever);
			Date dateSent = new Date();
			message.setDateSent(dateSent);
			message.setMessageTitle(messageDTO.getMessageTitle());
			message.setMessageContent(messageDTO.getMessageContent());
			
			sender.getSentMessages().add(message);
			reciever.getRecievedMessages().add(message);
			
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			messageDTO.setDateSent(dateFormat.format(dateSent));
			
			String jsonMessageDTO = new Gson().toJson(messageDTO);
			ws.echoTextMessage(jsonMessageDTO);
			
			System.out.println("[INFO] Message has been sent");
			return Response.status(200).entity("Message has been sent").build();
		} else {
			
			if (!isDirectUser(messageDTO.getSenderUsername())) {
				System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() +"] [ERROR] Sender doesn't exist: " + messageDTO.getSenderUsername());
				return Response.status(400).entity("Sender doesn't exist").build();
			}
			
			System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] Send message to user on another host");
			Host hostWithUser = findHostWithUsername(messageDTO.getRecieverUsername());
			if(hostWithUser == null) {
				System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] Host with that username has not been found");
				return Response.status(400).entity("Reciever doesn't exist").build();
			}
			
			System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] Sending message to user on host {" + hostWithUser.getIpAddress() + "}");
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			Date dateSent = new Date();
			messageDTO.setDateSent(dateFormat.format(dateSent));
			ForeignMessage foreignMessage = new ForeignMessage(messageDTO, hostManagerBean.getCurrentSlaveHost().getIpAddress(), hostWithUser.getIpAddress());
			
			int succ;
			try {
				succ = RestHostBuilder.sendMessageBuilder(foreignMessage);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] [ERROR] Could not send message to user on host {" + hostWithUser.getIpAddress() + "} - Failed first time");
				try {
					System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] Sending message to user on host {" + hostWithUser.getIpAddress() + "} - Second time");
					succ = RestHostBuilder.sendMessageBuilder(foreignMessage);
				} catch (Exception eSecond) {
					eSecond.printStackTrace();
					System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] [ERROR] Could not send message to user on host {" + hostWithUser.getIpAddress() + "} - Failed second time - Finish");
					return Response.status(400).entity("").build();
				}
			}
			
			if (succ == 1) {
				storageBean.getUsers().get(foreignMessage.getSenderUsername()).getSentForeignMessages().add(foreignMessage);
				System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] Message sent to user on host {" + hostWithUser.getIpAddress() + "}");
				return Response.status(200).entity("Message has been sent").build();
			} else {
				System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] [ERROR] Could not send message to user on host {" + hostWithUser.getIpAddress() + "} - Problem on receiving host");
				return Response.status(400).entity("").build();
			}
		}
		
    }
	
	@POST
    @Path("/messages/all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendMessageToAll(MessageDTO messageDTO)  {
		System.out.println("---- SEND MESSAGE TO ALL USERS ----");
		System.out.println("[INFO] Sender: " + messageDTO.getSenderUsername());
		System.out.println("[INFO] Message title: " + messageDTO.getMessageTitle());
		System.out.println("[INFO] Message content: " + messageDTO.getMessageContent());
		
		User sender = storageBean.getUsers().get(messageDTO.getSenderUsername());
		if (sender == null) {
			System.out.println("[ERROR] Sender doesn't exist: " + messageDTO.getSenderUsername());
			return Response.status(400).entity("Sender doesn't exist").build();
		}
		
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String jsonMessageDTO = "";
		
		for(User u: storageBean.getUsers().values()) {
			Message message = new Message();
			message.setMessageContent(messageDTO.getMessageContent());
			message.setSender(sender);
			message.setReciever(u);
			message.setToAll(true);
			message.setMessageTitle(messageDTO.getMessageTitle());
			Date dateSent = new Date();
			message.setDateSent(dateSent);
			u.getRecievedMessages().add(message);

			messageDTO.setDateSent(dateFormat.format(dateSent));
			messageDTO.setRecieverUsername(u.getUsername());
			jsonMessageDTO = new Gson().toJson(messageDTO);
			ws.echoTextMessage(jsonMessageDTO);
			
			System.out.println("[INFO] Sent to: " + u.getUsername());
		}
		
		System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] Sending message to users on another hosts");
		for(String hostIp: hostManagerBean.getHosts().keySet()) {
			if (!hostIp.equals(hostManagerBean.getCurrentSlaveHost().getIpAddress())) {
				for(String receiver: hostManagerBean.getForeignLoggedUsers().get(hostIp)) {
					System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] Sending message to user on host {" + hostIp + "}");
					messageDTO.setRecieverUsername(receiver);
					Date dateSent = new Date();
					messageDTO.setDateSent(dateFormat.format(dateSent));
					ForeignMessage foreignMessage = new ForeignMessage(messageDTO, hostManagerBean.getCurrentSlaveHost().getIpAddress(), hostIp);
					
					int succ = 0;
					try {
						succ = RestHostBuilder.sendMessageBuilder(foreignMessage);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] [ERROR] Could not send message to user on host {" + hostIp + "} - Failed first time");
						try {
							System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] Sending message to user on host {" + hostIp + "} - Second time");
							succ = RestHostBuilder.sendMessageBuilder(foreignMessage);
						} catch (Exception eSecond) {
							eSecond.printStackTrace();
							System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] [ERROR] Could not send message to user on host {" + hostIp + "} - Failed second time - Finish");
						}
					}
					
					if (succ == 1) {
						storageBean.getUsers().get(foreignMessage.getSenderUsername()).getSentForeignMessages().add(foreignMessage);
						System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] Message sent to user on host {" + hostIp + "}");
					} else {
						System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] [ERROR] Could not send message to user on host {" + hostIp + "} - Problem on receiving host");
					}
				}
			}
		}
		System.out.println("[SEND] [" + hostManagerBean.getCurrentSlaveHost().getIpAddress() + "] Message sent to users on other hosts");
		
		System.out.println("[INFO] Messages have been sent");
		return Response.status(200).entity("Message has been sent").build();
	}
	
	@GET
	@Path("/messages/{user}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<MessageDTO> getAllMessages(@PathParam("user") String username) {
		List<MessageDTO> messagesDTO = new ArrayList<>();
		System.out.println("---- ALL MESSAGES ----");
		System.out.println("[INFO] User: " + username);
		
		User claimant = storageBean.getUsers().get(username);
		if (claimant == null) {
			System.out.println("[ERROR] User doesn't exist: " + username);
			return messagesDTO;
		}
		
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		System.out.println("---- Inbox ----");
		for(User u: storageBean.getUsers().values()) {
			if (u.getUsername().equals(username)) {
				System.out.println("--- FROM THIS HOST ---");
				for (Message m: u.getRecievedMessages()) {
					MessageDTO messageDTO = new MessageDTO(m.getSender().getUsername(), username, m.getMessageContent(), m.getMessageTitle(), dateFormat.format(m.getDateSent()));
					messagesDTO.add(messageDTO);
					System.out.println("[INFO] Sender: " + messageDTO.getSenderUsername());
					System.out.println("[INFO] Date: " + dateFormat.format(m.getDateSent()));
					System.out.println("[INFO] Content: " + messageDTO.getMessageTitle());
					System.out.println("[INFO] Content: " + messageDTO.getMessageContent());
					System.out.println("-----------------------------------");
				}
				
				System.out.println("--- FROM FOREIGN HOSTS ---");
				for (ForeignMessage fm: u.getReceivedForeignMessages()) {
					MessageDTO messageDTO = new MessageDTO(fm);
					messagesDTO.add(messageDTO);
					System.out.println("[INFO] Sender: " + messageDTO.getSenderUsername());
					System.out.println("[INFO] Date: " + messageDTO.getDateSent());
					System.out.println("[INFO] Content: " + messageDTO.getMessageTitle());
					System.out.println("[INFO] Content: " + messageDTO.getMessageContent());
					System.out.println("-----------------------------------");
				}
				
				Collections.sort(messagesDTO);
				return messagesDTO;
			}
		}
		
		System.out.println("[INFO] All messages are displayed");
		return messagesDTO;
	}
	
	
	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "OK";
	}
	
	/*
	@POST
	@Path("/messages/{text}")
	@Produces(MediaType.TEXT_PLAIN)
	public String post(@PathParam("text") String text) {
		System.out.println("Recieved message: " + text);
		User user = users.get("nena");
		ws.echoTextMessage(text);
		
		try {
			QueueConnection connection = (QueueConnection) connectionFactory.createConnection("guest", "guest.guest.1");
			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueSender sender = session.createSender(queue);
			//create and publish a message
			TextMessage message = session.createTextMessage();
			message.setText(text);
			sender.send(message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return "OK";
	}*/
	
	public void sendNewPackageToAllHosts() {
		System.out.println("[INFO] [INFORMING HOSTS] Informing other host about users from this host");
		UpdatePackage updatePackage = new UpdatePackage();
		String currentHostIp = hostManagerBean.getCurrentSlaveHost().getIpAddress();
		
		for (User u: storageBean.getLoggedInUsers().values()) {
			updatePackage.getLoggedInUsers().add(u.getUsername());
		}
		
		for (User u: storageBean.getUsers().values()) {
			updatePackage.getRegisteredUsers().add(u.getUsername());
		}
		
		Host sender = hostManagerBean.getCurrentSlaveHost();
		
		for (Host h: hostManagerBean.getHosts().values()) {
			if(!h.getIpAddress().equals(currentHostIp)) {
				try {
					RestHostBuilder.sendAllLoggedInUsersToNodeBuilder(sender, h, updatePackage, 0);
				} catch(Exception e) {
					try {
						e.printStackTrace();
						System.out.println("[INFO] [INFORMING " + h.getIpAddress() + "] Informing host about users from this host - Second time");
						RestHostBuilder.sendAllLoggedInUsersToNodeBuilder(sender, h, updatePackage, 0);
					} catch (Exception eSecond) {
						eSecond.printStackTrace();
					}
				}
			}
		}
		
		System.out.println("[INFO] [INFORMING HOSTS] Other hosts are informed of users from this node");
	}
	
	public Boolean isDirectUser(String username) {
		return storageBean.getUsers().containsKey(username);
	}
	
	public Host findHostWithUsername(String username) {
		Host noHost = null;
		for (Map.Entry<String, List<String>> entry : hostManagerBean.getForeignLoggedUsers().entrySet())  {
			for (String u : entry.getValue()) {
				if(u.equals(username)) {
					return hostManagerBean.getHosts().get(entry.getKey());
				}
			}
		}
		
		return noHost;
	}
	
	static public class Msg {
		public String senderUsername;
		public String recieverUsername;
		public String messageContent;
	}
}

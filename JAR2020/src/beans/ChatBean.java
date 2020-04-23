package beans;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import DTO.MessageDTO;
import models.Message;
import models.User;
import ws.WSEndPoint;

@Stateless
@Path("/chat")
@LocalBean
public class ChatBean implements ChatRemote {
	
	private Map<String, User> users = new HashMap<String, User>();
	private Map<String, User> loggedInUsers = new HashMap<String, User>();
	
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
		for (User userSpecific: users.values()) {
			if (userSpecific.getUsername().equals(u.getUsername())) {
				System.out.println("[REGISTER - FORBIDDEN] User already exist");
				return Response.status(400).entity("Username already exists").build();
			}
		}
		
		this.loggedInUsers.put(u.getUsername(), u);
		this.users.put(u.getUsername(), u);
		System.out.println("[REGISTER - SUCCESSFUL] User registered and logged in");
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
		for (User userSpecific: users.values()) {
			if (userSpecific.getUsername().equals(u.getUsername()) && userSpecific.getPassword().equals(u.getPassword())) {
				this.loggedInUsers.put(u.getUsername(), u);
				System.out.println("[LOGIN - SUCCESSFUL] User logged in");
				return Response.status(200).entity("User credentials are correct").build();
			}
		}
		
		System.out.println("[LOGIN - FORBIDDEN] User credentals are incorrect");
		return Response.status(400).entity("User credentials are incorrect").build();
    }
	
	@DELETE
    @Path("/users/logout/{user}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response logout(@PathParam("user") String username) {
		System.out.println("---- LOGOUT ----");
		System.out.println("[INFO] Username: " + username);
		for (User userSpecific: loggedInUsers.values()) {
			if (userSpecific.getUsername().equals(username)) {
				this.loggedInUsers.remove(userSpecific.getUsername());
				System.out.println("[LOGOUT - SUCCESSFUL] User logged out");
				return Response.status(200).entity("User is logged out").build();
			}
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
		for(User u: users.values()) {
			System.out.println(u.getUsername());
			usernames.add(u.getUsername());
		}

		return usernames;
	}
	
	@GET
	@Path("/users/loggedIn")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getAllLoggedIn() {
		System.out.println("---- ALL LOGGED IN USERS ----");
		List<String> usernames = new ArrayList();
		for(User u: loggedInUsers.values()) {
			System.out.println(u.getUsername());
			usernames.add(u.getUsername());
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
		System.out.println("[INFO] Reciever: " + messageDTO.getRecieverUsername());
		System.out.println("[INFO] Message content: " + messageDTO.getMessageContent());
		Message message = new Message();
		
		message.setMessageContent(messageDTO.getMessageContent());
		
		User sender = this.users.get(messageDTO.getSenderUsername());
		if (sender == null) {
			System.out.println("[ERROR] Sender doesn't exist: " + messageDTO.getSenderUsername());
			return Response.status(400).entity("Sender doesn't exist").build();
		}
		
		User reciever = this.users.get(messageDTO.getRecieverUsername());
		if (reciever == null) {
			System.out.println("[ERROR] Reciever doesn't exist: " + messageDTO.getRecieverUsername());
			return Response.status(400).entity("Reciever doesn't exist").build();
		}
		
		message.setSender(sender);
		message.setReciever(reciever);
		message.setMessageContent(messageDTO.getMessageContent());
		
		sender.getSentMessages().add(message);
		reciever.getRecievedMessages().add(message);
		
		System.out.println("[INFO] Message has been sent");
		return Response.status(200).entity("Message has been sent").build();
    }
	
	@POST
    @Path("/messages/all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendMessageToAll(MessageDTO messageDTO)  {
		System.out.println("---- SEND MESSAGE TO ALL USERS ----");
		System.out.println("[INFO] Sender: " + messageDTO.getSenderUsername());
		System.out.println("[INFO] Message content: " + messageDTO.getMessageContent());
		
		User sender = this.users.get(messageDTO.getSenderUsername());
		if (sender == null) {
			System.out.println("[ERROR] Sender doesn't exist: " + messageDTO.getSenderUsername());
			return Response.status(400).entity("Sender doesn't exist").build();
		}
		
		for(User u: users.values()) {
			Message message = new Message();
			message.setMessageContent(messageDTO.getMessageContent());
			message.setSender(sender);
			message.setReciever(u);
			message.setToAll(true);
			u.getRecievedMessages().add(message);
			System.out.println("[INFO] Sent to: " + u.getUsername());
		}
		
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
		
		User claimant = this.users.get(username);
		if (claimant == null) {
			System.out.println("[ERROR] User doesn't exist: " + username);
			return messagesDTO;
		}
		
		System.out.println("---- Inbox ----");
		for(User u: users.values()) {
			if (u.getUsername().equals(username)) {
				for (Message m: u.getRecievedMessages()) {
					MessageDTO messageDTO = new MessageDTO(m.getSender().getUsername(), username, m.getMessageContent());
					messagesDTO.add(messageDTO);
					System.out.println("[INFO] Sender: " + messageDTO.getSenderUsername());
					System.out.println("[INFO] Content: " + messageDTO.getMessageContent());
					System.out.println("-----------------------------------");
				}
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
	
	static public class Msg {
		public String senderUsername;
		public String recieverUsername;
		public String messageContent;
	}
}

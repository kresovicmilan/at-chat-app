package ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

import DTO.MessageDTO;
import model.SocketMessage;



@Singleton
@ServerEndpoint("/ws/{username}")
@LocalBean
public class WSEndPoint {
    static List<Session> sessions = new ArrayList<Session>();
    static Map<String, List<Session>> userSessions = new HashMap<>();
    Set<String> registeredUsers = new HashSet<String>();

    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session) {
    	if(!sessions.contains(session)) {
    		sessions.add(session);
    		if (!userSessions.containsKey(username)) {
    			List<Session> userListSessions = new ArrayList<>();
    			userListSessions.add(session);
    			userSessions.put(username, userListSessions);
    			
    			List<String> usernames = new ArrayList<>(userSessions.keySet());
    			SocketMessage message = new SocketMessage("logged", new Date(), new Gson().toJson(usernames));
    			String jsonMessage = new Gson().toJson(message);
    			try {
	    			for (Session s: sessions) {
						s.getBasicRemote().sendText(jsonMessage);
	    			}
    			} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
    			}
    			
    			registeredUsers.add(username);
    			
    			usernames = new ArrayList<>(registeredUsers);
    			message = new SocketMessage("registered", new Date(), new Gson().toJson(usernames));
    			jsonMessage = new Gson().toJson(message);
    			try {
	    			for (Session s: sessions) {
						s.getBasicRemote().sendText(jsonMessage);
	    			}
    			} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
    			}
    		} else {
    			userSessions.get(username).add(session);
    		}
            //sessions.add(session);
        }
    }

    @OnMessage
    public void echoTextMessage(String jsonMessageDTO) {
        try {
        	MessageDTO messageDTO = new Gson().fromJson(jsonMessageDTO, MessageDTO.class);
        	List<Session> sessionsOfUser = userSessions.get(messageDTO.getRecieverUsername());
        	
        	//If active user is not active anymore, that is, if user doesn't have active sessions left
        	if (sessionsOfUser != null) {
	        	SocketMessage socketMessage = new SocketMessage();
	        	socketMessage.setType("message");
	        	socketMessage.setMessage(jsonMessageDTO);
	        	String jsonMessage = new Gson().toJson(socketMessage);
	        	
	        	for (Session s: sessionsOfUser) {
	        		s.getBasicRemote().sendText(jsonMessage);
	        	}
        	}
        	
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void close(@PathParam("username") String username, Session session) {
        sessions.remove(session);
        if (userSessions.get(username) != null) {
	        List<Session> userListSessions = userSessions.get(username);
	        for(Session s: userListSessions) {
	        	if (s.equals(session)) {
	        		userListSessions.remove(s);
	        		if (userListSessions.size() == 0) {
	        			userSessions.remove(username);
	        		}
	        		break;
	        	}
	        }
	        
	        List<String> usernames = new ArrayList<>(userSessions.keySet());
			SocketMessage message = new SocketMessage("logged", new Date(), new Gson().toJson(usernames));
			String jsonMessage = new Gson().toJson(message);
			try {
				for (Session s: sessions) {
					s.getBasicRemote().sendText(jsonMessage);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    @OnError
    public void error(@PathParam("username") String username, Session session, Throwable t) {
        sessions.remove(session);
        List<Session> userListSessions = userSessions.get(username);
        for(Session s: userListSessions) {
        	if (s.equals(session)) {
        		userListSessions.remove(s);
        		if (userListSessions.size() == 0) {
        			userSessions.remove(username);
        		}
        		break;
        	}
        }
        
        List<String> usernames = new ArrayList<>(userSessions.keySet());
		SocketMessage message = new SocketMessage("logged", new Date(), new Gson().toJson(usernames));
		String jsonMessage = new Gson().toJson(message);
		try {
			for (Session s: sessions) {
				s.getBasicRemote().sendText(jsonMessage);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public static Map<String, List<Session>> getUserSessions() {
		return userSessions;
	}

	public static void setUserSessions(Map<String, List<Session>> userSessions) {
		WSEndPoint.userSessions = userSessions;
	}

}

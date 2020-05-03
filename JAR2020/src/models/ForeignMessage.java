package models;

import java.io.Serializable;

import DTO.MessageDTO;

public class ForeignMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String senderUsername;
	
	private String recieverUsername;
	
	private String messageContent;
	
	private String messageTitle;
	
	private String dateSent;
	
	private String ipSendingHost;
	
	private String ipReceivingHost;
	
	public ForeignMessage() {
		
	}

	public ForeignMessage(String senderUsername, String recieverUsername, String messageContent, String messageTitle,
			String dateSent, String ipSendingHost, String ipReceivingHost) {
		super();
		this.senderUsername = senderUsername;
		this.recieverUsername = recieverUsername;
		this.messageContent = messageContent;
		this.messageTitle = messageTitle;
		this.dateSent = dateSent;
		this.ipSendingHost = ipSendingHost;
		this.ipReceivingHost = ipReceivingHost;
	}
	
	public ForeignMessage(MessageDTO messageDTO, String ipSendingHost, String ipReceivingHost) {
		this.senderUsername = messageDTO.getSenderUsername();
		this.recieverUsername = messageDTO.getRecieverUsername();
		this.messageContent = messageDTO.getMessageContent();
		this.messageTitle = messageDTO.getMessageTitle();
		this.dateSent = messageDTO.getDateSent();
		this.ipSendingHost = ipSendingHost;
		this.ipReceivingHost = ipReceivingHost;
	}

	public String getSenderUsername() {
		return senderUsername;
	}

	public void setSenderUsername(String senderUsername) {
		this.senderUsername = senderUsername;
	}

	public String getRecieverUsername() {
		return recieverUsername;
	}

	public void setRecieverUsername(String recieverUsername) {
		this.recieverUsername = recieverUsername;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public String getMessageTitle() {
		return messageTitle;
	}

	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}

	public String getDateSent() {
		return dateSent;
	}

	public void setDateSent(String dateSent) {
		this.dateSent = dateSent;
	}

	public String getIpSendingHost() {
		return ipSendingHost;
	}

	public void setIpSendingHost(String ipSendingHost) {
		this.ipSendingHost = ipSendingHost;
	}

	public String getIpReceivingHost() {
		return ipReceivingHost;
	}

	public void setIpReceivingHost(String ipReceivingHost) {
		this.ipReceivingHost = ipReceivingHost;
	}
	
	
}

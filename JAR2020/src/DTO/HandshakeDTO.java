package DTO;

import java.io.Serializable;

import models.Host;
import models.UpdatePackage;

public class HandshakeDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Host sender;
	private UpdatePackage updatePackage;
	private int handshake;
	
	public HandshakeDTO() {
		this.sender = new Host();
		this.updatePackage = new UpdatePackage();
		this.handshake = 0;
	}

	public Host getSender() {
		return sender;
	}

	public void setSender(Host sender) {
		this.sender = sender;
	}

	public UpdatePackage getUpdatePackage() {
		return updatePackage;
	}

	public void setUpdatePackage(UpdatePackage updatePackage) {
		this.updatePackage = updatePackage;
	}

	public int getHandshake() {
		return handshake;
	}

	public void setHandshake(int handshake) {
		this.handshake = handshake;
	}
	
	

}

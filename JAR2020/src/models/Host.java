package models;

public class Host {
	private String alias;
	private String ipAddress;
	private Boolean isMaster;
	
	public Host() {
		
	}
	
	public Host(String alias, String ipAddress, Boolean isMaster) {
		super();
		this.alias = alias;
		this.ipAddress = ipAddress;
		this.isMaster = isMaster;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Boolean getIsMaster() {
		return isMaster;
	}

	public void setIsMaster(Boolean isMaster) {
		this.isMaster = isMaster;
	}
	
	
}

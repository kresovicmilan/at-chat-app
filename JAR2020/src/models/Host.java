package models;

public class Host {
	private String alias;
	private String ipAddress;
	
	public Host() {
		
	}
	
	public Host(String alias, String ipAddress) {
		super();
		this.alias = alias;
		this.ipAddress = ipAddress;
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
	
	
}

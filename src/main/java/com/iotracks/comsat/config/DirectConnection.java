package com.iotracks.comsat.config;

public class DirectConnection {

	private String ipAddress;
	private int port;
	private String certificate;
	
	
	public DirectConnection(String ipAddress, int port, String certificate) {
		super();
		this.ipAddress = ipAddress;
		this.port = port;
		this.certificate = certificate;
	}
	
	
	public DirectConnection() {
		super();
	}

	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getCertificate() {
		return certificate;
	}
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}
	
}

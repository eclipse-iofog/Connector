/*
 * *******************************************************************************
 *  * Copyright (c) 2018 Edgeworx, Inc.
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Eclipse Public License v. 2.0 which is available at
 *  * http://www.eclipse.org/legal/epl-2.0
 *  *
 *  * SPDX-License-Identifier: EPL-2.0
 *  *******************************************************************************
 *
 */

package org.eclipse.iofog.comsat.config;

public class DirectConnection {

	private String ipAddress;
	private String remoteIpAddress;
	private int port;
	private String certificate;
	
	
	public DirectConnection(String remoteIpAddress, String ipAddress, int port, String certificate) {
		super();
		this.ipAddress = ipAddress;
		this.port = port;
		this.certificate = certificate;
		this.remoteIpAddress = remoteIpAddress;
	}
	
	
	public DirectConnection() {
		super();
	}

	public String getRemoteIpAddress() {
		return remoteIpAddress;
	}
	public void setRemoteIpAddress(String remoteIpAddress) {
		this.remoteIpAddress = remoteIpAddress;
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

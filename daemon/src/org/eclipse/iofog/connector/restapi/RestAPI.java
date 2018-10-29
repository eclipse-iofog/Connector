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

package org.eclipse.iofog.connector.restapi;

public class RestAPI {
	
	private static RestAPI instance = null;
	private RestAPIServer apiServer;
	private boolean secure;

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public static RestAPI getInstance(boolean devMode) {
		if (instance == null) {
			synchronized (RestAPI.class) {
				if (instance == null) {
					instance = new RestAPI();
					instance.setSecure(!devMode);
				}
			}
		}
		
		return instance;
	}
	
	public void start() {
        apiServer = RestAPIServer.getInstance(secure);
        new Thread(apiServer).start();
	}
	
	public void stop() {
		if (apiServer != null) 
			apiServer.stop();
	}

}

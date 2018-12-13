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

import java.util.concurrent.CompletableFuture;

import static org.eclipse.iofog.connector.utils.InstanceUtils.sendCommandlineParameters;

public class RestAPI {
	
	private static RestAPI instance = null;
	private RestAPIServer apiServer;
	public static final Object restApiStartLock = new Object();

	public static RestAPI getInstance() {
		if (instance == null) {
			synchronized (RestAPI.class) {
				if (instance == null) {
					instance = new RestAPI();
				}
			}
		}
		
		return instance;
	}
	
	public void start() throws Exception {
        apiServer = RestAPIServer.getInstance();
		CompletableFuture.runAsync(() -> apiServer.start());

		synchronized (restApiStartLock) {
			while(!apiServer.isOpen()) {
				restApiStartLock.wait();
			}
			try {
				sendCommandlineParameters("status");
			} catch (Exception e) {
				apiServer.stop();
				throw e;
			}
		}
	}
	
	public void stop() {
		if (apiServer != null) 
			apiServer.stop();
	}

}

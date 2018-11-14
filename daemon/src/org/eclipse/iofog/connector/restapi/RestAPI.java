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

import io.netty.handler.ssl.SslContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RestAPI {
	
	private static RestAPI instance = null;
	private RestAPIServer apiServer;
	private SslContext sslContext;

	private void setSslContext(SslContext sslContext) {
		this.sslContext = sslContext;
	}

	public static RestAPI getInstance(SslContext sslContext) {
		if (instance == null) {
			synchronized (RestAPI.class) {
				if (instance == null) {
					instance = new RestAPI();
					instance.setSslContext(sslContext);
				}
			}
		}
		
		return instance;
	}
	
	public void start() throws ExecutionException, InterruptedException {
        apiServer = RestAPIServer.getInstance(sslContext);
		CompletableFuture.runAsync(() -> apiServer.start());
	}
	
	public void stop() {
		if (apiServer != null) 
			apiServer.stop();
	}

}

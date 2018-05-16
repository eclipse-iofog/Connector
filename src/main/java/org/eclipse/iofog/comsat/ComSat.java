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

package org.eclipse.iofog.comsat;

import java.io.FileOutputStream;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import org.eclipse.iofog.comsat.commandline.CommandLineParser;
import org.eclipse.iofog.comsat.config.ConfigManager;
import org.eclipse.iofog.comsat.config.Configuration;
import org.eclipse.iofog.comsat.restapi.RestAPI;
import org.eclipse.iofog.comsat.utils.Constants;
import org.eclipse.iofog.comsat.utils.Settings;
import org.eclipse.iofog.comsat.utils.SocketsManager;

/**
 * Created by Saeid on 6/25/2016.
 */
public class ComSat {
	public static Object exitLock = new Object();
	
	private static SocketsManager socketsManager;

	public static void main(String[] args) throws Exception {
//		createHugeConfigFile(2000);
		
		CommandLineParser parser = new CommandLineParser();
		boolean stop = parser.localParser(args);
		if (stop)
			System.exit(1);
		
		Settings.loadSettings();
		
		ConfigManager.loadConfiguration();
        
        RestAPI server = RestAPI.getInstance();
        server.start();
        
        Thread.sleep(1000);

        socketsManager = new SocketsManager();
        openPorts();
        
        synchronized (exitLock) {
        	exitLock.wait();
		}
        
        Thread.sleep(200);

		server.stop();
        closePorts();        

        Constants.bossGroup.shutdownGracefully();
		Constants.workerGroup.shutdownGracefully();
		
		System.exit(0);
    }
	
    private static void openPorts() {
    	for (Entry<String, Configuration> e: ConfigManager.getMappings().entrySet()) {
    		Configuration cfg = e.getValue();
    		socketsManager.openPort(cfg);
    	}
    }
    
    private static void closePorts() {
    	socketsManager.closePorts();
    }
    
    @SuppressWarnings("unused")
	private static void createHugeConfigFile(int max) throws Exception {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		int port = 30000;
		for (int i = 0; i < max; i++) {
			JsonObject mapping = Json.createObjectBuilder()
					.add("id", String.valueOf(i))
					.add("port1", port)
					.add("port2", port + 1)
					.add("maxconnectionsport1", 60)
					.add("maxconnectionsport2", 60)
					.add("passcodeport1", "9zpVYBqYW3p7vNPq4fxHpbFx3BZNYYy7")
					.add("passcodeport2", "")
					.add("heartbeatabsencethresholdport1", 60000)
					.add("heartbeatabsencethresholdport2", 0)
					.build();
			arrayBuilder.add(mapping);
			
			port += 2;
		}
		JsonObject mappings = Json.createObjectBuilder()
				.add("mappings", arrayBuilder.build())
				.build();
        Json.createWriter(new FileOutputStream("/home/saeid/configs.json")).writeObject(mappings);
    }
    
}

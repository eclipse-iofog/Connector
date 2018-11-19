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

package org.eclipse.iofog.connector;

import io.netty.handler.ssl.SslContext;
import org.eclipse.iofog.connector.config.ConfigManager;
import org.eclipse.iofog.connector.config.Configuration;
import org.eclipse.iofog.connector.restapi.RestAPI;
import org.eclipse.iofog.connector.utils.*;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.net.ssl.SSLContext;
import java.io.FileOutputStream;
import java.util.Map.Entry;

import static org.eclipse.iofog.connector.commandline.CommandLineAction.showHelp;
import static org.eclipse.iofog.connector.utils.InstanceUtils.isAnotherInstanceRunning;
import static org.eclipse.iofog.connector.utils.InstanceUtils.sendCommandlineParameters;


/**
 * Created by Saeid on 6/25/2016.
 */
public class Connector {
	public static final Object exitLock = new Object();

	public static void main(String[] args) {
        try {
            Settings.loadSettings();

            if (args == null || args.length == 0) {
                System.out.println(showHelp());
            } else if (isAnotherInstanceRunning()) {
                switch (args[0]) {
                    case "stop":
                        System.out.println("Stopping connector...");
                        sendCommandlineParameters(args);
                        break;
                    case "start":
                        System.out.println("Connector is already running.");
                        break;
                }
            } else if ("start".equals(args[0])) {
				startConnector();
            }
        } catch (Exception ex) {
			LogUtil.warning(ex.getMessage());
        }
    }

    private static void startConnector() throws Exception {
		SslContext sslContext = Settings.isDevMode()
				? null
				: SslManager.getSslContext();

		ConfigManager.loadConfiguration();

		RestAPI server = RestAPI.getInstance(sslContext);
		server.start();
		SocketsManager socketsManager = new SocketsManager();
		openPorts(socketsManager, sslContext);

		synchronized (exitLock) {
			System.out.println("Connector started.");
			exitLock.wait();
		}

		Thread.sleep(200);

		server.stop();
		closePorts(socketsManager);

		Constants.bossGroup.shutdownGracefully();
		Constants.workerGroup.shutdownGracefully();

		System.exit(0);
	}

    private static void openPorts(SocketsManager socketsManager, SslContext sslContext) {
        for (Entry<String, Configuration> e : ConfigManager.getMappings().entrySet()) {
            Configuration cfg = e.getValue();
            socketsManager.openPort(cfg, sslContext);
        }
    }

    private static void closePorts(SocketsManager socketsManager) {
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

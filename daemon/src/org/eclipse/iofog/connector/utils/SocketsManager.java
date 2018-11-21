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

package org.eclipse.iofog.connector.utils;

import io.netty.handler.ssl.SslContext;
import org.eclipse.iofog.connector.config.Configuration;
import org.eclipse.iofog.connector.privatesocket.PrivateSocket;
import org.eclipse.iofog.connector.publicsocket.PublicSocket;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SocketsManager {
	private static Map<String, PublicSocket> publicSockets = new HashMap<>();
	private static Map<String, PrivateSocket> privateSockets = new HashMap<>();

	public synchronized void openPort(Configuration cfg, SslContext sslContext) {
    	PublicSocket publicSocket = null;
        PrivateSocket privateSocket = new PrivateSocket(cfg.getPort1(), cfg.getPassCode1(), cfg.getMaxConnections1(), sslContext);

        if (cfg.getPassCode2().equals("")) {
        	publicSocket = new PublicSocket(cfg.getPort2(), privateSocket, sslContext);
            new Thread(publicSocket).start();
            publicSockets.put(cfg.getId(), publicSocket);
        } else {
            PrivateSocket privateSocket2 = new PrivateSocket(cfg.getPort2(), cfg.getPassCode2(), cfg.getMaxConnections2(), sslContext);
            privateSocket.setPairSocket(privateSocket2);
            privateSocket2.setPairSocket(privateSocket);
            new Thread(privateSocket2).start();
        }

        new Thread(privateSocket).start();

        privateSockets.put(cfg.getId(), privateSocket);

        Settings.setPortInUse(cfg.getPort2());
        Settings.setPortInUse(cfg.getPort1());
	}
	
	public void closePorts() {
    	for (PrivateSocket privateSocket: privateSockets.values())
    		privateSocket.close();
    	
    	for (PublicSocket publicSocket: publicSockets.values()) 
    		publicSocket.close();
	}
	
	public synchronized void closePort(String id) {
		if (publicSockets.containsKey(id)) {
			publicSockets.get(id).close();
			publicSockets.remove(id);
		}
		
		if (privateSockets.containsKey(id)) {
			privateSockets.get(id).close();
			privateSockets.remove(id);
		}
	}
	
	public PrivateSocket getPrivateSocket(String id) {
		return privateSockets.get(id);
	}
	
	public PublicSocket getPublicSocket(String id) {
		return publicSockets.get(id);
	}
	
	public boolean isPortInUse(int portNumber) {
        boolean result;

        try {

            Socket s = new Socket("127.0.0.1", portNumber);
            s.close();
            result = true;

        }
        catch(Exception e) {
            result = false;
        }

        return(result);
	}
	
}

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

package org.eclipse.iofog.connector.privatesocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.eclipse.iofog.connector.utils.Constants;
import org.eclipse.iofog.connector.utils.LogUtil;
import org.eclipse.iofog.connector.utils.Settings;

import java.util.*;

public class PrivateSocket implements Runnable {

	public final String passCode;
	public final int port;
	public final int maxConnections;

	private Channel channel;
	private boolean stopListening = false;
	private PrivateSocket pairSocket;
	private static Map<Channel, Channel> channelsMapping;

	public List<Channel> connections = new ArrayList<>();
	
	public PrivateSocket(int port, String passCode, int maxConnections) {
		this.passCode = passCode;
		this.port = port;
		this.maxConnections = maxConnections;
		PrivateSocket.channelsMapping = new HashMap<>();
		this.pairSocket = null;
	}
	
	public void run() {
		while (!stopListening) {
			try {
				ServerBootstrap b = new ServerBootstrap();
				b.group(Constants.bossGroup, Constants.workerGroup)
					.channel(NioServerSocketChannel.class);
				PrivateSocketInitializer channelInitializer = new PrivateSocketInitializer(this);
				b.childHandler(channelInitializer);

				channel = b.bind(port).sync().channel();
				channel.closeFuture().sync();
			} catch (Exception e) {
				LogUtil.warning(e.getMessage());
			}

			if (!stopListening) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					LogUtil.warning(e.getMessage());
				}
			}
		}
	}

	public void close() {
		if (channel != null) {
			this.stopListening = true;
			try {
				channel.close().sync();
				Settings.setPortAvailable(port);

				while (connections.size() > 0) {
					Channel ch = connections.get(0);
					ch.close().sync();
				}

				if (pairSocket != null) {
					pairSocket.pairSocket = null;
					pairSocket.close();
				}
			} catch (Exception e) {
				LogUtil.warning(e.getMessage());
			}
		}
	}

	public Optional<Channel> mapChannel(Channel publicChannel) {
		synchronized (PrivateSocket.class) {
			return connections.stream()
					.filter(ch -> !channelsMapping.containsKey(ch))
					.peek(ch -> channelsMapping.put(ch, publicChannel))
					.findAny();
		}
	}

	public void releaseChannel(Channel privateChannel) {
		synchronized (PrivateSocket.class) {
			channelsMapping.remove(privateChannel);
		}
	}
	
	public Channel getPublicChannel(Channel privateChannel) {
		synchronized (PrivateSocket.class) {
			return channelsMapping.get(privateChannel);
		}
	}
	
	public void setPairSocket(PrivateSocket pairSocket) {
		this.pairSocket = pairSocket;
	}
	
	public PrivateSocket getPairSocket() {
		return this.pairSocket;
	}
	
	public boolean isPublic() {
		return this.pairSocket == null;
	}

	public Channel getPrivateChannel() {
		return connections.size() > 0 ? connections.get(0) : null;
	}
	
	public String getStatus() {
		return channel.isActive() ? "Active" : "Inactive";
	}
}

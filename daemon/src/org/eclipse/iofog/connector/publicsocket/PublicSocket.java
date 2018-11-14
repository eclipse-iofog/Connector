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

package org.eclipse.iofog.connector.publicsocket;

import org.eclipse.iofog.connector.privatesocket.PrivateSocket;
import org.eclipse.iofog.connector.utils.Constants;
import org.eclipse.iofog.connector.utils.LogUtil;
import org.eclipse.iofog.connector.utils.Settings;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;

public class PublicSocket implements Runnable {

	private final int port;
	private PrivateSocket privateSocket;

	private Channel channel;
	private boolean stopListening = false;
	private SslContext sslContext;
	
	public PublicSocket(int port, PrivateSocket privateSocket, SslContext sslContext) {
		this.port = port;
		this.privateSocket = privateSocket;
		this.sslContext = sslContext;
	}

	public void run() {
		while (!stopListening) {
			try {
				ServerBootstrap b = new ServerBootstrap();
				b.group(Constants.bossGroup, Constants.workerGroup).channel(NioServerSocketChannel.class);
				PublicSocketInitializer channelInitializer = new PublicSocketInitializer(privateSocket, sslContext);
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
			} catch (Exception e) {
				LogUtil.warning(e.getMessage());
			}
		}
	}
	
	public String getStatus() {
		return channel.isActive() ? "Active" : "Inactive";
	}
	
}

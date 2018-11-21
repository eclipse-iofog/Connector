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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import org.eclipse.iofog.connector.utils.Constants;
import org.eclipse.iofog.connector.utils.LogUtil;

/**
 * Created by Saeid on 6/26/2016.
 */
class RestAPIServer {

    private int port;
    private Channel ch;
    private SslContext sslCtx = null;
    private static RestAPIServer instance = null;
    
    private RestAPIServer(int port, SslContext sslContext) {
    	this.port = port;
    	this.sslCtx = sslContext;
    }

    private int getPort() {
        return port;
    }

    static RestAPIServer getInstance(SslContext sslContext) {
		if (instance == null) {
			synchronized (RestAPIServer.class) {
				if (instance == null) {
					int port = sslContext != null ? Constants.HTTPS_PORT : Constants.HTTP_PORT;
					instance = new RestAPIServer(port, sslContext);
                }
			}
		}
		
		return instance;
    }
    
	void start() {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(Constants.bossGroup, Constants.workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new RestAPIChannelInitializer(sslCtx));
            ch = b.bind(instance.getPort()).sync().channel();
            LogUtil.info(String.format("RestAPI server started on port: %d", instance.getPort()));
            ch.closeFuture().sync();
        } catch (Exception e) {
			LogUtil.warning(e.getMessage());
		}
    }
    
    void stop() {
    	if (ch != null) { 
    		try {
    			ch.disconnect().sync();
    		} catch (Exception e) {
				LogUtil.warning(e.getMessage());
    		}
    	}
    }
}

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

package org.eclipse.iofog.comsat.restapi;

import org.eclipse.iofog.comsat.utils.Constants;
import org.eclipse.iofog.comsat.utils.LogUtil;
import org.eclipse.iofog.comsat.utils.SslManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;

/**
 * Created by Saeid on 6/26/2016.
 */
public class RestAPIServer implements Runnable {

    private int port;
    private Channel ch;
    private SslContext sslCtx = null;
    private static RestAPIServer instance = null;
    
    private RestAPIServer(int port) {
    	this.port = port;
    }

    public int getPort() {
        return port;
    }

    public static RestAPIServer getInstance(boolean secure) {
		if (instance == null) {
			synchronized (RestAPIServer.class) {
				if (instance == null) {
                    if (secure)
                        instance = new RestAPIServer(Constants.HTTPS_PORT);
                    else
                        instance = new RestAPIServer(Constants.HTTP_PORT);
                }
			}
		}
		
		return instance;
    }
    
    public void run() {
        try {

            if(instance.getPort() == Constants.HTTPS_PORT)
                sslCtx = SslManager.getSslContext();

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
    
    public void stop() {
    	if (ch != null) { 
    		try {
    			ch.disconnect().sync();
    		} catch (Exception e) {
				LogUtil.warning(e.getMessage());
    		}
    	}
    }
}

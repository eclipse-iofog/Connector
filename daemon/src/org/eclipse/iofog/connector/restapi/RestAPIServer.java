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
import org.eclipse.iofog.connector.utils.Constants;
import org.eclipse.iofog.connector.utils.LogUtil;
import org.eclipse.iofog.connector.utils.Settings;

import static org.eclipse.iofog.connector.restapi.RestAPI.restApiStartLock;

/**
 * Created by Saeid on 6/26/2016.
 */
class RestAPIServer {

    private int port;
    private Channel ch;
    private static RestAPIServer instance = null;
    
    private RestAPIServer(int port) {
    	this.port = port;
    }

    private int getPort() {
        return port;
    }

    static RestAPIServer getInstance() {
		if (instance == null) {
			synchronized (RestAPIServer.class) {
				if (instance == null) {
					int port = Settings.isDevMode() ? Constants.HTTP_PORT : Constants.HTTPS_PORT;
					instance = new RestAPIServer(port);
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
                    .childHandler(new RestAPIChannelInitializer());
            ch = b.bind(instance.getPort()).sync().channel();
            LogUtil.info(String.format("RestAPI server started on port: %d", instance.getPort()));
            synchronized (restApiStartLock) {
                restApiStartLock.notifyAll();
            }
            ch.closeFuture().sync();
        } catch (Exception e) {
			LogUtil.error(e.getMessage());
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

    boolean isOpen() {
        return ch != null && ch.isOpen();
    }
}

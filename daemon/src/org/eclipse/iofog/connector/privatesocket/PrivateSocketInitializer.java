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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.ssl.SslHandler;
import org.eclipse.iofog.connector.utils.SslManager;

public class PrivateSocketInitializer extends ChannelInitializer<SocketChannel> {

	private final PrivateSocket socketServer;

    public PrivateSocketInitializer(PrivateSocket socketServer) {
        this.socketServer = socketServer;
    }
    
    @Override
	protected void initChannel(SocketChannel ch) throws Exception {
    	ChannelPipeline pipeline = ch.pipeline();

    	if (SslManager.getSslContext() != null) {
    		SslHandler sslHandler = SslManager.getSslContext().newHandler(ch.alloc());
    		pipeline.addLast(sslHandler);
    	}
    	
        pipeline.addLast(new ByteArrayDecoder());
        pipeline.addLast(new ByteArrayEncoder());
        pipeline.addLast(new PrivateSocketHandler(socketServer));
    }

}

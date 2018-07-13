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

package org.eclipse.iofog.comsat.privatesocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

public class PrivateSocketInitializer extends ChannelInitializer<SocketChannel> {

	private final SslContext sslCtx;
	private final PrivateSocket socketServer;

    public PrivateSocketInitializer(SslContext sslCtx, PrivateSocket socketServer) {
        this.sslCtx = sslCtx;
        this.socketServer = socketServer;
    }
    
    @Override
	protected void initChannel(SocketChannel ch) throws Exception {
    	ChannelPipeline pipeline = ch.pipeline();

    	if (sslCtx != null) {
    		SslHandler sslHandler = sslCtx.newHandler(ch.alloc());
    		pipeline.addLast(sslHandler);
    	}
    	
        pipeline.addLast(new ByteArrayDecoder());
        pipeline.addLast(new ByteArrayEncoder());
        pipeline.addLast(new PrivateSocketHandler(sslCtx, socketServer));
    }

}

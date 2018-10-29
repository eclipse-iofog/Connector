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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.ssl.SslContext;

public class PublicSocketInitializer extends ChannelInitializer<SocketChannel> {

	private final PrivateSocket privateSocket;
	private final SslContext sslCtx;

    public PublicSocketInitializer(PrivateSocket privateSocket, SslContext sslCtx) {
        this.privateSocket = privateSocket;
        this.sslCtx = sslCtx;
    }
    
    @Override
	protected void initChannel(SocketChannel ch) throws Exception {
    	ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null)
        	pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        pipeline.addLast(new ByteArrayDecoder());
        pipeline.addLast(new ByteArrayEncoder());
        pipeline.addLast(new PublicSocketHandler(privateSocket));
    }

}

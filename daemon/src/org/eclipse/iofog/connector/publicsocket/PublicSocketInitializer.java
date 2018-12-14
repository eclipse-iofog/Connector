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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import org.eclipse.iofog.connector.privatesocket.PrivateSocket;
import org.eclipse.iofog.connector.utils.SslManager;

public class PublicSocketInitializer extends ChannelInitializer<SocketChannel> {

	private final PrivateSocket privateSocket;

    public PublicSocketInitializer(PrivateSocket privateSocket) {
        this.privateSocket = privateSocket;
    }
    
    @Override
	protected void initChannel(SocketChannel ch) throws Exception {
    	ChannelPipeline pipeline = ch.pipeline();

        if (SslManager.getSslContext() != null)
        	pipeline.addLast(SslManager.getSslContext().newHandler(ch.alloc()));
        pipeline.addLast(new ByteArrayDecoder());
        pipeline.addLast(new ByteArrayEncoder());
        pipeline.addLast(new PublicSocketHandler(privateSocket));
    }

}

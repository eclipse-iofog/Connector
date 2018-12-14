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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.eclipse.iofog.connector.utils.SslManager;

/**
 * Created by Saeid on 6/26/2016.
 */
public class RestAPIChannelInitializer extends ChannelInitializer<SocketChannel> {
	private final EventExecutorGroup executor;

	public RestAPIChannelInitializer() {
		this.executor = new DefaultEventExecutorGroup(10);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if (SslManager.getSslContext() != null)
        	p.addLast(SslManager.getSslContext().newHandler(ch.alloc()));
        p.addLast(new HttpServerCodec());
		p.addLast(new HttpObjectAggregator(65535));
		p.addLast(new RestAPIChannelHandler(executor));
    }
}

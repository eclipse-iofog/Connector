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
import org.eclipse.iofog.connector.utils.LogUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PublicSocketHandler extends SimpleChannelInboundHandler<byte[]> {

	private final PrivateSocket privateSocket;
	
	private Channel privateChannel;
	
	public PublicSocketHandler(PrivateSocket privateSocket) {
		this.privateSocket = privateSocket;
	}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (privateChannel != null) {
			privateSocket.releaseChannel(privateChannel);
			privateChannel = null;
		}
    }

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
			privateChannel = privateSocket.mapChannel(ctx.channel()).orElse(null);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, byte[] bytes) throws Exception {
		if (privateChannel != null) {
			privateChannel.writeAndFlush(bytes).sync();
		}
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LogUtil.warning(cause.getMessage());
       ctx.channel().close().sync();
    }
}

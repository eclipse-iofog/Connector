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

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.eclipse.iofog.connector.utils.Constants;
import org.eclipse.iofog.connector.utils.LogUtil;

public class PrivateSocketHandler extends SimpleChannelInboundHandler<byte[]> {

	private final PrivateSocket socketServer;

	private boolean init = true;

	public PrivateSocketHandler(PrivateSocket socketServer) {
		this.socketServer = socketServer;
	}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		socketServer.connections.remove(ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	if (socketServer.connections.size() >= socketServer.maxConnections) {
    		ctx.channel().close().sync();
    	} else {
			socketServer.connections.add(ctx.channel());
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {

		if (init) {
			init = false;
			String passCode = new String(msg);
			if (passCode.equals(socketServer.passCode)) {
				ctx.channel().writeAndFlush(Constants.AUTHORIZED).sync();
			} else {
				ctx.channel().close().sync();
			}
			return;
		} else if (msg.length == 4 && (new String(msg)).equals("BEAT")) {
			ctx.channel().writeAndFlush(Constants.BEAT).sync();
			return;
		}
		
		if (!socketServer.isPublic()) {
			Channel pairChannel = socketServer.getPairSocket().getPrivateChannel();
			if (pairChannel != null) {
				pairChannel.writeAndFlush(msg).sync();
			}
		} else {
			Channel publicChannel = socketServer.getPublicChannel(ctx.channel());
			if (publicChannel != null) {
				publicChannel.writeAndFlush(msg).sync();
			}
		}
	}
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LogUtil.warning(cause.getMessage());
        ctx.channel().close().sync();
    }

}

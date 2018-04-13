package com.iotracks.comsat.public_socket;

import com.iotracks.comsat.private_socket.PrivateSocket;
import com.iotracks.comsat.utils.LogUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Optional;

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

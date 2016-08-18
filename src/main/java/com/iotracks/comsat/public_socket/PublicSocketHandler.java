package com.iotracks.comsat.public_socket;

import com.iotracks.comsat.private_socket.PrivateSocket;
import com.iotracks.comsat.utils.LogUtil;

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

    public void channelActive(ChannelHandlerContext ctx) {
		privateChannel = privateSocket.mapChannel(ctx.channel());
	}

	protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
		if (privateChannel != null) {
			privateChannel.writeAndFlush(msg).sync();
		}
	}
	
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LogUtil.warning(cause.getMessage());
       ctx.channel().close().sync();
    }
}

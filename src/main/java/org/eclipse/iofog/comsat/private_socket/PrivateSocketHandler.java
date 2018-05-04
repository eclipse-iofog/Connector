package main.java.org.eclipse.iofog.comsat.private_socket;

import main.java.org.eclipse.iofog.comsat.utils.Constants;
import main.java.org.eclipse.iofog.comsat.utils.LogUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslContext;

public class PrivateSocketHandler extends SimpleChannelInboundHandler<byte[]> {

	private final PrivateSocket socketServer;

	private boolean init = true;

	public PrivateSocketHandler(SslContext sslCtx, PrivateSocket socketServer) {
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
				pairChannel.writeAndFlush(msg);
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

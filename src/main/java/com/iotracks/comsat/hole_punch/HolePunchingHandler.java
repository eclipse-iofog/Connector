package com.iotracks.comsat.hole_punch;

import java.io.StringReader;
import java.net.InetSocketAddress;

import javax.json.Json;
import javax.json.JsonObject;

import com.iotracks.comsat.config.ConfigManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HolePunchingHandler extends SimpleChannelInboundHandler<byte[]> {

	protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
		JsonObject message = Json.createReader(new StringReader(new String(msg))).readObject();
		
		String id = message.getString("id");
		String passKey = message.getString("passkey");
		String certificate = message.getString("certificate");
		String localIp = message.getString("localip");
		
		if (!ConfigManager.isDirectValid(id, passKey)) {
			JsonObject response = Json.createObjectBuilder()
					.add("status", "not authorized")
					.build();
			ctx.channel().writeAndFlush(response.toString().getBytes()).sync();
			return;
		}
		
		HolePunchingPeer remotePeer = HolePunchingManager.remotePeerRepository.get(id);
		
		if (remotePeer == null) {
			HolePunchingManager.addRemotePeer(message.getString("id"), new HolePunchingPeer(localIp, ctx, certificate));
			JsonObject response = Json.createObjectBuilder()
					.add("status", "ok")
					.build();
			ctx.channel().writeAndFlush(response.toString().getBytes()).sync();
		}
		else {
			InetSocketAddress local = (InetSocketAddress) ctx.channel().remoteAddress();
			InetSocketAddress remote = (InetSocketAddress) remotePeer.getCtx().channel().remoteAddress();
			JsonObject responsePeerB = Json.createObjectBuilder()
					.add("status", "connect")
					.add("listen", local.getPort())
					.add("connect", remote.getPort())
					.add("privateip", remotePeer.getLocalIp())
					.add("publicip", remote.getAddress().getHostAddress())
					.add("certificate", certificate)
					.add("delay", true)
					.build();
			JsonObject responsePeerA = Json.createObjectBuilder()
					.add("status", "connect")
					.add("listen", remote.getPort())
					.add("connect", local.getPort())
					.add("privateip", message.getString("localip"))
					.add("publicip", local.getAddress().getHostAddress())
					.add("certificate", certificate)
					.add("delay", false)
					.build();
			ctx.channel().writeAndFlush(responsePeerB.toString().getBytes()).sync();
			remotePeer.getCtx().channel().writeAndFlush(responsePeerA.toString().getBytes()).sync();
			HolePunchingManager.removeRemotePeer(remotePeer.getCtx());
		}
	}

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		HolePunchingManager.removeRemotePeer(ctx);
    }

}

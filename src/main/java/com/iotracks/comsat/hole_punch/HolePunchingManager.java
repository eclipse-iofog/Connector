package com.iotracks.comsat.hole_punch;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.iotracks.comsat.utils.Constants;
import com.iotracks.comsat.utils.LogUtil;
import com.iotracks.comsat.utils.Settings;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class HolePunchingManager implements Runnable {
	private Channel channel;
	public static Map<String, HolePunchingPeer> remotePeerRepository = new HashMap<>();
	
	public void run() {
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(Constants.bossGroup, Constants.workerGroup).channel(NioServerSocketChannel.class);
			ChannelInitializer channelInitializer = new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel ch) throws Exception {
			    	ChannelPipeline pipeline = ch.pipeline();

			        pipeline.addLast(new ByteArrayDecoder());
			        pipeline.addLast(new ByteArrayEncoder());
			        pipeline.addLast(new HolePunchingHandler());
				}
			};
			b.childHandler(channelInitializer);

			channel = b.bind(Settings.getBrokerPort()).sync().channel();
			channel.closeFuture().sync();
		} catch (Exception e) {
			LogUtil.warning(e.getMessage());
		}
	}
	
	public static void addRemotePeer(String id, HolePunchingPeer peer) {
		synchronized (remotePeerRepository) {
			remotePeerRepository.put(id, peer);
		}
	}

	public static void removeRemotePeer(ChannelHandlerContext peer) {
		synchronized (remotePeerRepository) {
			for (Entry<String, HolePunchingPeer> entry : remotePeerRepository.entrySet()) {
				if (entry.getValue().getCtx().equals(peer)) {
					remotePeerRepository.remove(entry.getKey());
					return;
				}
			}
		}
	}

}

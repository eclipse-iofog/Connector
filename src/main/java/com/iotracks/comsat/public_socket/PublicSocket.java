package com.iotracks.comsat.public_socket;

import com.iotracks.comsat.private_socket.PrivateSocket;
import com.iotracks.comsat.utils.Constants;
import com.iotracks.comsat.utils.LogUtil;
import com.iotracks.comsat.utils.Settings;
import com.iotracks.comsat.utils.SslManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;

public class PublicSocket implements Runnable {

	private final int port;
	private PrivateSocket privateSocket;

	private Channel channel;
	private boolean stopListening = false;
	
	public PublicSocket(int port, PrivateSocket privateSocket) {
		this.port = port;
		this.privateSocket = privateSocket;
	}

	public void run() {
		SslContext sslCtx = null;
		try {
			sslCtx = SslManager.getSslContext();
		} catch (Exception e) {
			System.out.println("Error reading certificates");
			return;
		}
		while (!stopListening) {
//			EventLoopGroup bossGroup = new NioEventLoopGroup(1);
//			EventLoopGroup workerGroup = new NioEventLoopGroup(1);
			try {
				ServerBootstrap b = new ServerBootstrap();
				b.group(Constants.bossGroup, Constants.workerGroup).channel(NioServerSocketChannel.class);
				PublicSocketInitializer channelInitializer = new PublicSocketInitializer(privateSocket, sslCtx);
				b.childHandler(channelInitializer);

				channel = b.bind(port).sync().channel();
				channel.closeFuture().sync();
			} catch (Exception e) {
				LogUtil.warning(e.getMessage());
			}
//			finally {
//				bossGroup.shutdownGracefully();
//				workerGroup.shutdownGracefully();
//			}

			if (!stopListening) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					LogUtil.warning(e.getMessage());
				}
			}
		}
	}

	public void close() {
		if (channel != null) {
			this.stopListening = true;
			try {
				channel.close().sync();
				Settings.setPortAvailable(port);
			} catch (Exception e) {
				LogUtil.warning(e.getMessage());
			}
		}
	}
	
	public String getStatus() {
		if (channel.isActive())
			return "Active";
		else
			return "Inactive";
	}
	
}

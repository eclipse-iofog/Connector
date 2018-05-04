package main.java.org.eclipse.iofog.comsat.public_socket;

import main.java.org.eclipse.iofog.comsat.private_socket.PrivateSocket;
import main.java.org.eclipse.iofog.comsat.utils.Constants;
import main.java.org.eclipse.iofog.comsat.utils.LogUtil;
import main.java.org.eclipse.iofog.comsat.utils.Settings;
import main.java.org.eclipse.iofog.comsat.utils.SslManager;

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
		return channel.isActive() ? "Active" : "Inactive";
	}
	
}

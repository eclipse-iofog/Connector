package org.eclipse.iofog.comsat.restapi;

import org.eclipse.iofog.comsat.utils.Constants;
import org.eclipse.iofog.comsat.utils.LogUtil;
import org.eclipse.iofog.comsat.utils.SslManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;

/**
 * Created by Saeid on 6/26/2016.
 */
public class RestAPIServer implements Runnable {
    private final int PORT = 443;

//    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
//    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel ch;
    private SslContext sslCtx;
    private static RestAPIServer instance = null;
    
    private RestAPIServer() {
    	
    }
    
    public static RestAPIServer getInstance() {
		if (instance == null) {
			synchronized (RestAPIServer.class) {
				if (instance == null) 
					instance = new RestAPIServer();
			}
		}
		
		return instance;
    }
    
    public void run() {
        try {
    		sslCtx = SslManager.getSslContext();
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(Constants.bossGroup, Constants.workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new RestAPIChannelInitializer(sslCtx));

            ch = b.bind(PORT).sync().channel();
            LogUtil.info(String.format("RestAPI server started on port: %d", PORT));
            ch.closeFuture().sync();
        } catch (Exception e) {
			LogUtil.warning(e.getMessage());
        }
//        finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
    }
    
    public void stop() {
    	if (ch != null) { 
    		try {
    			ch.disconnect().sync();
    		} catch (Exception e) {
				LogUtil.warning(e.getMessage());
    		}
    	}
    }
}

package org.eclipse.iofog.comsat.publicsocket;

import org.eclipse.iofog.comsat.privatesocket.PrivateSocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.ssl.SslContext;

public class PublicSocketInitializer extends ChannelInitializer<SocketChannel> {

	private final PrivateSocket privateSocket;
	private final SslContext sslCtx;

    public PublicSocketInitializer(PrivateSocket privateSocket, SslContext sslCtx) {
        this.privateSocket = privateSocket;
        this.sslCtx = sslCtx;
    }
    
    @Override
	protected void initChannel(SocketChannel ch) throws Exception {
    	ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null)
        	pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        pipeline.addLast(new ByteArrayDecoder());
        pipeline.addLast(new ByteArrayEncoder());
        pipeline.addLast(new PublicSocketHandler(privateSocket));
    }

}

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

package org.eclipse.iofog.comsat.restapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

import javax.json.Json;
import javax.json.JsonObject;

import org.eclipse.iofog.comsat.ComSat;
import org.eclipse.iofog.comsat.commandline.CommandLineParser;
import org.eclipse.iofog.comsat.restapi.handlers.StatusHandler;
import org.eclipse.iofog.comsat.restapi.handlers.CommandLineHandler;
import org.eclipse.iofog.comsat.restapi.handlers.DirectConnectionAddHandler;
import org.eclipse.iofog.comsat.restapi.handlers.DirectConnectionRemoveHandler;
import org.eclipse.iofog.comsat.restapi.handlers.DirectConnectionRequestHandler;
import org.eclipse.iofog.comsat.restapi.handlers.NewMappingHandler;
import org.eclipse.iofog.comsat.restapi.handlers.RemoveMappingHandler;
import org.eclipse.iofog.comsat.utils.Constants;
import org.eclipse.iofog.comsat.utils.LogUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslContext;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * Created by Saeid on 6/26/2016.
 */
public class RestAPIChannelHandler extends ChannelInboundHandlerAdapter {
	
	private HttpRequest request;
	private ByteArrayOutputStream baos;
	private byte[] content;
	private final EventExecutorGroup executor;
//	private final SslContext sslCtx;
	
	public RestAPIChannelHandler(EventExecutorGroup executor, SslContext sslCtx) {
//		this.sslCtx = sslCtx;
		this.executor = executor; 
	}

	@Override
    public void channelActive(final ChannelHandlerContext ctx) {
//    	System.out.println(">>>> Starting handshake...");
//        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
//                new GenericFutureListener<Future<Channel>>() {
//                    @Override
//                    public void operationComplete(Future<Channel> future) throws Exception {
//                    	if (future.isSuccess())
//                    		System.out.println(">>>> Handshake done!");
//                    	else
//                    		System.out.println(">>>> Handshake failed!");
//                    }
//                });
    }
	
	@Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	try {
			if (msg instanceof FullHttpRequest) {
				FullHttpRequest request = (FullHttpRequest) msg;
				this.request = request;
				ByteBuf content = request.content();
				this.content = new byte[content.readableBytes()];
				content.readBytes(this.content);
				handleHttpRequest(ctx);
				return;
			} else if (msg instanceof HttpRequest) {					
				if (this.baos == null)
					this.baos = new ByteArrayOutputStream();
				request = (HttpRequest) msg;
			} else if (msg instanceof HttpContent) {
				HttpContent httpContent = (HttpContent) msg;
				ByteBuf content = httpContent.content();
				if (content.isReadable()) {
					try {
						content.readBytes(this.baos, content.readableBytes());
					} catch (IOException e) {
						String errorMsg = "Out of memory";
						LogUtil.warning(errorMsg);
						ByteBuf	errorMsgBytes = ctx.alloc().buffer();
						errorMsgBytes.writeBytes(errorMsg.getBytes());
						sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, errorMsgBytes));
						return;
					}
				}
				if (msg instanceof LastHttpContent) {					
					this.content = baos.toByteArray();
					handleHttpRequest(ctx);
				}
			}
		} catch (Exception e) {
			LogUtil.warning("Failed to initialize channel for the request: " + e.getMessage());
		}
    }
    
    private void handleHttpRequest(ChannelHandlerContext ctx) throws Exception {
    	String uri = request.getUri();
    	Callable<? extends Object> callable = null;

    	if (uri.equals(Constants.API_PORT_ADD)) {
			callable = new NewMappingHandler(request, ctx.alloc().buffer(), content);
		} else if (uri.equals(Constants.API_PORT_REMOVE)) {
			callable = new RemoveMappingHandler(request, ctx.alloc().buffer(), content);
		} else if (uri.equals(Constants.API_STATUS)) {
			callable = new StatusHandler(request, ctx.alloc().buffer(), content);
		} else if (uri.equals(Constants.API_COMMAND_LINE)) {
			callable = new CommandLineHandler(request, ctx.alloc().buffer(), content);
		} else if (uri.equals(Constants.API_DIRECT_REQUEST)) {
			callable = new DirectConnectionRequestHandler(request, ctx.alloc().buffer(), content, (InetSocketAddress)ctx.channel().remoteAddress());
		} else if (uri.equals(Constants.API_DIRECT_ADD)) {
			callable = new DirectConnectionAddHandler(request, ctx.alloc().buffer(), content);
		} else if (uri.equals(Constants.API_DIRECT_REMOVE)) {
			callable = new DirectConnectionRemoveHandler(request, ctx.alloc().buffer(), content);
		} else if (uri.equals(Constants.API_EXIT)) {
			String msg = "ComSat stopped... :)";
			sendFormattedResponse(ctx, msg);
			synchronized (ComSat.exitLock) {
				ComSat.exitLock.notifyAll();
			}
			return;
		} else if (uri.equals(Constants.API_REBOOT)) {
			CommandLineParser parser = new CommandLineParser();
			String msg = parser.parse("reboot", null);
			sendFormattedResponse(ctx, msg);
			synchronized (ComSat.exitLock) {
				ComSat.exitLock.notifyAll();
			}
			return;
		} else {
			ByteBuf	errorMsgBytes = ctx.alloc().buffer();
			String errorMsg = " Request not found ";
			errorMsgBytes.writeBytes(errorMsg.getBytes());
			sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, errorMsgBytes));
			return;
		}

		runTask(callable, ctx, request);
	}
    
    private void runTask(Callable<? extends Object> callable, final ChannelHandlerContext ctx, final HttpRequest req) {
		final Future<? extends Object> future = executor.submit(callable);
		future.addListener(new GenericFutureListener<Future<Object>>() {
			public void operationComplete(Future<Object> future)
					throws Exception {
				if (future.isSuccess()) {
					sendHttpResponse(ctx, req, (FullHttpResponse)future.get());
				} else {
					ctx.fireExceptionCaught(future.cause());
					ctx.close();
				}
			}
		});
	}
    
    private void sendFormattedResponse(ChannelHandlerContext ctx, String msg) throws Exception {
    	ByteBuf	msgBytes = ctx.alloc().buffer();
    	JsonObject response = Json.createObjectBuilder()
    			.add("timestamp", System.currentTimeMillis())
    			.add("message", msg)
    			.build();
		msgBytes.writeBytes(response.toString().getBytes());
		sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, msgBytes));
    }
    
    private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, FullHttpResponse res) throws Exception {
		if (res.getStatus().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
			HttpHeaders.setContentLength(res, res.content().readableBytes());
		}

		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LogUtil.warning(cause.getMessage());
        ctx.channel().close().sync();
    }
}

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

package org.eclipse.iofog.connector.restapi.handlers;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.json.Json;
import javax.json.JsonObject;

import io.netty.handler.ssl.SslContext;
import org.eclipse.iofog.connector.config.ConfigManager;
import org.eclipse.iofog.connector.config.Configuration;
import org.eclipse.iofog.connector.exceptions.DuplicateIdException;
import org.eclipse.iofog.connector.utils.LogUtil;
import org.eclipse.iofog.connector.utils.Settings;
import org.eclipse.iofog.connector.utils.SocketsManager;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.eclipse.iofog.connector.utils.SslManager;

public class NewMappingHandler implements Callable<Object> {
	
	private final HttpRequest request;
	private ByteBuf outputBuffer;
	private final byte[] content;
	
	public NewMappingHandler(HttpRequest request, ByteBuf outputBuffer, byte[] content) {
		this.request = request;
		this.outputBuffer = outputBuffer;
		this.content = content;
	}
	
	public Object call() throws Exception {
		HttpHeaders headers = request.headers();

		if (request.getMethod() != HttpMethod.POST) {
			LogUtil.warning("Request method not allowed");
			return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.METHOD_NOT_ALLOWED);
		}

		String contentType = headers.get(HttpHeaders.Names.CONTENT_TYPE); 
		if (contentType == null || !contentType.contains("application/x-www-form-urlencoded")) {
			String errorMessage = "Incorrect content type!";
			LogUtil.warning(errorMessage);
			outputBuffer.writeBytes(errorMessage.getBytes());
			return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, outputBuffer);
		}
		
		String requestBody = new String(content, StandardCharsets.UTF_8);
		
		QueryStringDecoder decoder = new QueryStringDecoder(requestBody, false);
		String mapping = decoder.parameters().get("mapping").get(0);
		
		JsonObject responseJson = null;
		try {
			JsonObject mappingJson = Json.createReader(new StringReader(mapping)).readObject();
			String mappingType = mappingJson.getString("type");
			Configuration config = new Configuration();
			String mappingId = UUID.randomUUID().toString();
			config.setId(mappingId);
			if (mappingType.equals("public")) {
				config.setPassCode2("");
				config.setMaxConnections1(mappingJson.getInt("maxconnections"));
				config.setMaxConnections2(0);
				config.setHeartBeatThreshold1(mappingJson.getInt("heartbeatabsencethreshold"));
				config.setHeartBeatThreshold2(0);
			} else if (mappingType.equals("private")) {
				config.setPassCode2(UUID.randomUUID().toString());
				config.setMaxConnections1(mappingJson.getInt("maxconnectionsport1"));
				config.setMaxConnections2(mappingJson.getInt("maxconnectionsport2"));
				config.setHeartBeatThreshold1(mappingJson.getInt("heartbeatabsencethresholdport1"));
				config.setHeartBeatThreshold2(mappingJson.getInt("heartbeatabsencethresholdport2"));
			} else {
				throw new Exception("Invalid mapping type!");
			}

			SocketsManager socketsManager = new SocketsManager();
			SslContext sslContext = Settings.isDevMode()
					? null
					: SslManager.getSslContext();

			int port1 = -1;
			do {
				port1 = Settings.getNextFreePort();
			} while (port1 != -1 && socketsManager.isPortInUse(port1));
			
			int port2 = -1;
			do {
				port2 = Settings.getNextFreePort();
			} while (port2 != -1 && socketsManager.isPortInUse(port2));
			
			if (port1 == -1 || port2 == -1) {
				throw new Exception("No free ports found on this server!");
			}
			
			config.setPort1(port1);
			config.setPort2(port2);
			config.setPassCode1(UUID.randomUUID().toString());
			
			LogUtil.info(String.format(">>>>>> ADD : %s (%d, %d)", mappingId, config.getPort1(), config.getPort2()));
			ConfigManager.addMapping(config);
			
			socketsManager.openPort(config, sslContext);
			responseJson = Json.createObjectBuilder()
					.add("status", "ok")
					.add("id", mappingId)
					.add("port1", config.getPort1())
					.add("port2", config.getPort2())
					.add("passcode1", config.getPassCode1())
					.add("passcode2", config.getPassCode2())
					.add("timestamp", System.currentTimeMillis())
					.build();
		} catch (DuplicateIdException e) {
			responseJson = Json.createObjectBuilder()
					.add("status", "failed")
					.add("error", "Duplicate mapping id!")
					.add("errormessage", e.getMessage())
					.add("timestamp", System.currentTimeMillis())
					.build();
			LogUtil.warning("Duplicate mapping id : " + e.getMessage());
		} catch (IOException e) {
			responseJson = Json.createObjectBuilder()
					.add("status", "failed")
					.add("error", "Error saving mapping!")
					.add("errormessage", e.getMessage())
					.add("timestamp", System.currentTimeMillis())
					.build();
			LogUtil.warning("Error saving mapping : " + e.getMessage());
		} catch (Exception e) {
			responseJson = Json.createObjectBuilder()
					.add("status", "failed")
					.add("errormessage", e.getMessage())
					.add("timestamp", System.currentTimeMillis())
					.build();
			LogUtil.warning("Error parsing mapping : " + e.getMessage());
		}
		
		String responseString = responseJson.toString();
		outputBuffer.writeBytes(responseString.getBytes());
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, outputBuffer);
		HttpHeaders.setContentLength(response, outputBuffer.readableBytes());
	    response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");
		return response; 
	}

}

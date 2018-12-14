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

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import org.eclipse.iofog.connector.config.ConfigManager;
import org.eclipse.iofog.connector.privatesocket.PrivateSocket;
import org.eclipse.iofog.connector.publicsocket.PublicSocket;
import org.eclipse.iofog.connector.utils.CmdProperties;
import org.eclipse.iofog.connector.utils.LogUtil;
import org.eclipse.iofog.connector.utils.SocketsManager;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class StatusHandler implements Callable<Object> {

	private final HttpRequest request;
	private ByteBuf outputBuffer;
	private final byte[] content;
	
	public StatusHandler(HttpRequest request, ByteBuf outputBuffer, byte[] content) {
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
		
		JsonObject responseJson = null;
		try {
			String mappingId = decoder.parameters().get("mappingid").get(0).toString();
			LogUtil.info(String.format(">>>>>> STATUS : %s", mappingId));
			
			JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
					.add("id", mappingId)
					.add("timestamp", System.currentTimeMillis());
			
			if (mappingId.equals("all")) {
				objectBuilder.add("status", "running")
							 .add("mappings", ConfigManager.getMappings().size() - ConfigManager.getDirectsCount())
							 .add("version", CmdProperties.getVersion());
			} else if (ConfigManager.getMappings().containsKey(mappingId)) {
				SocketsManager socketsManager = new SocketsManager();
				PublicSocket publicSocket = socketsManager.getPublicSocket(mappingId);
				PrivateSocket privateSocket = socketsManager.getPrivateSocket(mappingId);
	
				if (publicSocket != null) {
					objectBuilder.add("public", publicSocket.getStatus());
					if (privateSocket != null) {
						objectBuilder.add("private", privateSocket.getStatus());
					}
				} else if (privateSocket != null) {
					objectBuilder.add("private1", privateSocket.getStatus());
					if (privateSocket.getPairSocket() != null) {
						objectBuilder.add("private2", privateSocket.getPairSocket().getStatus());
					}
				}
			} else {
				objectBuilder.add("error", String.format("Mapping for id %s does not exist!", mappingId));
			}
			responseJson = objectBuilder.build();
		} catch (Exception e) {
			responseJson = Json.createObjectBuilder()
					.add("status", "failed")
					.add("error", "Error parsing mapping!")
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

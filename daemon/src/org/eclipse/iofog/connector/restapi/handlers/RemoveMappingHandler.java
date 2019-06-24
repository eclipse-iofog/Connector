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
import org.eclipse.iofog.connector.exceptions.NotFoundException;
import org.eclipse.iofog.connector.utils.LogUtil;
import org.eclipse.iofog.connector.utils.SocketsManager;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class RemoveMappingHandler implements Callable<Object> {
	
	private final HttpRequest request;
	private ByteBuf outputBuffer;
	private final byte[] content;
	
	public RemoveMappingHandler(HttpRequest request, ByteBuf outputBuffer, byte[] content) {
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

		JsonObject responseJson = null;
		HttpResponseStatus responseStatus = HttpResponseStatus.OK;
		try {
			String requestBody = new String(content, StandardCharsets.UTF_8);

			QueryStringDecoder decoder = new QueryStringDecoder(requestBody, false);
			String mappingId = decoder.parameters().get("mappingid").get(0);

			LogUtil.info(">>>>>> REMOVE : " + mappingId);
			ConfigManager.removeMapping(mappingId);

			SocketsManager socketsManager = new SocketsManager();
			socketsManager.closePort(mappingId);

			responseJson = Json.createObjectBuilder()
					.add("status", "ok")
					.add("id", mappingId)
					.add("timestamp", System.currentTimeMillis())
					.build();
		} catch (NotFoundException e) {
			responseJson = Json.createObjectBuilder()
					.add("status", "failed")
					.add("error", "Mapping does not exist!")
					.add("errormessage", e.getMessage())
					.add("timestamp", System.currentTimeMillis())
					.build();
			responseStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR;
			LogUtil.error("Mapping does not exist : " + e.getMessage());
			LogUtil.error(ExceptionUtils.exceptionStackTraceAsString(e));
		} catch (IOException e) {
			responseJson = Json.createObjectBuilder()
					.add("status", "failed")
					.add("error", "Error saving mapping!")
					.add("errormessage", e.getMessage())
					.add("timestamp", System.currentTimeMillis())
					.build();
			responseStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR;
			LogUtil.error("Error deleting mapping : " + e.getMessage());
			LogUtil.error(ExceptionUtils.exceptionStackTraceAsString(e));
		} catch (Exception e) {
			responseJson = Json.createObjectBuilder()
					.add("status", "failed")
					.add("errormessage", e.getMessage())
					.add("timestamp", System.currentTimeMillis())
					.build();
			responseStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR;
			LogUtil.error("Error deleting mapping : " + e.getMessage());
			LogUtil.error(ExceptionUtils.exceptionStackTraceAsString(e));
		}
		
		String responseString = responseJson.toString();
		outputBuffer.writeBytes(responseString.getBytes());
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseStatus, outputBuffer);
		HttpHeaders.setContentLength(response, outputBuffer.readableBytes());
	    response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");
		return response; 
	}

}

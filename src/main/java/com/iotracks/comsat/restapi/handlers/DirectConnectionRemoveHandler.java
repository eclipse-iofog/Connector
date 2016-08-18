package com.iotracks.comsat.restapi.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

import javax.json.Json;
import javax.json.JsonObject;

import com.iotracks.comsat.config.ConfigManager;
import com.iotracks.comsat.exceptions.NotFoundException;
import com.iotracks.comsat.utils.LogUtil;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

public class DirectConnectionRemoveHandler implements Callable<Object> {

	private final HttpRequest request;
	private ByteBuf outputBuffer;
	private final byte[] content;
	
	public DirectConnectionRemoveHandler(HttpRequest request, ByteBuf outputBuffer, byte[] content) {
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
		String directId = decoder.parameters().get("id").get(0);
		
		JsonObject responseJson = null;
		try {
			LogUtil.info(">>>>>> REMOVE DIRECT : " + directId);
			ConfigManager.removeMapping(directId);
			responseJson = Json.createObjectBuilder()
					.add("status", "ok")
					.add("id", directId)
					.add("timestamp", System.currentTimeMillis())
					.build();
		} catch (NotFoundException e) {
			responseJson = Json.createObjectBuilder()
					.add("status", "failed")
					.add("error", "Connection ID is not valid!")
					.add("timestamp", System.currentTimeMillis())
					.build();
			LogUtil.warning("Connection ID is not valid!");
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
			LogUtil.warning(e.getMessage());
		}
		
		String responseString = responseJson.toString();
		outputBuffer.writeBytes(responseString.getBytes());
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, outputBuffer);
		HttpHeaders.setContentLength(response, outputBuffer.readableBytes());
	    response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");
		return response; 
	}
}

package com.iotracks.comsat.restapi.handlers;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.json.Json;
import javax.json.JsonObject;

import com.iotracks.comsat.config.ConfigManager;
import com.iotracks.comsat.config.Configuration;
import com.iotracks.comsat.utils.LogUtil;
import com.iotracks.comsat.utils.Settings;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class DirectConnectionAddHandler implements Callable<Object> {

	private final HttpRequest request;
	private ByteBuf outputBuffer;
	
	public DirectConnectionAddHandler(HttpRequest request, ByteBuf outputBuffer, byte[] content) {
		this.request = request;
		this.outputBuffer = outputBuffer;
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
		try {
			String directId = UUID.randomUUID().toString();
			String passkey = UUID.randomUUID().toString();
			LogUtil.info(">>>>>> ADD DIRECT : " + directId);
			
			Configuration config = new Configuration();
			config.setId(directId);
			config.setPassCode1(passkey);
			config.setPort1(0);
			config.setPort2(0);
			config.setMaxConnections1(0);
			config.setMaxConnections2(0);
			config.setHeartBeatThreshold1(0);
			config.setHeartBeatThreshold2(0);
			config.setPassCode2("");
			
			ConfigManager.addMapping(config);
			responseJson = Json.createObjectBuilder()
					.add("status", "ok")
					.add("id", directId)
					.add("passkey", passkey)
					.add("port", Settings.getBrokerPort())
					.add("timestamp", System.currentTimeMillis())
					.build();
		} catch (IOException e) {
			responseJson = Json.createObjectBuilder()
					.add("status", "failed")
					.add("error", "Error saving config!")
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

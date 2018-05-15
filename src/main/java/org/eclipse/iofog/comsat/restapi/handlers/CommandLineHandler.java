package org.eclipse.iofog.comsat.restapi.handlers;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.json.Json;
import javax.json.JsonObject;

import org.eclipse.iofog.comsat.commandline.CommandLineParser;
import org.eclipse.iofog.comsat.utils.LogUtil;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

public class CommandLineHandler implements Callable<Object> {

	private final HttpRequest request;
	private ByteBuf outputBuffer;
	private final byte[] content;
	
	public CommandLineHandler(HttpRequest request, ByteBuf outputBuffer, byte[] content) {
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
			Map<String, List<String>> parameters = decoder.parameters();
			
			if (!parameters.containsKey("command"))
				throw new Exception("Invalid command line parameters");
			
			String command = parameters.get("command").get(0);
			String params = null;
			if (parameters.containsKey("params"))
				params = parameters.get("params").get(0);
			
			CommandLineParser parser = new CommandLineParser();
			responseJson = Json.createObjectBuilder()
					.add("response", parser.parse(command, params))
					.add("timestamp", System.currentTimeMillis())
					.build();
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

package main.java.org.eclipse.iofog.comsat.restapi.handlers;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import main.java.org.eclipse.iofog.comsat.config.ConfigManager;
import main.java.org.eclipse.iofog.comsat.private_socket.PrivateSocket;
import main.java.org.eclipse.iofog.comsat.public_socket.PublicSocket;
import main.java.org.eclipse.iofog.comsat.utils.Constants;
import main.java.org.eclipse.iofog.comsat.utils.LogUtil;
import main.java.org.eclipse.iofog.comsat.utils.SocketsManager;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

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
							 .add("version", Constants.VERSION);
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

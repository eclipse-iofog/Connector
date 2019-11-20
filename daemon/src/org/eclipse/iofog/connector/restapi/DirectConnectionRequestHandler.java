package org.eclipse.iofog.connector.restapi;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import io.javalin.plugin.openapi.annotations.ContentType;
import org.eclipse.iofog.connector.config.ConfigManager;
import org.eclipse.iofog.connector.config.DirectConnection;
import org.eclipse.iofog.connector.utils.LogUtil;
import org.jetbrains.annotations.NotNull;

import javax.json.Json;
import javax.json.JsonObject;

public class DirectConnectionRequestHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        String directId = context.formParam("id");
        String directPassKey = context.formParam("passkey");
        String portString = context.formParam("port");
        String ipAddress =  context.formParam("ip");
        String certificate = context.formParam("certificate");

        if (!ConfigManager.isDirectValid(directId, directPassKey))
            throw new NotFoundResponse();

        JsonObject responseJson;
        DirectConnection directConnection = ConfigManager.getDirectConnection(directId);
        if (directConnection == null) {
            int portNumber = Integer.parseInt(portString);
            LogUtil.info(">>>>>> ADD DIRECT REQUEST : " + directId);
            String remoteIpAddress =  context.req.getHeader("X-FORWARDED-FOR");
            if (remoteIpAddress == null) {
                remoteIpAddress = context.req.getRemoteAddr();
            }
            directConnection = new DirectConnection(remoteIpAddress, ipAddress, portNumber, certificate);
            ConfigManager.putDirectRequest(directId, directConnection);
            responseJson = Json.createObjectBuilder()
                    .add("status", "wait")
                    .add("id", directId)
                    .add("timestamp", System.currentTimeMillis())
                    .build();
        } else {
            LogUtil.info(">>>>>> RETURN DIRECT REQUEST : " + directId);
            responseJson = Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("id", directId)
                    .add("ip", directConnection.getIpAddress())
                    .add("remoteip", directConnection.getRemoteIpAddress())
                    .add("port", directConnection.getPort())
                    .add("certificate", directConnection.getCertificate())
                    .add("timestamp", System.currentTimeMillis())
                    .build();
        }

        context.status(200).contentType(ContentType.JSON).result(responseJson.toString());
    }
}

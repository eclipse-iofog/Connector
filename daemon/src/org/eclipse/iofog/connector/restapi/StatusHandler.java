package org.eclipse.iofog.connector.restapi;

import io.javalin.http.*;
import io.javalin.plugin.openapi.annotations.ContentType;
import org.eclipse.iofog.connector.config.ConfigManager;
import org.eclipse.iofog.connector.privatesocket.PrivateSocket;
import org.eclipse.iofog.connector.publicsocket.PublicSocket;
import org.eclipse.iofog.connector.utils.CmdProperties;
import org.eclipse.iofog.connector.utils.LogUtil;
import org.eclipse.iofog.connector.utils.Settings;
import org.eclipse.iofog.connector.utils.SocketsManager;
import org.jetbrains.annotations.NotNull;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

public class StatusHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        String mappingId = context.formParam("mappingid");

        try {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                    .add("id", mappingId)
                    .add("timestamp", System.currentTimeMillis());

            if (mappingId.equals("all")) {
                objectBuilder.add("status", "running")
                        .add("mappings", ConfigManager.getMappings().size() - ConfigManager.getDirectsCount())
                        .add("version", CmdProperties.getVersion())
                        .add("availablePorts", Settings.getNumberOfAvailablePorts());
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
                throw new NotFoundResponse(String.format("Mapping for id %s does not exist!", mappingId));
            }

            context.status(200).contentType(ContentType.JSON).result(objectBuilder.build().toString());
        } catch (HttpResponseException e) {
            throw e;
        } catch (Exception e) {
            LogUtil.warning("Error parsing mapping : " + e.getMessage());
            throw new InternalServerErrorResponse(e.getMessage());
        }
    }
}

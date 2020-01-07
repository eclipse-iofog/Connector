package org.eclipse.iofog.connector.restapi;

import io.javalin.http.*;
import io.javalin.plugin.openapi.annotations.ContentType;
import io.netty.util.internal.StringUtil;
import org.eclipse.iofog.connector.config.ConfigManager;
import org.eclipse.iofog.connector.exceptions.NotFoundException;
import org.eclipse.iofog.connector.utils.LogUtil;
import org.eclipse.iofog.connector.utils.SocketsManager;
import org.glassfish.jersey.internal.util.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import javax.json.Json;
import javax.json.JsonObject;

public class RemoveDirectConnectionHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        try {
            String mappingId = context.formParam("mappingid");
            if (StringUtil.isNullOrEmpty(mappingId)) {
                throw new BadRequestResponse("Mapping id must not be null or empty");
            }

            LogUtil.info(">>>>>> REMOVE : " + mappingId);
            ConfigManager.removeMapping(mappingId);

            SocketsManager socketsManager = new SocketsManager();
            socketsManager.closePort(mappingId);

            JsonObject responseJson = Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("id", mappingId)
                    .add("timestamp", System.currentTimeMillis())
                    .build();
            context.status(200).contentType(ContentType.JSON).result(responseJson.toString());
        } catch (NotFoundException e) {
            LogUtil.error("Mapping does not exist : " + e.getMessage());
            LogUtil.error(ExceptionUtils.exceptionStackTraceAsString(e));
            throw new NotFoundResponse("Mapping does not exist!");
        } catch (Exception e) {
            LogUtil.error("Error deleting mapping : " + e.getMessage());
            LogUtil.error(ExceptionUtils.exceptionStackTraceAsString(e));
            throw new InternalServerErrorResponse(e.getMessage());
        }
    }
}

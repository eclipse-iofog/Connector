package org.eclipse.iofog.connector.restapi;

import io.javalin.http.*;
import org.eclipse.iofog.connector.config.ConfigManager;
import org.eclipse.iofog.connector.exceptions.NotFoundException;
import org.eclipse.iofog.connector.restapi.response.RemoveMappingResponse;
import org.eclipse.iofog.connector.utils.LogUtil;
import org.eclipse.iofog.connector.utils.SocketsManager;
import org.glassfish.jersey.internal.util.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

public class RemoveMappingHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        String mappingId = context.formParam("mappingid");

        try {
            ConfigManager.removeMapping(mappingId);

            SocketsManager socketsManager = new SocketsManager();
            socketsManager.closePort(mappingId);

            context.status(200).json(new RemoveMappingResponse("ok", mappingId, System.currentTimeMillis()));
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

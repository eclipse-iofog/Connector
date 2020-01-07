package org.eclipse.iofog.connector.restapi;

import io.javalin.http.*;
import io.netty.util.internal.StringUtil;
import org.eclipse.iofog.connector.config.ConfigManager;
import org.eclipse.iofog.connector.config.Configuration;
import org.eclipse.iofog.connector.exceptions.DuplicateIdException;
import org.eclipse.iofog.connector.restapi.response.NewMappingResponse;
import org.eclipse.iofog.connector.utils.LogUtil;
import org.eclipse.iofog.connector.utils.Settings;
import org.eclipse.iofog.connector.utils.SocketsManager;
import org.glassfish.jersey.internal.util.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.Collections;
import java.util.UUID;

public class NewMappingHandler implements Handler {
    private static Object portLock = new Object();

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String mapping = context.formParam("mapping");
        if (StringUtil.isNullOrEmpty(mapping)) {
            throw new BadRequestResponse("Mapping must not be null or empty");
        }

        try {
            JsonObject mappingJson = Json.createReader(new StringReader(mapping)).readObject();
            String mappingType = mappingJson.getString("type");
            Configuration config = new Configuration();
            String mappingId = UUID.randomUUID().toString();
            config.setId(mappingId);
            if (mappingType.equals("public")) {
                config.setPassCode2("");
                config.setMaxConnections1(mappingJson.getInt("maxconnections"));
                config.setMaxConnections2(0);
                config.setHeartBeatThreshold1(mappingJson.getInt("heartbeatabsencethreshold"));
                config.setHeartBeatThreshold2(0);
            } else if (mappingType.equals("private")) {
                config.setPassCode2(UUID.randomUUID().toString());
                config.setMaxConnections1(mappingJson.getInt("maxconnectionsport1"));
                config.setMaxConnections2(mappingJson.getInt("maxconnectionsport2"));
                config.setHeartBeatThreshold1(mappingJson.getInt("heartbeatabsencethresholdport1"));
                config.setHeartBeatThreshold2(mappingJson.getInt("heartbeatabsencethresholdport2"));
            } else {
                throw new BadRequestResponse("Invalid mapping type!");
            }

            int port1 = -1;
            int port2 = -1;
            SocketsManager socketsManager;
            synchronized (portLock) {
                if (Settings.getNumberOfAvailablePorts() < 2) {
                    throw new HttpResponseException(507, "No enough available ports", Collections.emptyMap());
                }

                socketsManager = new SocketsManager();

                do {
                    port1 = Settings.getNextFreePort();
                } while (port1 != -1 && socketsManager.isPortInUse(port1));

                do {
                    port2 = Settings.getNextFreePort();
                } while (port2 != -1 && socketsManager.isPortInUse(port2));

                if (port1 == -1 || port2 == -1) {
                    throw new HttpResponseException(507, "No enough available ports", Collections.emptyMap());
                }
            }

            config.setPort1(port1);
            config.setPort2(port2);
            config.setPassCode1(UUID.randomUUID().toString());

            LogUtil.info(String.format("New port mapping: %s (%d, %d)", mappingId, config.getPort1(), config.getPort2()));
            ConfigManager.addMapping(config);

            socketsManager.openPort(config);

            context.status(200);

            context.status(200).json(new NewMappingResponse(
                "ok",
                mappingId,
                config.getPort1(),
                config.getPort2(),
                config.getPassCode1(),
                config.getPassCode2(),
                System.currentTimeMillis()
            ));
        } catch (DuplicateIdException e) {
            LogUtil.error("Duplicate mapping id : " + e.getMessage());
            LogUtil.error(ExceptionUtils.exceptionStackTraceAsString(e));
            throw new ConflictResponse("Duplicate mapping id");
        } catch (HttpResponseException e) {
            throw e;
        } catch (Exception e) {
            LogUtil.error("Error parsing mapping : " + e.getMessage());
            LogUtil.error(ExceptionUtils.exceptionStackTraceAsString(e));
            throw new InternalServerErrorResponse(e.getMessage());
        }
    }
}

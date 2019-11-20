package org.eclipse.iofog.connector.restapi;

import io.javalin.http.*;
import io.javalin.plugin.openapi.annotations.ContentType;
import org.eclipse.iofog.connector.config.ConfigManager;
import org.eclipse.iofog.connector.config.Configuration;
import org.eclipse.iofog.connector.utils.LogUtil;
import org.eclipse.iofog.connector.utils.Settings;
import org.jetbrains.annotations.NotNull;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.UUID;

public class NewDirectConnectionHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
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

        JsonObject responseJson = Json.createObjectBuilder()
                .add("status", "ok")
                .add("id", directId)
                .add("passkey", passkey)
                .add("port", Settings.getBrokerPort())
                .add("timestamp", System.currentTimeMillis())
                .build();
        context.status(200).contentType(ContentType.JSON).result(responseJson.toString());
    }
}

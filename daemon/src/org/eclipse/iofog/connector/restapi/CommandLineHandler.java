package org.eclipse.iofog.connector.restapi;

import io.javalin.http.*;
import io.javalin.plugin.openapi.annotations.ContentType;
import io.netty.util.internal.StringUtil;
import org.eclipse.iofog.connector.commandline.CommandLineParser;
import org.eclipse.iofog.connector.utils.LogUtil;
import org.jetbrains.annotations.NotNull;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;

public class CommandLineHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        String command = context.formParam("command");

        try {
            JsonObject responseJson;

            Map<String, String> params = new HashMap<>();
            String parameters = context.formParam("params");
            if (!StringUtil.isNullOrEmpty(parameters)) {
                List<String> paramList = Arrays.asList(parameters.split(" "));
                params = IntStream.range(1, paramList.size())
                        .boxed()
                        .collect(toMap(i -> paramList.get(i - 1), paramList::get));
            }

            CommandLineParser parser = new CommandLineParser();
            responseJson = Json.createObjectBuilder()
                    .add("response", parser.parse(command, params))
                    .add("timestamp", System.currentTimeMillis())
                    .build();

            context.status(200).contentType(ContentType.JSON).result(responseJson.toString());
        } catch (Exception e) {
            LogUtil.warning("Error parsing command line arguments : " + e.getMessage());
            throw new InternalServerErrorResponse(e.getMessage());
        }
    }
}

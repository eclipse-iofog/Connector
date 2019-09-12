package org.eclipse.iofog.connector.restapi;

import io.javalin.Javalin;
import org.eclipse.iofog.connector.utils.Constants;
import org.eclipse.iofog.connector.utils.Settings;

public class RestAPIServer {
    private int port;
    private Javalin server;
    private static RestAPIServer instance = null;

    private RestAPIServer(int port) {
        this.port = port;
    }

    private int getPort() {
        return port;
    }

    public static RestAPIServer getInstance() {
        if (instance == null) {
            synchronized (RestAPIServer.class) {
                if (instance == null) {
                    int port = Settings.isDevMode() ? Constants.HTTP_PORT : Constants.HTTPS_PORT;
                    instance = new RestAPIServer(port);
                }
            }
        }

        return instance;
    }

    public void start() {
        server = Javalin
                .create()
                .post("/api/v2/mapping/add", new NewMappingHandler())
                .post("/api/v2/mapping/remove", new RemoveMappingHandler())
                .post("/api/v2/status", new StatusHandler())
                .post("/api/v2/commandline", new CommandLineHandler())
                .post("/api/v2/direct", new DirectConnectionRequestHandler())
                .post("/api/v2/direct/add", new NewDirectConnectionHandler())
                .post("/api/v2/direct/remove", new RemoveDirectConnectionHandler())
                .start(port);
    }

    public void stop() {
        server.stop();
    }

    public boolean isOpen() {
        return server.server().getStarted();
    }
}

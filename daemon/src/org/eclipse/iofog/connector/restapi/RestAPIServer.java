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
                .post(Constants.API_PORT_ADD, new NewMappingHandler())
                .post(Constants.API_PORT_REMOVE, new RemoveMappingHandler())
                .post(Constants.API_STATUS, new StatusHandler())
                .post(Constants.API_COMMAND_LINE, new CommandLineHandler())
                .post(Constants.API_DIRECT_REQUEST, new DirectConnectionRequestHandler())
                .post(Constants.API_DIRECT_ADD, new NewDirectConnectionHandler())
                .post(Constants.API_DIRECT_REMOVE, new RemoveDirectConnectionHandler())
                .start(port);
    }

    public void stop() {
        server.stop();
    }
}

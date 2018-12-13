package org.eclipse.iofog.connector.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.eclipse.iofog.connector.utils.LogUtil.warning;

public class CmdProperties {

    private static final String MODULE_NAME = "CmdProperties";
    private static final String PROPERTIES_FILE_PATH = "cmd_messages.properties";

    private static final Properties cmdProperties;

    static {
        cmdProperties = new Properties();
        try (InputStream in = CmdProperties.class.getResourceAsStream(PROPERTIES_FILE_PATH)) {
            cmdProperties.load(in);
        } catch (IOException e) {
            warning(String.format("%s: %s", MODULE_NAME, e.getMessage()));
        }
    }

    public static String getVersion() {
        return cmdProperties.getProperty("version");
    }
}

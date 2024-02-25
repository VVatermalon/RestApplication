package org.example.restapplication.db;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    private static final Properties PROPERTIES = new Properties();
    private static final String PROPERTIES_FILE_NAME = "db.properties";

    static {
        loadProperties();
    }

    private PropertiesUtil() {
    }

    public static Properties getProperties() {
        return PROPERTIES;
    }

    private static void loadProperties() {
        try (var resourcesAsStream =
                     PropertiesUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            PROPERTIES.load(resourcesAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

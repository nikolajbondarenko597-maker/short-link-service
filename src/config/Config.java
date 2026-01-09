package config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
    private static Properties props = new Properties();

    static {
        try {
            if (Files.exists(Paths.get("app.properties"))) {
                props.load(Files.newInputStream(Paths.get("app.properties")));
            } else {
                // Дефолтные значения
                props.setProperty("link.length", "6");
                props.setProperty("link.ttl_seconds", "86400"); // 24 часа
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки конфигурации: " + e.getMessage());
        }
    }

    public static int getLinkLength() {
        return Integer.parseInt(props.getProperty("link.length"));
    }

    public static long getDefaultTTL() {
        return Long.parseLong(props.getProperty("link.ttl_seconds"));
    }
}
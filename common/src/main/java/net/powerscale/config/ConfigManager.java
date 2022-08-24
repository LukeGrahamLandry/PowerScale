package net.powerscale.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.powerscale.Platform;
import net.powerscale.PowerScale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    static final Logger LOGGER = LogManager.getLogger();
    public static Config currentConfig = Default.config;

    public static void initialize() {
        reload();
    }

    public static void reload() {
        Config config = Default.config;
        String configFileName = PowerScale.MODID + ".json";
        boolean sanitizeConfig = false;
        boolean overrideWithDefault = false;
        Path configDir = Platform.getConfigDir();

        try {
            Gson gson = new Gson();
            Path filePath = configDir.resolve(configFileName);
            boolean configFileExists = Files.exists(filePath);
            if (configFileExists) {
                // Read
                Reader reader = Files.newBufferedReader(filePath);
                config = gson.fromJson(reader, Config.class);
                reader.close();
                if (config.dimensions.length == 0) {
                    LOGGER.error("PowerScale config loaded empty config! The JSON is most likely malformed.");
                } else {
                    LOGGER.info("PowerScale config loaded: " + gson.toJson(config));
                }
            }

            if (config.meta != null) {
                sanitizeConfig = config.meta.sanitize_config;
                overrideWithDefault = config.meta.override_with_default;
                if (overrideWithDefault) {
                    config = Default.config;
                }
            }

            if (sanitizeConfig || overrideWithDefault || !configFileExists) {
                Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
                Writer writer = Files.newBufferedWriter(filePath);
                writer.write(prettyGson.toJson(config));
                writer.close();
                LOGGER.info("PowerScale config written: " + gson.toJson(config));
            }
        } catch(Exception e) {
            LOGGER.error("Failed loading PowerScale config: " + e.getMessage());
        }

        currentConfig = config;
    }
}

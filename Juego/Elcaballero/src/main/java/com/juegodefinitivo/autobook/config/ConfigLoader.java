package com.juegodefinitivo.autobook.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

public class ConfigLoader {

    public AppConfig load() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IllegalStateException("No se encontro application.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer la configuracion", e);
        }

        Path dataDir = Path.of(properties.getProperty("app.data.dir", ".autobook-data")).toAbsolutePath().normalize();
        Path saveFile = dataDir.resolve(properties.getProperty("app.save.file", "savegame.properties"));
        Path booksDir = Path.of(properties.getProperty("app.books.dir", "books")).toAbsolutePath().normalize();

        int maxChars = Integer.parseInt(properties.getProperty("app.scene.max.chars", "420"));
        int linesPerChunk = Integer.parseInt(properties.getProperty("app.scene.lines-per-chunk", "4"));

        return new AppConfig(
                properties.getProperty("app.name", "AutoBook Adventure"),
                dataDir,
                saveFile,
                booksDir,
                maxChars,
                linesPerChunk
        );
    }
}

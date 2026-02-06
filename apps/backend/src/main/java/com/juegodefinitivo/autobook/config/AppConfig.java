package com.juegodefinitivo.autobook.config;

import java.nio.file.Path;

public record AppConfig(
        String appName,
        Path dataDir,
        Path saveFile,
        Path booksDir,
        int sceneMaxChars,
        int sceneLinesPerChunk
) {
}

package com.juegodefinitivo.autobook.ingest;

import java.nio.file.Path;

public record BookAsset(String title, Path path, String format) {
}

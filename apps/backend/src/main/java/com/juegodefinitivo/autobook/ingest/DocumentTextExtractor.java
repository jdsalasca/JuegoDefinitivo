package com.juegodefinitivo.autobook.ingest;

import java.nio.file.Path;

public interface DocumentTextExtractor {
    String extract(Path path);
}

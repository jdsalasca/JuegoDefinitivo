package com.juegodefinitivo.autobook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juegodefinitivo.autobook.api.dto.TelemetryEventRequest;
import com.juegodefinitivo.autobook.api.dto.TelemetrySummaryResponse;
import com.juegodefinitivo.autobook.config.AppConfig;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

@Service
public class TelemetryService {

    private final Path eventsDir;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, LongAdder> byEvent = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LongAdder> byStage = new ConcurrentHashMap<>();

    public TelemetryService(AppConfig config, ObjectMapper objectMapper) {
        this.eventsDir = config.dataDir().resolve("telemetry");
        this.objectMapper = objectMapper;
    }

    public void record(TelemetryEventRequest event) {
        if (event == null || event.eventName() == null || event.eventName().isBlank()) {
            throw new IllegalArgumentException("eventName es requerido.");
        }

        String eventName = event.eventName().trim();
        String stage = event.stage() == null || event.stage().isBlank() ? "unknown" : event.stage().trim();
        byEvent.computeIfAbsent(eventName, key -> new LongAdder()).increment();
        byStage.computeIfAbsent(stage, key -> new LongAdder()).increment();

        Map<String, Object> line = new LinkedHashMap<>();
        line.put("timestamp", Instant.now().toString());
        line.put("sessionId", event.sessionId());
        line.put("eventName", eventName);
        line.put("stage", stage);
        line.put("elapsedMs", event.elapsedMs());
        line.put("metadata", event.metadata() == null ? Map.of() : event.metadata());
        append(line);
    }

    public TelemetrySummaryResponse summary() {
        return new TelemetrySummaryResponse(totalEvents(), snapshot(byEvent), snapshot(byStage));
    }

    private long totalEvents() {
        return byEvent.values().stream().mapToLong(LongAdder::sum).sum();
    }

    private Map<String, Long> snapshot(ConcurrentHashMap<String, LongAdder> source) {
        Map<String, Long> out = new LinkedHashMap<>();
        source.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> out.put(entry.getKey(), entry.getValue().sum()));
        return out;
    }

    private void append(Map<String, Object> payload) {
        try {
            Files.createDirectories(eventsDir);
            Path file = eventsDir.resolve("events-" + LocalDate.now() + ".jsonl");
            String line = objectMapper.writeValueAsString(payload) + System.lineSeparator();
            Files.writeString(file, line, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo registrar telemetria.", ex);
        }
    }
}


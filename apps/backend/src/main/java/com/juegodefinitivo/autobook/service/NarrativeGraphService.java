package com.juegodefinitivo.autobook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juegodefinitivo.autobook.api.dto.NarrativeGraphResponse;
import com.juegodefinitivo.autobook.api.dto.NarrativeLinkView;
import com.juegodefinitivo.autobook.config.AppConfig;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class NarrativeGraphService {

    private final Path graphDir;
    private final ObjectMapper objectMapper;

    public NarrativeGraphService(AppConfig config, ObjectMapper objectMapper) {
        this.graphDir = config.dataDir().resolve("narrative-graphs");
        this.objectMapper = objectMapper;
    }

    public NarrativeGraphResponse record(String sessionId, List<String> entities) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId es requerido.");
        }
        GraphData graph = loadGraph(sessionId);
        List<String> clean = entities == null ? List.of() : entities.stream()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();

        for (String entity : clean) {
            graph.nodes.merge(entity, 1, Integer::sum);
        }
        for (int i = 0; i < clean.size(); i++) {
            for (int j = i + 1; j < clean.size(); j++) {
                String a = clean.get(i);
                String b = clean.get(j);
                String key = edgeKey(a, b);
                graph.edges.merge(key, 1, Integer::sum);
            }
        }
        persist(sessionId, graph);
        return toResponse(sessionId, graph);
    }

    public NarrativeGraphResponse snapshot(String sessionId) {
        return toResponse(sessionId, loadGraph(sessionId));
    }

    private NarrativeGraphResponse toResponse(String sessionId, GraphData graph) {
        List<NarrativeLinkView> links = new ArrayList<>();
        graph.edges.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(20)
                .forEach(entry -> {
                    String[] pair = entry.getKey().split("\\|\\|", 2);
                    String source = pair.length > 0 ? pair[0] : "";
                    String target = pair.length > 1 ? pair[1] : "";
                    links.add(new NarrativeLinkView(source, target, entry.getValue()));
                });
        return new NarrativeGraphResponse(sessionId, new LinkedHashMap<>(graph.nodes), links);
    }

    private GraphData loadGraph(String sessionId) {
        Path file = graphDir.resolve(sessionId + ".json");
        if (!Files.exists(file)) {
            return new GraphData(new LinkedHashMap<>(), new LinkedHashMap<>());
        }
        try {
            return objectMapper.readValue(file.toFile(), GraphData.class);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar el grafo narrativo.", e);
        }
    }

    private void persist(String sessionId, GraphData graph) {
        try {
            Files.createDirectories(graphDir);
            Path file = graphDir.resolve(sessionId + ".json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), graph);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el grafo narrativo.", e);
        }
    }

    private String edgeKey(String a, String b) {
        if (a.compareToIgnoreCase(b) <= 0) {
            return a + "||" + b;
        }
        return b + "||" + a;
    }

    public static class GraphData {
        public Map<String, Integer> nodes = new LinkedHashMap<>();
        public Map<String, Integer> edges = new LinkedHashMap<>();

        public GraphData() {
        }

        public GraphData(Map<String, Integer> nodes, Map<String, Integer> edges) {
            this.nodes = nodes;
            this.edges = edges;
        }
    }
}


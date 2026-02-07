package com.juegodefinitivo.autobook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juegodefinitivo.autobook.config.AppConfig;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class TeacherWorkspaceMigrationService implements ApplicationRunner {

    private final TeacherWorkspaceService teacherWorkspaceService;
    private final ObjectMapper objectMapper;
    private final Path legacyFile;

    public TeacherWorkspaceMigrationService(
            TeacherWorkspaceService teacherWorkspaceService,
            ObjectMapper objectMapper,
            AppConfig config
    ) {
        this.teacherWorkspaceService = teacherWorkspaceService;
        this.objectMapper = objectMapper;
        this.legacyFile = config.dataDir().resolve("teacher-workspace.json");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (teacherWorkspaceService.hasAnyClassroom()) {
            return;
        }
        if (!Files.exists(legacyFile)) {
            return;
        }

        JsonNode root = objectMapper.readTree(legacyFile.toFile());
        TeacherWorkspaceService.LegacyWorkspace workspace = new TeacherWorkspaceService.LegacyWorkspace(
                parseClassrooms(root.path("classrooms")),
                parseStudents(root.path("students")),
                parseAssignments(root.path("assignments")),
                parseAttempts(root.path("attempts"))
        );

        if (workspace.classrooms().isEmpty()) {
            return;
        }

        teacherWorkspaceService.importLegacyWorkspace(workspace);
        Files.move(legacyFile, legacyFile.resolveSibling("teacher-workspace.migrated.json"), StandardCopyOption.REPLACE_EXISTING);
    }

    private List<TeacherWorkspaceService.LegacyClassroom> parseClassrooms(JsonNode node) {
        List<TeacherWorkspaceService.LegacyClassroom> rows = new ArrayList<>();
        Iterator<JsonNode> values = node.elements();
        while (values.hasNext()) {
            JsonNode row = values.next();
            rows.add(new TeacherWorkspaceService.LegacyClassroom(
                    row.path("id").asText(),
                    row.path("name").asText(),
                    row.path("teacherName").asText(),
                    parseInstant(row.path("createdAt").asText())
            ));
        }
        return rows;
    }

    private List<TeacherWorkspaceService.LegacyStudent> parseStudents(JsonNode node) {
        List<TeacherWorkspaceService.LegacyStudent> rows = new ArrayList<>();
        Iterator<JsonNode> values = node.elements();
        while (values.hasNext()) {
            JsonNode row = values.next();
            rows.add(new TeacherWorkspaceService.LegacyStudent(
                    row.path("id").asText(),
                    row.path("classroomId").asText(),
                    row.path("name").asText(),
                    parseInstant(row.path("createdAt").asText())
            ));
        }
        return rows;
    }

    private List<TeacherWorkspaceService.LegacyAssignment> parseAssignments(JsonNode node) {
        List<TeacherWorkspaceService.LegacyAssignment> rows = new ArrayList<>();
        Iterator<JsonNode> values = node.elements();
        while (values.hasNext()) {
            JsonNode row = values.next();
            rows.add(new TeacherWorkspaceService.LegacyAssignment(
                    row.path("id").asText(),
                    row.path("classroomId").asText(),
                    row.path("title").asText(),
                    row.path("bookPath").asText(),
                    parseInstant(row.path("createdAt").asText())
            ));
        }
        return rows;
    }

    private List<TeacherWorkspaceService.LegacyAttempt> parseAttempts(JsonNode node) {
        List<TeacherWorkspaceService.LegacyAttempt> rows = new ArrayList<>();
        if (!node.isArray()) {
            return rows;
        }
        for (JsonNode row : node) {
            rows.add(new TeacherWorkspaceService.LegacyAttempt(
                    row.path("id").asText(),
                    row.path("studentId").asText(),
                    row.path("assignmentId").asText(),
                    row.path("sessionId").asText(),
                    parseInstant(row.path("createdAt").asText())
            ));
        }
        return rows;
    }

    private Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return Instant.now();
        }
        try {
            return Instant.parse(value);
        } catch (Exception ignored) {
            return Instant.now();
        }
    }
}

package com.juegodefinitivo.autobook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juegodefinitivo.autobook.api.dto.AssignmentView;
import com.juegodefinitivo.autobook.api.dto.ClassroomDashboardResponse;
import com.juegodefinitivo.autobook.api.dto.ClassroomView;
import com.juegodefinitivo.autobook.api.dto.GameStateResponse;
import com.juegodefinitivo.autobook.api.dto.StudentProgressView;
import com.juegodefinitivo.autobook.api.dto.StudentView;
import com.juegodefinitivo.autobook.config.AppConfig;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeacherWorkspaceService {

    private final Path workspaceFile;
    private final ObjectMapper objectMapper;
    private final GameFacadeService gameFacadeService;

    public TeacherWorkspaceService(AppConfig config, ObjectMapper objectMapper, GameFacadeService gameFacadeService) {
        this.workspaceFile = config.dataDir().resolve("teacher-workspace.json");
        this.objectMapper = objectMapper;
        this.gameFacadeService = gameFacadeService;
    }

    public List<ClassroomView> listClassrooms() {
        Workspace workspace = load();
        return workspace.classrooms.values().stream()
                .map(classroom -> toClassroomView(workspace, classroom))
                .sorted(Comparator.comparing(ClassroomView::name))
                .toList();
    }

    public ClassroomView createClassroom(String name, String teacherName) {
        String safeName = safeValue(name, "Aula");
        String safeTeacher = safeValue(teacherName, "Docente");
        Workspace workspace = load();
        String id = "cls_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        Classroom classroom = new Classroom(id, safeName, safeTeacher, Instant.now().toString());
        workspace.classrooms.put(id, classroom);
        persist(workspace);
        return toClassroomView(workspace, classroom);
    }

    public StudentView addStudent(String classroomId, String name) {
        Workspace workspace = load();
        requireClassroom(workspace, classroomId);
        String id = "stu_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        Student student = new Student(id, classroomId, safeValue(name, "Estudiante"), Instant.now().toString());
        workspace.students.put(id, student);
        persist(workspace);
        return new StudentView(student.id, student.classroomId, student.name);
    }

    public List<StudentView> listStudents(String classroomId) {
        Workspace workspace = load();
        requireClassroom(workspace, classroomId);
        return workspace.students.values().stream()
                .filter(student -> student.classroomId.equals(classroomId))
                .sorted(Comparator.comparing(student -> student.name.toLowerCase(Locale.ROOT)))
                .map(student -> new StudentView(student.id, student.classroomId, student.name))
                .toList();
    }

    public AssignmentView createAssignment(String classroomId, String title, String bookPath) {
        Workspace workspace = load();
        requireClassroom(workspace, classroomId);
        String id = "asg_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        Assignment assignment = new Assignment(
                id,
                classroomId,
                safeValue(title, "Lectura asignada"),
                safeValue(bookPath, ""),
                Instant.now().toString()
        );
        workspace.assignments.put(id, assignment);
        persist(workspace);
        return new AssignmentView(assignment.id, assignment.classroomId, assignment.title, assignment.bookPath);
    }

    public List<AssignmentView> listAssignments(String classroomId) {
        Workspace workspace = load();
        requireClassroom(workspace, classroomId);
        return workspace.assignments.values().stream()
                .filter(assignment -> assignment.classroomId.equals(classroomId))
                .sorted(Comparator.comparing(assignment -> assignment.title.toLowerCase(Locale.ROOT)))
                .map(assignment -> new AssignmentView(assignment.id, assignment.classroomId, assignment.title, assignment.bookPath))
                .toList();
    }

    public void linkAttempt(String studentId, String assignmentId, String sessionId) {
        Workspace workspace = load();
        Student student = workspace.students.get(studentId);
        if (student == null) {
            throw new IllegalArgumentException("studentId no existe.");
        }
        Assignment assignment = workspace.assignments.get(assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("assignmentId no existe.");
        }
        if (!student.classroomId.equals(assignment.classroomId)) {
            throw new IllegalArgumentException("student y assignment pertenecen a aulas diferentes.");
        }
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId es requerido.");
        }
        Attempt attempt = new Attempt(
                "att_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12),
                studentId,
                assignmentId,
                sessionId.trim(),
                Instant.now().toString()
        );
        workspace.attempts.add(attempt);
        persist(workspace);
    }

    public ClassroomDashboardResponse getDashboard(String classroomId) {
        Workspace workspace = load();
        Classroom classroom = requireClassroom(workspace, classroomId);

        List<Student> classroomStudents = workspace.students.values().stream()
                .filter(student -> student.classroomId.equals(classroomId))
                .sorted(Comparator.comparing(student -> student.name.toLowerCase(Locale.ROOT)))
                .toList();

        List<Assignment> classroomAssignments = workspace.assignments.values().stream()
                .filter(assignment -> assignment.classroomId.equals(classroomId))
                .toList();

        List<StudentProgressView> progressViews = new ArrayList<>();
        for (Student student : classroomStudents) {
            List<Attempt> attempts = workspace.attempts.stream()
                    .filter(attempt -> attempt.studentId.equals(student.id))
                    .filter(attempt -> {
                        Assignment assignment = workspace.assignments.get(attempt.assignmentId);
                        return assignment != null && assignment.classroomId.equals(classroomId);
                    })
                    .toList();

            List<GameStateResponse> states = attempts.stream()
                    .map(attempt -> safeState(attempt.sessionId))
                    .filter(state -> state != null)
                    .toList();

            int avgScore = (int) states.stream().mapToInt(GameStateResponse::score).average().orElse(0);
            int avgCorrect = (int) states.stream().mapToInt(GameStateResponse::correctAnswers).average().orElse(0);
            int avgProgress = (int) states.stream().mapToInt(this::progressPercent).average().orElse(0);
            int completed = (int) states.stream().filter(GameStateResponse::completed).count();
            String difficulty = dominantDifficulty(states);

            progressViews.add(new StudentProgressView(
                    student.id,
                    student.name,
                    attempts.size(),
                    completed,
                    avgScore,
                    avgCorrect,
                    avgProgress,
                    difficulty
            ));
        }

        return new ClassroomDashboardResponse(
                classroom.id,
                classroom.name,
                classroom.teacherName,
                classroomStudents.size(),
                classroomAssignments.size(),
                progressViews
        );
    }

    public String exportClassroomCsv(String classroomId) {
        ClassroomDashboardResponse dashboard = getDashboard(classroomId);
        StringBuilder csv = new StringBuilder();
        csv.append("classroom_id,classroom_name,teacher,student_id,student_name,attempts,completed_attempts,avg_score,avg_correct_answers,avg_progress_percent,dominant_difficulty")
                .append('\n');
        for (StudentProgressView view : dashboard.studentProgress()) {
            csv.append(csv(dashboard.classroomId())).append(',')
                    .append(csv(dashboard.classroomName())).append(',')
                    .append(csv(dashboard.teacherName())).append(',')
                    .append(csv(view.studentId())).append(',')
                    .append(csv(view.studentName())).append(',')
                    .append(view.attempts()).append(',')
                    .append(view.completedAttempts()).append(',')
                    .append(view.averageScore()).append(',')
                    .append(view.averageCorrectAnswers()).append(',')
                    .append(view.averageProgressPercent()).append(',')
                    .append(csv(view.dominantDifficulty()))
                    .append('\n');
        }
        return csv.toString();
    }

    private GameStateResponse safeState(String sessionId) {
        try {
            return gameFacadeService.getState(sessionId);
        } catch (Exception ignored) {
            return null;
        }
    }

    private int progressPercent(GameStateResponse state) {
        if (state.currentScene() == null) {
            return state.completed() ? 100 : 0;
        }
        if (state.currentScene().total() <= 0) {
            return 0;
        }
        return (int) (((state.currentScene().index() + 1) / (double) state.currentScene().total()) * 100);
    }

    private String dominantDifficulty(List<GameStateResponse> states) {
        if (states.isEmpty()) {
            return "UNKNOWN";
        }
        Map<String, Long> counts = states.stream()
                .collect(Collectors.groupingBy(GameStateResponse::adaptiveDifficulty, LinkedHashMap::new, Collectors.counting()));
        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("UNKNOWN");
    }

    private ClassroomView toClassroomView(Workspace workspace, Classroom classroom) {
        int students = (int) workspace.students.values().stream()
                .filter(student -> student.classroomId.equals(classroom.id))
                .count();
        int assignments = (int) workspace.assignments.values().stream()
                .filter(assignment -> assignment.classroomId.equals(classroom.id))
                .count();
        return new ClassroomView(classroom.id, classroom.name, classroom.teacherName, students, assignments);
    }

    private Classroom requireClassroom(Workspace workspace, String classroomId) {
        Classroom classroom = workspace.classrooms.get(classroomId);
        if (classroom == null) {
            throw new IllegalArgumentException("classroomId no existe.");
        }
        return classroom;
    }

    private String safeValue(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    private String csv(String value) {
        String safe = value == null ? "" : value;
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    private Workspace load() {
        if (!Files.exists(workspaceFile)) {
            return new Workspace();
        }
        try {
            return objectMapper.readValue(workspaceFile.toFile(), Workspace.class);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar teacher workspace.", e);
        }
    }

    private void persist(Workspace workspace) {
        try {
            Files.createDirectories(workspaceFile.getParent());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(workspaceFile.toFile(), workspace);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo persistir teacher workspace.", e);
        }
    }

    public static class Workspace {
        public Map<String, Classroom> classrooms = new LinkedHashMap<>();
        public Map<String, Student> students = new LinkedHashMap<>();
        public Map<String, Assignment> assignments = new LinkedHashMap<>();
        public List<Attempt> attempts = new ArrayList<>();
    }

    public static class Classroom {
        public String id;
        public String name;
        public String teacherName;
        public String createdAt;

        public Classroom() {
        }

        public Classroom(String id, String name, String teacherName, String createdAt) {
            this.id = id;
            this.name = name;
            this.teacherName = teacherName;
            this.createdAt = createdAt;
        }
    }

    public static class Student {
        public String id;
        public String classroomId;
        public String name;
        public String createdAt;

        public Student() {
        }

        public Student(String id, String classroomId, String name, String createdAt) {
            this.id = id;
            this.classroomId = classroomId;
            this.name = name;
            this.createdAt = createdAt;
        }
    }

    public static class Assignment {
        public String id;
        public String classroomId;
        public String title;
        public String bookPath;
        public String createdAt;

        public Assignment() {
        }

        public Assignment(String id, String classroomId, String title, String bookPath, String createdAt) {
            this.id = id;
            this.classroomId = classroomId;
            this.title = title;
            this.bookPath = bookPath;
            this.createdAt = createdAt;
        }
    }

    public static class Attempt {
        public String id;
        public String studentId;
        public String assignmentId;
        public String sessionId;
        public String createdAt;

        public Attempt() {
        }

        public Attempt(String id, String studentId, String assignmentId, String sessionId, String createdAt) {
            this.id = id;
            this.studentId = studentId;
            this.assignmentId = assignmentId;
            this.sessionId = sessionId;
            this.createdAt = createdAt;
        }
    }
}

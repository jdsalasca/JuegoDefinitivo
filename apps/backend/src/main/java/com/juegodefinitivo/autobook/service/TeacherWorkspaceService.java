package com.juegodefinitivo.autobook.service;

import com.juegodefinitivo.autobook.api.dto.AssignmentView;
import com.juegodefinitivo.autobook.api.dto.ClassroomDashboardResponse;
import com.juegodefinitivo.autobook.api.dto.ClassroomView;
import com.juegodefinitivo.autobook.api.dto.GameStateResponse;
import com.juegodefinitivo.autobook.api.dto.StudentProgressView;
import com.juegodefinitivo.autobook.api.dto.StudentView;
import com.juegodefinitivo.autobook.persistence.teacher.TeacherWorkspaceRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeacherWorkspaceService {

    private final TeacherWorkspaceRepository repository;
    private final GameFacadeService gameFacadeService;

    public TeacherWorkspaceService(TeacherWorkspaceRepository repository, GameFacadeService gameFacadeService) {
        this.repository = repository;
        this.gameFacadeService = gameFacadeService;
    }

    public List<ClassroomView> listClassrooms() {
        return repository.listClassrooms().stream()
                .map(this::toClassroomView)
                .toList();
    }

    public ClassroomView createClassroom(String name, String teacherName) {
        TeacherWorkspaceRepository.ClassroomRow classroom = new TeacherWorkspaceRepository.ClassroomRow(
                newId("cls_"),
                safeValue(name, "Aula"),
                safeValue(teacherName, "Docente"),
                Instant.now()
        );
        repository.insertClassroom(classroom);
        return toClassroomView(classroom);
    }

    public StudentView addStudent(String classroomId, String name) {
        requireClassroom(classroomId);
        TeacherWorkspaceRepository.StudentRow student = new TeacherWorkspaceRepository.StudentRow(
                newId("stu_"),
                classroomId,
                safeValue(name, "Estudiante"),
                Instant.now()
        );
        repository.insertStudent(student);
        return new StudentView(student.id(), student.classroomId(), student.name());
    }

    public List<StudentView> listStudents(String classroomId) {
        requireClassroom(classroomId);
        return repository.listStudents(classroomId).stream()
                .map(student -> new StudentView(student.id(), student.classroomId(), student.name()))
                .toList();
    }

    public AssignmentView createAssignment(String classroomId, String title, String bookPath) {
        requireClassroom(classroomId);
        TeacherWorkspaceRepository.AssignmentRow assignment = new TeacherWorkspaceRepository.AssignmentRow(
                newId("asg_"),
                classroomId,
                safeValue(title, "Lectura asignada"),
                safeValue(bookPath, ""),
                Instant.now()
        );
        repository.insertAssignment(assignment);
        return new AssignmentView(assignment.id(), assignment.classroomId(), assignment.title(), assignment.bookPath());
    }

    public List<AssignmentView> listAssignments(String classroomId) {
        requireClassroom(classroomId);
        return repository.listAssignments(classroomId).stream()
                .map(assignment -> new AssignmentView(assignment.id(), assignment.classroomId(), assignment.title(), assignment.bookPath()))
                .toList();
    }

    public void linkAttempt(String studentId, String assignmentId, String sessionId) {
        TeacherWorkspaceRepository.StudentRow student = repository.findStudent(studentId)
                .orElseThrow(() -> new IllegalArgumentException("studentId no existe."));

        TeacherWorkspaceRepository.AssignmentRow assignment = repository.findAssignment(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("assignmentId no existe."));

        if (!student.classroomId().equals(assignment.classroomId())) {
            throw new IllegalArgumentException("student y assignment pertenecen a aulas diferentes.");
        }
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId es requerido.");
        }
        String normalizedSessionId = sessionId.trim();
        if (repository.existsAttempt(studentId, assignmentId, normalizedSessionId)) {
            throw new IllegalArgumentException("Ese intento ya esta vinculado para este estudiante y asignacion.");
        }

        repository.insertAttempt(new TeacherWorkspaceRepository.AttemptRow(
                newId("att_"),
                studentId,
                assignmentId,
                normalizedSessionId,
                Instant.now()
        ));
    }

    public ClassroomDashboardResponse getDashboard(String classroomId) {
        return getDashboard(classroomId, null, null);
    }

    public ClassroomDashboardResponse getDashboard(String classroomId, LocalDate from, LocalDate to) {
        TeacherWorkspaceRepository.ClassroomRow classroom = requireClassroom(classroomId);
        List<TeacherWorkspaceRepository.StudentRow> students = repository.listStudents(classroomId);
        List<TeacherWorkspaceRepository.AssignmentRow> assignments = repository.listAssignments(classroomId);
        List<TeacherWorkspaceRepository.AttemptRow> attempts = loadAttempts(classroomId, from, to);

        List<StudentProgressView> progressViews = new ArrayList<>();
        int totalAttempts = 0;
        int totalCompletedAttempts = 0;
        for (TeacherWorkspaceRepository.StudentRow student : students) {
            List<TeacherWorkspaceRepository.AttemptRow> studentAttempts = attempts.stream()
                    .filter(attempt -> attempt.studentId().equals(student.id()))
                    .toList();

            List<GameStateResponse> states = studentAttempts.stream()
                    .map(attempt -> safeState(attempt.sessionId()))
                    .filter(state -> state != null)
                    .toList();

            int avgScore = (int) states.stream().mapToInt(GameStateResponse::score).average().orElse(0);
            int avgCorrect = (int) states.stream().mapToInt(GameStateResponse::correctAnswers).average().orElse(0);
            int avgProgress = (int) states.stream().mapToInt(this::progressPercent).average().orElse(0);
            int completed = (int) states.stream().filter(GameStateResponse::completed).count();
            String difficulty = dominantDifficulty(states);
            totalAttempts += studentAttempts.size();
            totalCompletedAttempts += completed;

            progressViews.add(new StudentProgressView(
                    student.id(),
                    student.name(),
                    studentAttempts.size(),
                    completed,
                    avgScore,
                    avgCorrect,
                    avgProgress,
                    difficulty
            ));
        }

        int activeAttempts = Math.max(0, totalAttempts - totalCompletedAttempts);
        int abandonmentRatePercent = totalAttempts == 0
                ? 0
                : (int) ((activeAttempts / (double) totalAttempts) * 100);

        return new ClassroomDashboardResponse(
                classroom.id(),
                classroom.name(),
                classroom.teacherName(),
                students.size(),
                assignments.size(),
                activeAttempts,
                totalCompletedAttempts,
                abandonmentRatePercent,
                progressViews
        );
    }

    public String exportClassroomCsv(String classroomId) {
        return exportClassroomCsv(classroomId, null, null);
    }

    public String exportClassroomCsv(String classroomId, LocalDate from, LocalDate to) {
        ClassroomDashboardResponse dashboard = getDashboard(classroomId, from, to);
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

    public boolean hasAnyClassroom() {
        return repository.hasAnyClassroom();
    }

    public void importLegacyWorkspace(LegacyWorkspace workspace) {
        for (LegacyClassroom classroom : workspace.classrooms()) {
            repository.insertClassroom(new TeacherWorkspaceRepository.ClassroomRow(
                    classroom.id(),
                    classroom.name(),
                    classroom.teacherName(),
                    classroom.createdAt()
            ));
        }
        for (LegacyStudent student : workspace.students()) {
            repository.insertStudent(new TeacherWorkspaceRepository.StudentRow(
                    student.id(),
                    student.classroomId(),
                    student.name(),
                    student.createdAt()
            ));
        }
        for (LegacyAssignment assignment : workspace.assignments()) {
            repository.insertAssignment(new TeacherWorkspaceRepository.AssignmentRow(
                    assignment.id(),
                    assignment.classroomId(),
                    assignment.title(),
                    assignment.bookPath(),
                    assignment.createdAt()
            ));
        }
        for (LegacyAttempt attempt : workspace.attempts()) {
            repository.insertAttempt(new TeacherWorkspaceRepository.AttemptRow(
                    attempt.id(),
                    attempt.studentId(),
                    attempt.assignmentId(),
                    attempt.sessionId(),
                    attempt.createdAt()
            ));
        }
    }

    private TeacherWorkspaceRepository.ClassroomRow requireClassroom(String classroomId) {
        return repository.findClassroom(classroomId)
                .orElseThrow(() -> new IllegalArgumentException("classroomId no existe."));
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

    private String newId(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private ClassroomView toClassroomView(TeacherWorkspaceRepository.ClassroomRow classroom) {
        int students = repository.listStudents(classroom.id()).size();
        int assignments = repository.listAssignments(classroom.id()).size();
        return new ClassroomView(classroom.id(), classroom.name(), classroom.teacherName(), students, assignments);
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

    private List<TeacherWorkspaceRepository.AttemptRow> loadAttempts(String classroomId, LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            return repository.listAttemptsForClassroom(classroomId);
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Rango de fechas invalido: 'to' no puede ser anterior a 'from'.");
        }
        Instant fromInclusive = from.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant toExclusive = to.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        return repository.listAttemptsForClassroom(classroomId, fromInclusive, toExclusive);
    }

    public record LegacyWorkspace(
            List<LegacyClassroom> classrooms,
            List<LegacyStudent> students,
            List<LegacyAssignment> assignments,
            List<LegacyAttempt> attempts
    ) {
    }

    public record LegacyClassroom(String id, String name, String teacherName, Instant createdAt) {
    }

    public record LegacyStudent(String id, String classroomId, String name, Instant createdAt) {
    }

    public record LegacyAssignment(String id, String classroomId, String title, String bookPath, Instant createdAt) {
    }

    public record LegacyAttempt(String id, String studentId, String assignmentId, String sessionId, Instant createdAt) {
    }
}

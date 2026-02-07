package com.juegodefinitivo.autobook.persistence.teacher;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TeacherWorkspaceRepository {

    List<ClassroomRow> listClassrooms();

    Optional<ClassroomRow> findClassroom(String classroomId);

    void insertClassroom(ClassroomRow classroom);

    List<StudentRow> listStudents(String classroomId);

    Optional<StudentRow> findStudent(String studentId);

    void insertStudent(StudentRow student);

    List<AssignmentRow> listAssignments(String classroomId);

    Optional<AssignmentRow> findAssignment(String assignmentId);

    void insertAssignment(AssignmentRow assignment);

    void insertAttempt(AttemptRow attempt);

    List<AttemptRow> listAttemptsForClassroom(String classroomId);

    boolean hasAnyClassroom();

    record ClassroomRow(String id, String name, String teacherName, Instant createdAt) {
    }

    record StudentRow(String id, String classroomId, String name, Instant createdAt) {
    }

    record AssignmentRow(String id, String classroomId, String title, String bookPath, Instant createdAt) {
    }

    record AttemptRow(String id, String studentId, String assignmentId, String sessionId, Instant createdAt) {
    }
}

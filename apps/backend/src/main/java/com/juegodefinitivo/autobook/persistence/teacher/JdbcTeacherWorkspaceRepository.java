package com.juegodefinitivo.autobook.persistence.teacher;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTeacherWorkspaceRepository implements TeacherWorkspaceRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTeacherWorkspaceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ClassroomRow> listClassrooms() {
        return jdbcTemplate.query(
                """
                        SELECT id, name, teacher_name, created_at
                        FROM classrooms
                        ORDER BY LOWER(name)
                        """,
                (rs, rowNum) -> new ClassroomRow(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("teacher_name"),
                        rs.getTimestamp("created_at").toInstant()
                )
        );
    }

    @Override
    public Optional<ClassroomRow> findClassroom(String classroomId) {
        List<ClassroomRow> rows = jdbcTemplate.query(
                """
                        SELECT id, name, teacher_name, created_at
                        FROM classrooms
                        WHERE id = ?
                        """,
                (rs, rowNum) -> new ClassroomRow(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("teacher_name"),
                        rs.getTimestamp("created_at").toInstant()
                ),
                classroomId
        );
        return rows.stream().findFirst();
    }

    @Override
    public void insertClassroom(ClassroomRow classroom) {
        jdbcTemplate.update(
                """
                        INSERT INTO classrooms (id, name, teacher_name, created_at)
                        VALUES (?, ?, ?, ?)
                        """,
                classroom.id(),
                classroom.name(),
                classroom.teacherName(),
                Timestamp.from(classroom.createdAt())
        );
    }

    @Override
    public List<StudentRow> listStudents(String classroomId) {
        return jdbcTemplate.query(
                """
                        SELECT id, classroom_id, name, created_at
                        FROM students
                        WHERE classroom_id = ?
                        ORDER BY LOWER(name)
                        """,
                (rs, rowNum) -> new StudentRow(
                        rs.getString("id"),
                        rs.getString("classroom_id"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at").toInstant()
                ),
                classroomId
        );
    }

    @Override
    public Optional<StudentRow> findStudent(String studentId) {
        List<StudentRow> rows = jdbcTemplate.query(
                """
                        SELECT id, classroom_id, name, created_at
                        FROM students
                        WHERE id = ?
                        """,
                (rs, rowNum) -> new StudentRow(
                        rs.getString("id"),
                        rs.getString("classroom_id"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at").toInstant()
                ),
                studentId
        );
        return rows.stream().findFirst();
    }

    @Override
    public void insertStudent(StudentRow student) {
        jdbcTemplate.update(
                """
                        INSERT INTO students (id, classroom_id, name, created_at)
                        VALUES (?, ?, ?, ?)
                        """,
                student.id(),
                student.classroomId(),
                student.name(),
                Timestamp.from(student.createdAt())
        );
    }

    @Override
    public List<AssignmentRow> listAssignments(String classroomId) {
        return jdbcTemplate.query(
                """
                        SELECT id, classroom_id, title, book_path, created_at
                        FROM assignments
                        WHERE classroom_id = ?
                        ORDER BY LOWER(title)
                        """,
                (rs, rowNum) -> new AssignmentRow(
                        rs.getString("id"),
                        rs.getString("classroom_id"),
                        rs.getString("title"),
                        rs.getString("book_path"),
                        rs.getTimestamp("created_at").toInstant()
                ),
                classroomId
        );
    }

    @Override
    public Optional<AssignmentRow> findAssignment(String assignmentId) {
        List<AssignmentRow> rows = jdbcTemplate.query(
                """
                        SELECT id, classroom_id, title, book_path, created_at
                        FROM assignments
                        WHERE id = ?
                        """,
                (rs, rowNum) -> new AssignmentRow(
                        rs.getString("id"),
                        rs.getString("classroom_id"),
                        rs.getString("title"),
                        rs.getString("book_path"),
                        rs.getTimestamp("created_at").toInstant()
                ),
                assignmentId
        );
        return rows.stream().findFirst();
    }

    @Override
    public void insertAssignment(AssignmentRow assignment) {
        jdbcTemplate.update(
                """
                        INSERT INTO assignments (id, classroom_id, title, book_path, created_at)
                        VALUES (?, ?, ?, ?, ?)
                        """,
                assignment.id(),
                assignment.classroomId(),
                assignment.title(),
                assignment.bookPath(),
                Timestamp.from(assignment.createdAt())
        );
    }

    @Override
    public void insertAttempt(AttemptRow attempt) {
        jdbcTemplate.update(
                """
                        INSERT INTO attempts (id, student_id, assignment_id, session_id, created_at)
                        VALUES (?, ?, ?, ?, ?)
                        """,
                attempt.id(),
                attempt.studentId(),
                attempt.assignmentId(),
                attempt.sessionId(),
                Timestamp.from(attempt.createdAt())
        );
    }

    @Override
    public boolean existsAttempt(String studentId, String assignmentId, String sessionId) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM attempts
                        WHERE student_id = ?
                          AND assignment_id = ?
                          AND session_id = ?
                        """,
                Integer.class,
                studentId,
                assignmentId,
                sessionId
        );
        return count != null && count > 0;
    }

    @Override
    public List<AttemptRow> listAttemptsForClassroom(String classroomId) {
        return listAttemptsForClassroom(classroomId, null, null);
    }

    @Override
    public List<AttemptRow> listAttemptsForClassroom(String classroomId, Instant fromInclusive, Instant toExclusive) {
        boolean filtered = fromInclusive != null && toExclusive != null;
        return jdbcTemplate.query(
                filtered
                        ? """
                            SELECT at.id, at.student_id, at.assignment_id, at.session_id, at.created_at
                            FROM attempts at
                            INNER JOIN students st ON st.id = at.student_id
                            INNER JOIN assignments ag ON ag.id = at.assignment_id
                            WHERE st.classroom_id = ?
                              AND ag.classroom_id = ?
                              AND at.created_at >= ?
                              AND at.created_at < ?
                            ORDER BY at.created_at
                          """
                        : """
                            SELECT at.id, at.student_id, at.assignment_id, at.session_id, at.created_at
                            FROM attempts at
                            INNER JOIN students st ON st.id = at.student_id
                            INNER JOIN assignments ag ON ag.id = at.assignment_id
                            WHERE st.classroom_id = ?
                              AND ag.classroom_id = ?
                            ORDER BY at.created_at
                          """,
                (rs, rowNum) -> new AttemptRow(
                        rs.getString("id"),
                        rs.getString("student_id"),
                        rs.getString("assignment_id"),
                        rs.getString("session_id"),
                        rs.getTimestamp("created_at").toInstant()
                ),
                filtered
                        ? new Object[] {classroomId, classroomId, Timestamp.from(fromInclusive), Timestamp.from(toExclusive)}
                        : new Object[] {classroomId, classroomId}
        );
    }

    @Override
    public boolean hasAnyClassroom() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM classrooms", Integer.class);
        return count != null && count > 0;
    }
}

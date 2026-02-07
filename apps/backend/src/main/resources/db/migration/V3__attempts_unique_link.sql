CREATE UNIQUE INDEX IF NOT EXISTS uq_attempts_student_assignment_session
    ON attempts (student_id, assignment_id, session_id);

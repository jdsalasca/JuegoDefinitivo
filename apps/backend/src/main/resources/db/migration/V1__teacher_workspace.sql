CREATE TABLE IF NOT EXISTS classrooms (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    teacher_name VARCHAR(160) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS students (
    id VARCHAR(32) PRIMARY KEY,
    classroom_id VARCHAR(32) NOT NULL,
    name VARCHAR(160) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_students_classroom FOREIGN KEY (classroom_id) REFERENCES classrooms (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS assignments (
    id VARCHAR(32) PRIMARY KEY,
    classroom_id VARCHAR(32) NOT NULL,
    title VARCHAR(220) NOT NULL,
    book_path VARCHAR(1500) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_assignments_classroom FOREIGN KEY (classroom_id) REFERENCES classrooms (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS attempts (
    id VARCHAR(32) PRIMARY KEY,
    student_id VARCHAR(32) NOT NULL,
    assignment_id VARCHAR(32) NOT NULL,
    session_id VARCHAR(80) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_attempts_student FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE,
    CONSTRAINT fk_attempts_assignment FOREIGN KEY (assignment_id) REFERENCES assignments (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_students_classroom ON students (classroom_id);
CREATE INDEX IF NOT EXISTS idx_assignments_classroom ON assignments (classroom_id);
CREATE INDEX IF NOT EXISTS idx_attempts_student ON attempts (student_id);
CREATE INDEX IF NOT EXISTS idx_attempts_assignment ON attempts (assignment_id);
CREATE INDEX IF NOT EXISTS idx_attempts_session ON attempts (session_id);

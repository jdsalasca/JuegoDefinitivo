CREATE TABLE IF NOT EXISTS game_sessions (
    session_id VARCHAR(80) PRIMARY KEY,
    player_name VARCHAR(160) NOT NULL,
    book_path VARCHAR(1500) NOT NULL,
    book_title VARCHAR(500) NOT NULL,
    current_scene INT NOT NULL,
    life INT NOT NULL,
    knowledge INT NOT NULL,
    courage INT NOT NULL,
    focus INT NOT NULL,
    score INT NOT NULL,
    correct_answers INT NOT NULL,
    discoveries INT NOT NULL,
    completed BOOLEAN NOT NULL,
    inventory_json CLOB NOT NULL,
    narrative_memory_json CLOB NOT NULL,
    challenge_attempts INT NOT NULL,
    challenge_correct INT NOT NULL,
    last_message VARCHAR(2000) NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_game_sessions_updated_at ON game_sessions (updated_at);

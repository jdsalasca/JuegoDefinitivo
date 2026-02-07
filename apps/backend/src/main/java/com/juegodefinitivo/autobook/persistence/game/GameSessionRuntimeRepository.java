package com.juegodefinitivo.autobook.persistence.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juegodefinitivo.autobook.domain.GameSession;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class GameSessionRuntimeRepository {

    private static final TypeReference<LinkedHashMap<String, Integer>> MAP_TYPE = new TypeReference<>() {
    };

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public GameSessionRuntimeRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public void save(String sessionId, GameSession session, Map<String, Integer> narrativeMemory, int challengeAttempts, int challengeCorrect, String lastMessage) {
        String inventoryJson = toJson(session.getInventory());
        String memoryJson = toJson(narrativeMemory);
        Instant now = Instant.now();

        int updated = jdbcTemplate.update(
                """
                        UPDATE game_sessions
                        SET player_name = ?,
                            book_path = ?,
                            book_title = ?,
                            current_scene = ?,
                            life = ?,
                            knowledge = ?,
                            courage = ?,
                            focus = ?,
                            score = ?,
                            correct_answers = ?,
                            discoveries = ?,
                            completed = ?,
                            inventory_json = ?,
                            narrative_memory_json = ?,
                            challenge_attempts = ?,
                            challenge_correct = ?,
                            last_message = ?,
                            updated_at = ?
                        WHERE session_id = ?
                        """,
                session.getPlayerName(),
                session.getBookPath(),
                session.getBookTitle(),
                session.getCurrentScene(),
                session.getLife(),
                session.getKnowledge(),
                session.getCourage(),
                session.getFocus(),
                session.getScore(),
                session.getCorrectAnswers(),
                session.getDiscoveries(),
                session.isCompleted(),
                inventoryJson,
                memoryJson,
                challengeAttempts,
                challengeCorrect,
                safeMessage(lastMessage),
                Timestamp.from(now),
                sessionId
        );

        if (updated == 0) {
            jdbcTemplate.update(
                    """
                            INSERT INTO game_sessions (
                                session_id,
                                player_name,
                                book_path,
                                book_title,
                                current_scene,
                                life,
                                knowledge,
                                courage,
                                focus,
                                score,
                                correct_answers,
                                discoveries,
                                completed,
                                inventory_json,
                                narrative_memory_json,
                                challenge_attempts,
                                challenge_correct,
                                last_message,
                                updated_at
                            )
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                            """,
                    sessionId,
                    session.getPlayerName(),
                    session.getBookPath(),
                    session.getBookTitle(),
                    session.getCurrentScene(),
                    session.getLife(),
                    session.getKnowledge(),
                    session.getCourage(),
                    session.getFocus(),
                    session.getScore(),
                    session.getCorrectAnswers(),
                    session.getDiscoveries(),
                    session.isCompleted(),
                    inventoryJson,
                    memoryJson,
                    challengeAttempts,
                    challengeCorrect,
                    safeMessage(lastMessage),
                    Timestamp.from(now)
            );
        }
    }

    public Optional<StoredSession> load(String sessionId) {
        List<StoredSession> rows = jdbcTemplate.query(
                """
                        SELECT session_id, player_name, book_path, book_title, current_scene,
                               life, knowledge, courage, focus, score, correct_answers,
                               discoveries, completed, inventory_json, narrative_memory_json,
                               challenge_attempts, challenge_correct, last_message, updated_at
                        FROM game_sessions
                        WHERE session_id = ?
                        """,
                (rs, rowNum) -> {
                    GameSession session = new GameSession();
                    session.setPlayerName(rs.getString("player_name"));
                    session.setBookPath(rs.getString("book_path"));
                    session.setBookTitle(rs.getString("book_title"));
                    session.setCurrentScene(rs.getInt("current_scene"));
                    session.setLife(rs.getInt("life"));
                    session.setKnowledge(rs.getInt("knowledge"));
                    session.setCourage(rs.getInt("courage"));
                    session.setFocus(rs.getInt("focus"));
                    session.setScore(rs.getInt("score"));
                    session.setCorrectAnswers(rs.getInt("correct_answers"));
                    session.setDiscoveries(rs.getInt("discoveries"));
                    session.setCompleted(rs.getBoolean("completed"));
                    session.replaceInventory(fromJson(rs.getString("inventory_json")));
                    return new StoredSession(
                            rs.getString("session_id"),
                            session,
                            fromJson(rs.getString("narrative_memory_json")),
                            rs.getInt("challenge_attempts"),
                            rs.getInt("challenge_correct"),
                            rs.getString("last_message"),
                            rs.getTimestamp("updated_at").toInstant()
                    );
                },
                sessionId
        );
        return rows.stream().findFirst();
    }

    public Map<String, Instant> findLastUpdatedAt(List<String> sessionIds) {
        if (sessionIds == null || sessionIds.isEmpty()) {
            return Map.of();
        }
        String placeholders = sessionIds.stream().map(id -> "?").collect(Collectors.joining(", "));
        String sql = "SELECT session_id, updated_at FROM game_sessions WHERE session_id IN (" + placeholders + ")";
        return jdbcTemplate.query(
                sql,
                (rs) -> {
                    Map<String, Instant> bySession = new LinkedHashMap<>();
                    while (rs.next()) {
                        bySession.put(rs.getString("session_id"), rs.getTimestamp("updated_at").toInstant());
                    }
                    return bySession;
                },
                sessionIds.toArray()
        );
    }

    private String toJson(Map<String, Integer> value) {
        try {
            return objectMapper.writeValueAsString(value == null ? Map.of() : value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("No se pudo serializar sesion runtime.", ex);
        }
    }

    private Map<String, Integer> fromJson(String value) {
        try {
            if (value == null || value.isBlank()) {
                return new LinkedHashMap<>();
            }
            return objectMapper.readValue(value, MAP_TYPE);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("No se pudo deserializar sesion runtime.", ex);
        }
    }

    private String safeMessage(String value) {
        if (value == null || value.isBlank()) {
            return "Sesion restaurada.";
        }
        return value.length() > 1990 ? value.substring(0, 1990) : value;
    }

    public record StoredSession(
            String sessionId,
            GameSession session,
            Map<String, Integer> narrativeMemory,
            int challengeAttempts,
            int challengeCorrect,
            String lastMessage,
            Instant updatedAt
    ) {
    }
}

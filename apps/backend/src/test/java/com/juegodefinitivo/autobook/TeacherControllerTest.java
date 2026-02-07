package com.juegodefinitivo.autobook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juegodefinitivo.autobook.api.dto.StartGameRequest;
import com.juegodefinitivo.autobook.app.AutoBookApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AutoBookApplication.class)
@AutoConfigureMockMvc
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateTeacherWorkspaceAndProduceDashboard() throws Exception {
        String booksJson = mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String bookPath = objectMapper.readTree(booksJson).get(0).get("path").asText();

        String gameJson = mockMvc.perform(post("/api/game/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new StartGameRequest("Alumno Demo", bookPath))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String sessionId = objectMapper.readTree(gameJson).get("sessionId").asText();

        String classroomJson = mockMvc.perform(post("/api/teacher/classrooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Aula Test", "teacherName", "Profe Ana"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String classroomId = objectMapper.readTree(classroomJson).get("id").asText();

        String studentJson = mockMvc.perform(post("/api/teacher/classrooms/" + classroomId + "/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Estudiante 1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String studentId = objectMapper.readTree(studentJson).get("id").asText();

        String assignmentJson = mockMvc.perform(post("/api/teacher/classrooms/" + classroomId + "/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "Lectura 1", "bookPath", bookPath))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode assignmentNode = objectMapper.readTree(assignmentJson);
        String assignmentId = assignmentNode.get("id").asText();

        mockMvc.perform(post("/api/teacher/attempts/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "studentId", studentId,
                                "assignmentId", assignmentId,
                                "sessionId", sessionId
                        ))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/teacher/attempts/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "studentId", studentId,
                                "assignmentId", assignmentId,
                                "sessionId", sessionId
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ese intento ya esta vinculado para este estudiante y asignacion."));

        mockMvc.perform(get("/api/teacher/classrooms/" + classroomId + "/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.classroomId").value(classroomId))
                .andExpect(jsonPath("$.studentProgress[0].studentId").value(studentId));

        mockMvc.perform(get("/api/teacher/classrooms/" + classroomId + "/report.csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("student_id")));
    }
}


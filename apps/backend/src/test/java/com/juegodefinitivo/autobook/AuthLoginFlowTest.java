package com.juegodefinitivo.autobook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juegodefinitivo.autobook.app.AutoBookApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AutoBookApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "app.security.enabled=true",
        "app.security.student-token=legacy-student-token",
        "app.security.teacher-token=legacy-teacher-token",
        "app.security.admin-token=legacy-admin-token",
        "app.security.student-username=student-user",
        "app.security.student-password=student-pass",
        "app.security.teacher-username=teacher-user",
        "app.security.teacher-password=teacher-pass",
        "app.security.admin-username=admin-user",
        "app.security.admin-password=admin-pass",
        "app.security.jwt-secret=test-secret-value-for-jwt-signature",
        "app.security.jwt-ttl-seconds=3600",
        "app.rate-limit.enabled=false"
})
class AuthLoginFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldLoginTeacherAndAccessTeacherEndpointWithBearer() throws Exception {
        String loginJson = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "teacher-user",
                                "password", "teacher-pass"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("TEACHER"))
                .andExpect(jsonPath("$.accessToken").isString())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(loginJson);
        String bearer = node.get("accessToken").asText();

        mockMvc.perform(get("/api/teacher/classrooms")
                        .header("Authorization", "Bearer " + bearer))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRestrictStudentBearerFromTeacherEndpoint() throws Exception {
        String loginJson = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "student-user",
                                "password", "student-pass"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String bearer = objectMapper.readTree(loginJson).get("accessToken").asText();

        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + bearer))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/teacher/classrooms")
                        .header("Authorization", "Bearer " + bearer))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectInvalidLoginAndInvalidBearer() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "teacher-user",
                                "password", "wrong-pass"
                        ))))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isUnauthorized());
    }
}

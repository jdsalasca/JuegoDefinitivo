package com.juegodefinitivo.autobook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juegodefinitivo.autobook.api.dto.TelemetryEventRequest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AutoBookApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "app.security.enabled=true",
        "app.security.student-token=test-student-token",
        "app.security.teacher-token=test-teacher-token",
        "app.security.admin-token=test-admin-token",
        "app.rate-limit.enabled=true",
        "app.rate-limit.window-seconds=600",
        "app.rate-limit.max-requests=1",
        "app.rate-limit.teacher-max-requests=2"
})
class ApiSecurityAndRateLimitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRejectMissingToken() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowStudentTokenForGameEndpoints() throws Exception {
        mockMvc.perform(get("/api/books")
                        .header("X-Api-Token", "test-student-token"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectStudentTokenForTeacherEndpoint() throws Exception {
        mockMvc.perform(get("/api/teacher/classrooms")
                        .header("X-Api-Token", "test-student-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowTeacherTokenForTeacherEndpoint() throws Exception {
        mockMvc.perform(get("/api/teacher/classrooms")
                        .header("X-Api-Token", "test-teacher-token"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldApplyRateLimitForWriteRequests() throws Exception {
        String payload = objectMapper.writeValueAsString(new TelemetryEventRequest(
                "session-rate",
                "action_executed",
                "play",
                100L,
                Map.of("source", "rate-test")
        ));

        mockMvc.perform(post("/api/telemetry/events")
                        .header("X-Api-Token", "test-admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isAccepted());

        mockMvc.perform(post("/api/telemetry/events")
                        .header("X-Api-Token", "test-admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isTooManyRequests());
    }
}

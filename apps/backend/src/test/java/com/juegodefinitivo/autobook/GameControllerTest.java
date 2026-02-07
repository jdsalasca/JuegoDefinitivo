package com.juegodefinitivo.autobook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juegodefinitivo.autobook.app.AutoBookApplication;
import com.juegodefinitivo.autobook.api.dto.ActionRequest;
import com.juegodefinitivo.autobook.api.dto.AutoplayRequest;
import com.juegodefinitivo.autobook.api.dto.StartGameRequest;
import com.juegodefinitivo.autobook.api.dto.TelemetryEventRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AutoBookApplication.class)
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListBooksAndStartGameFlow() throws Exception {
        String booksJson = mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].path").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String firstPath = objectMapper.readTree(booksJson).get(0).get("path").asText();

        String startResponse = mockMvc.perform(post("/api/game/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new StartGameRequest("Juan", firstPath))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.currentScene.title").exists())
                .andExpect(jsonPath("$.adaptiveDifficulty").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String sessionId = objectMapper.readTree(startResponse).get("sessionId").asText();

        mockMvc.perform(post("/api/game/" + sessionId + "/action")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ActionRequest("TALK", null, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastMessage").exists())
                .andExpect(jsonPath("$.score").isNumber())
                .andExpect(jsonPath("$.adaptiveDifficulty").exists());

        mockMvc.perform(get("/api/game/" + sessionId + "/graph"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(sessionId))
                .andExpect(jsonPath("$.nodes").exists());

        mockMvc.perform(post("/api/game/" + sessionId + "/autoplay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AutoplayRequest("9-12", "intermediate", 2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastMessage").exists());

        mockMvc.perform(post("/api/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TelemetryEventRequest(
                                sessionId,
                                "action_executed",
                                "play",
                                240L,
                                java.util.Map.of("mode", "manual")
                        ))))
                .andExpect(status().isAccepted());

        mockMvc.perform(get("/api/telemetry/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").isNumber())
                .andExpect(jsonPath("$.byEvent.action_executed").exists());
    }

    @Test
    void shouldRejectTelemetryWhenElapsedIsNegative() throws Exception {
        mockMvc.perform(post("/api/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TelemetryEventRequest(
                                "session-invalid",
                                "action_executed",
                                "play",
                                -5L,
                                java.util.Map.of()
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("elapsedMs no puede ser negativo."));
    }

    @Test
    void shouldNormalizeTelemetryEventAndStageNames() throws Exception {
        String eventName = " normalize_case_event ";
        String stageName = " normalize_stage ";

        mockMvc.perform(post("/api/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TelemetryEventRequest(
                                "session-normalized",
                                eventName,
                                stageName,
                                100L,
                                java.util.Map.of()
                        ))))
                .andExpect(status().isAccepted());

        mockMvc.perform(post("/api/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TelemetryEventRequest(
                                "session-normalized",
                                "normalize_case_event",
                                "normalize_stage",
                                null,
                                java.util.Map.of()
                        ))))
                .andExpect(status().isAccepted());

        mockMvc.perform(get("/api/telemetry/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.byEvent.normalize_case_event").value(2))
                .andExpect(jsonPath("$.byStage.normalize_stage").value(2));
    }
}

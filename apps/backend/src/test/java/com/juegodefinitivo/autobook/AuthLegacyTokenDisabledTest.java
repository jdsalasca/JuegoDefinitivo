package com.juegodefinitivo.autobook;

import com.juegodefinitivo.autobook.app.AutoBookApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AutoBookApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "app.security.enabled=true",
        "app.security.allow-legacy-token=false",
        "app.security.student-token=legacy-student-token",
        "app.security.teacher-token=legacy-teacher-token",
        "app.security.admin-token=legacy-admin-token",
        "app.security.jwt-secret=test-secret-value-for-jwt-signature",
        "app.rate-limit.enabled=false"
})
class AuthLegacyTokenDisabledTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRejectLegacyHeaderWhenDisabled() throws Exception {
        mockMvc.perform(get("/api/books")
                        .header("X-Api-Token", "legacy-student-token"))
                .andExpect(status().isUnauthorized());
    }
}

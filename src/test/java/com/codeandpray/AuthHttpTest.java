package com.codeandpray;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:auth;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "jwt.secret=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
        "jwt.expiration-ms=60000"
})
@AutoConfigureMockMvc
class AuthHttpTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void registrationLoginAndDuplicateEmailHaveExpectedStatus()
            throws Exception {

        String request = """
                {
                  "email": "tester@example.com",
                  "password": "strong-password"
                }
                """;

        mvc.perform(
                        post("/api/auth/register")
                                .contentType("application/json")
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ATHLETE"));

        mvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());

        mvc.perform(
                        post("/api/auth/register")
                                .contentType("application/json")
                                .content(request)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void malformedJsonAndInvalidParametersAreClientErrors()
            throws Exception {

        mvc.perform(
                        post("/api/auth/register")
                                .contentType("application/json")
                                .content(new byte[]{(byte) '{'})
                )
                .andExpect(status().isBadRequest());

        mvc.perform(
                        get("/api/competitions")
                                .param("page", "-1")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void participantListAndManagementRequireAuthentication()
            throws Exception {

        mvc.perform(
                        get("/api/competitions/1/registrations")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

        mvc.perform(
                        get("/api/results/1/management")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void brokenTokenIsRejectedWithoutInternalDetails()
            throws Exception {

        mvc.perform(
                        get("/api/ratings")
                                .header("Authorization", "Bearer broken")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }
}
package rs.ac.ftn.isa.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CommentRateLimitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String loginAndGetToken() throws Exception {
        String body = """
            {
              "email": "user@example.com",
              "password": "123"
            }
        """;

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);

        if (node.has("accessToken")) return node.get("accessToken").asText();
        if (node.has("token")) return node.get("token").asText();

        throw new IllegalStateException("Token field not found in response: " + response);
    }

    @Test
    void shouldAllow60CommentsThenBlock61st() throws Exception {
        String token = loginAndGetToken();

        // GET komentara radi i bez tokena (public)
        mockMvc.perform(get("/api/videos/1/comments?page=0&size=10"))
                .andExpect(status().isOk());

        for (int i = 0; i < 60; i++) {
            String commentBody = "{ \"text\": \"Comment #" + i + "\" }";

            mockMvc.perform(post("/api/videos/1/comments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .content(commentBody))
                    .andExpect(status().isOk());
        }

        // 61. komentar mora biti 429
        mockMvc.perform(post("/api/videos/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("{ \"text\": \"Comment #61\" }"))
                .andExpect(status().isTooManyRequests());
    }
}
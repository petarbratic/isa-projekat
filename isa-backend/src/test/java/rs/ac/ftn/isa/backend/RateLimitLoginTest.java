package rs.ac.ftn.isa.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class RateLimitLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldBlockAfterFiveAttemptsPerMinuteForSameIp() throws Exception {

        String body = """
            {
              "email": "nepostoji@test.com",
              "password": "pogresno"
            }
        """;

        // Prvih 5 pokušaja → auth greška (401 ili 403), ali NE 429
        for (int i = 0; i < 5; i++) {
            System.out.println(i + ". *********************************************************************************************************************************************************************************");
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Forwarded-For", "1.2.3.4")
                            .content(body))
                    .andExpect(status().is4xxClientError());
        }
        System.out.println("6.**************************************************************************************************************************************************************************************");
        // 6. pokušaj → rate limit
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Forwarded-For", "1.2.3.4")
                        .content(body))
                .andDo(print())
                .andExpect(status().isTooManyRequests());
    }
}
package hr.algebra.insurancebackend;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAccessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenAccessProtectedRoutesWithoutAuth_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/driver"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/report"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/driver/1"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/report/rejected"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAccessProtectedRoutesWithAuth_thenOk() throws Exception {
        // Obtain the token by logging in
        JSONObject loginResponse = Utils.loginOkWithUser(mockMvc, Utils.correctUser());
        String accessToken = loginResponse.getString("accessToken");

        mockMvc.perform(get("/driver")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/report")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/driver/2")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/report")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }
}


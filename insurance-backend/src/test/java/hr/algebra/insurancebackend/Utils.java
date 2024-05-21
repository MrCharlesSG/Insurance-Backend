package hr.algebra.insurancebackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.insurancebackend.security.dto.AuthRequestDTO;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class Utils {
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject loginOkWithUser(MockMvc mvc, AuthRequestDTO authRequestDTO) throws Exception {
        return loginOk(mvc, asJsonString(authRequestDTO));
    }

    public static JSONObject loginOk(MockMvc mvc, String request) throws Exception {
        MvcResult result = mvc.perform(post("/auth/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        return new JSONObject(content);
    }

    public static JSONObject loginKO(MockMvc mvc, String request) throws Exception {
        MvcResult result = mvc.perform(post("/auth/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden())
                .andReturn();
        return new JSONObject();
    }

    public static AuthRequestDTO correctUser(){
        return AuthRequestDTO
                .builder()
                .password("user")
                .username("1234ABC")
                .build();
    }
    public static AuthRequestDTO correctUserSecond(){
        return AuthRequestDTO
                .builder()
                .password("user")
                .username("5678DEF")
                .build();
    }


    public static AuthRequestDTO correctUserNotOwner(){
        return AuthRequestDTO
                .builder()
                .password("user")
                .username("juan")
                .build();
    }

    public static String getStringOfJsonPartFromFile(String part, String filePath) throws IOException, JSONException {
        InputStream inputStream = Utils.class.getResourceAsStream(filePath);
        if (inputStream != null) {
            try (java.util.Scanner scanner = new java.util.Scanner(inputStream, StandardCharsets.UTF_8.name())) {
                scanner.useDelimiter("\\A");
                String json = scanner.hasNext() ? scanner.next() : "";

                JSONObject jsonObject = new JSONObject(json);
                return jsonObject.getJSONObject(part).toString();
            }
        }
        throw new IOException("File not found: " + filePath);
    }

    public static String getStringOfJsonFile(String filePath) throws IOException {
        InputStream inputStream = Utils.class.getResourceAsStream(filePath);
        if (inputStream != null) {
            try (java.util.Scanner scanner = new java.util.Scanner(inputStream, StandardCharsets.UTF_8.name())) {
                scanner.useDelimiter("\\A");
                return scanner.hasNext() ? scanner.next() : "";
            }
        }
        throw new IOException("File not found: " + filePath);
    }

}

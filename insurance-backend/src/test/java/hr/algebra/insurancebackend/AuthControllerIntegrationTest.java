package hr.algebra.insurancebackend;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static hr.algebra.insurancebackend.Utils.*;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations =  "classpath:application-integrationtest.properties"
)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;


    @Test
    public void givenValidLoginWithVehicle_whenAuthAndGetToken_thenStatusOK() throws Exception{



        JSONObject login = Utils.loginOk(
                mvc,
                getStringOfJsonFile("/json/auth/login-vehicle.json")
        );
        assertNotNull(login.getString("accessToken"));
        assertNotNull(login.getString("token"));

    }

    @Test
    public void givenInvalidLoginWithVehicle_whenAuthAndGetToken_thenStatusKO() throws Exception{
        JSONObject login = Utils.loginKO(
                mvc,
                getStringOfJsonFile("/json/auth/bad-login-vehicle.json")
        );
        String string = login.toString();
        assert(string.equals("{}"));
    }

    @Test
    public void refreshTokenAfterLoginCorrectlyWithRightToken_thenResponseOK() throws Exception{

        JSONObject jsonObject = loginOkWithUser(mvc, correctUser());

        mvc.perform(post("/auth/api/v1/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(jsonObject.getString("token"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    public void refreshTokenAfterLoginCorrectlyWithWrongToken_thenResponseOK() throws Exception{
        /*
        AuthRequestDTO authRequestDTO = AuthRequestDTO
                .builder()
                .password("user")
                .username("ana@user.com")
                .build();
        JSONObject jsonObject = loginOk(authRequestDTO);

        mvc.perform(post("/auth/api/v1/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(jsonObject.getString("token")+"k")))
                .andExpect(result -> {
                    if (result.getResolvedException() instanceof RuntimeException) {
                        throw result.getResolvedException();
                    }
                });
         */
    }


    @Test
    public void registrationAndGetToken_thenStatusOK() throws Exception {
        mvc.perform(post("/auth/api/v1/register/vehicle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getStringOfJsonFile("/json/auth/requister-vehicle.json")))
                .andDo(print())
                .andExpect(status().isOk());
    }


}


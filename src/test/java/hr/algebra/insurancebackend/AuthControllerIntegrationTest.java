package hr.algebra.insurancebackend;

import hr.algebra.insurancebackend.security.dto.AuthRequestDTO;
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
import org.springframework.transaction.annotation.Transactional;

import static hr.algebra.insurancebackend.Utils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations =  "classpath:application-integrationtest.properties"
)
@Transactional
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
    public void givenIncorrectLoginWithVehicle_whenAuthAndGetToken_thenStatusKO() throws Exception{
        JSONObject login = Utils.loginKO(
                mvc,
                getStringOfJsonFile("/json/auth/bad-login-request-vehicle.json")
        );
        String string = login.toString();
        assertEquals("{}",string );
    }
    @Test
    public void givenInvalidLoginWithVehicle_whenAuthAndGetToken_thenStatusKO() throws Exception{
        mvc.perform(post("/auth/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getStringOfJsonFile("/json/auth/invalid-login-request.json")))
                .andExpect(status().isBadRequest());
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
        JSONObject jsonObject = loginOkWithUser(mvc, correctUser());

        mvc.perform(post("/auth/api/v1/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(jsonObject.getString("token")+"k")))
                .andExpect(status().isBadRequest());

    }



    @Test
    public void registrationAndGetToken_thenStatusOK() throws Exception {
        mvc.perform(post("/auth/api/v1/register/vehicle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getStringOfJsonFile("/json/auth/requister-vehicle.json")))
                .andDo(print())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(status().isOk());
    }
    @Test
    public void conflictRegistrationAndGetToken_thenStatusBadRequest() throws Exception {
        mvc.perform(post("/auth/api/v1/register/vehicle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getStringOfJsonFile("/json/auth/conflict-requister-vehicle.json")))
                .andDo(print())
                .andExpect(status().isConflict());
    }
    @Test
    public void invalidRegistrationAndGetToken_thenStatusBadRequest() throws Exception {
        mvc.perform(post("/auth/api/v1/register/vehicle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getStringOfJsonFile("/json/auth/invalid-requister-vehicle.json")))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void loginOkLogoutAndTryAccessResource_thenStatusForbidden() throws Exception{
        JSONObject jsonObject = loginOkWithUser(mvc, correctUser());

        mvc.perform(post("/auth/api/v1/logout")
                        .header("Authorization", "Bearer " + jsonObject.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(delete("/report/reject/3")
                        .header("Authorization", "Bearer " + jsonObject.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getStringOfJsonFile("/json/report/close-report.json"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void loginOkLogoutAndTryAccessResourceThenStatusForbiddenAndLoginAgainAndAccessResource_thenStatusOK() throws Exception{
       JSONObject jsonObject = loginOkWithUser(mvc, correctUser());

        mvc.perform(post("/auth/api/v1/logout")
                        .header("Authorization", "Bearer " + jsonObject.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get("/driver/byEmail")
                        .header("Authorization", "Bearer " + jsonObject.getString("accessToken"))
                        .param("email", "ger.pa@example.com")
                )
                .andExpect(status().isForbidden())
                .andDo(print());

        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(get("/driver/byEmail")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .param("email", "laura.ramirez@example.com")
                )
                .andExpect(status().isOk())
                .andDo(print());


    }

}


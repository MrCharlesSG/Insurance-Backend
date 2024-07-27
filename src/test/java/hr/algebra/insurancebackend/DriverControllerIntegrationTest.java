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
import org.springframework.transaction.annotation.Transactional;

import static hr.algebra.insurancebackend.Utils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations =  "classpath:application-integrationtest.properties"
)
@Transactional
public class DriverControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;


    @Test
    public void loginCorrectlyAndCreateDriverCorrectly_thenStatusOK() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());
        MvcResult result = mvc.perform(post("/driver")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getStringOfJsonFile("/json/driver/correct_driver.json"))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONObject jsonResponse = new JSONObject(content);
        Long driverId = jsonResponse.getLong("id");

        mvc.perform(get("/driver/{id}", driverId)
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void getAllDriversOfAuthenticatedVehicle_thenStatusOK() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());
        mvc.perform(get("/driver")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andDo(print());
    }

    @Test
    public void tryCreatingDriversWithSameEmail_thenStatusForbidden() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(post("/driver")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getStringOfJsonFile("/json/driver/correct_driver.json"))
                )
                .andExpect(status().isOk());

        mvc.perform(post("/driver")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getStringOfJsonFile("/json/driver/correct_driver.json"))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void associateDriverViaEmail_thenStatusOK() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(get("/driver")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andDo(print());

        // Crea el cuerpo de la solicitud con el correo electrónico válido
        String emailRequestJson = "{\"email\":\"vic.mar@example.com\"}";

        mvc.perform(post("/driver/associate")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailRequestJson)
                )
                .andExpect(status().isOk());

        mvc.perform(get("/driver")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andDo(print());
    }


    @Test
    public void disassociateDriverViaEmail_thenStatusOK() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(get("/driver")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andDo(print());

        // Crea el cuerpo de la solicitud con el correo electrónico válido
        String emailRequestJson = "{\"email\":\"ger.pa@example.com\"}";

        mvc.perform(delete("/driver/disassociate")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailRequestJson)
                )
                .andExpect(status().isOk());

        mvc.perform(get("/driver")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andDo(print());
    }

    @Test
    public void getDriversByVehiclePlate_thenStatusOK() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(get("/driver/byVehicle?plate=1234ABC")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andDo(print());
    }

    @Test
    public void getDriversByVehiclePlate_IncorrectPlate_thenNotFound() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(get("/driver/byVehicle?plate=123ABC")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void getDriverByEmail_thenStatusOK() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(get("/driver/byEmail")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .param("email", "ger.pa@example.com")
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getDriverByEmail_notFound_thenStatusNotFound() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(get("/driver/byEmail")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .param("email", "nonexistent@example.com")
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }
    @Test
    public void getDriverByEmail_IncorrectFormatEmail_thenBadRequest() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(get("/driver/byEmail")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .param("email", "nonexistent.com")
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void createDriver_withInvalidData_thenStatusBadRequest() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(post("/driver")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getStringOfJsonFile("/json/driver/invalid_driver.json"))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void associateDriver_withInvalidEmail_thenStatusBadRequest() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());
        String emailRequestJson = "{\"email\":\"invalid-email\"}";

        mvc.perform(post("/driver/associate")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailRequestJson)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }


    @Test
    public void disassociateDriver_withInvalidEmail_thenStatusBadRequest() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());

        String emailRequestJson = "{\"email\":\"invalid-email\"}";

        mvc.perform(delete("/driver/disassociate")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailRequestJson)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

}
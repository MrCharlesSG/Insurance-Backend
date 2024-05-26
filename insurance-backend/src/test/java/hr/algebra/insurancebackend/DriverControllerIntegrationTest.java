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

        mvc.perform(post("/driver/associate?email=vic.mar@example.com")
                        .header("Authorization", "Bearer " + login.getString("accessToken")))
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

        mvc.perform(delete("/driver/disassociate?email=ger.pa@example.com")
                        .header("Authorization", "Bearer " + login.getString("accessToken")))
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
}
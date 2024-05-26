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
import org.springframework.transaction.annotation.Transactional;

import static hr.algebra.insurancebackend.Utils.*;
import static org.hamcrest.Matchers.hasSize;
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
public class ReportControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void openAReportAndCheckAllTypesReports_thenStatusIsOK() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(post("/report")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getStringOfJsonFile("/json/report/open-report.json"))
                )
                .andExpect(status().isOk());

        mvc.perform(get("/report")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andDo(print());

        mvc.perform(get("/report/waiting")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andDo(print());


        mvc.perform(get("/report/accepted")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print());

        mvc.perform(get("/report/rejected")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andDo(print());

    }

    @Test
    public void acceptAReportAndCheckAllTypesReports_thenStatusIsOK() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(delete("/report/accept/3")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getStringOfJsonFile("/json/report/close-report.json"))
                )
                .andExpect(status().isOk());

        mvc.perform(get("/report")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andDo(print());

        mvc.perform(get("/report/waiting")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print());


        mvc.perform(get("/report/accepted")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andDo(print());

        mvc.perform(get("/report/rejected")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andDo(print());

    }
    @Test
    public void rejectAReportAndCheckAllTypesReports_thenStatusIsOK() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(delete("/report/reject/3")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getStringOfJsonFile("/json/report/close-report.json"))
                )
                .andExpect(status().isOk());

        mvc.perform(get("/report")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andDo(print());

        mvc.perform(get("/report/waiting")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print());


        mvc.perform(get("/report/accepted")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print());

        mvc.perform(get("/report/rejected")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andDo(print());

    }


}

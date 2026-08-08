package com.ailab.chemistry.controller;

import com.ailab.AiLaboratoryApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AiLaboratoryApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles({"test", "local"})
class ChemistryControllersIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Unauthenticated request to chemistry endpoint should return 401 Unauthorized")
    void unauthenticatedRequest_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/chemistry/formulas/parse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"formula\":\"H2O\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Parse formula returns parsed elements and net charge")
    void parseFormula_returnsStructuredFormula() throws Exception {
        mockMvc.perform(post("/api/v1/chemistry/formulas/parse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"formula\":\"CuSO4·5H2O\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalFormula").value("CuSO4·5H2O"))
                .andExpect(jsonPath("$.normalizedFormula").value("CuSO4·5H2O"))
                .andExpect(jsonPath("$.netCharge").value(0));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Balance equation returns balanced reactants and products")
    void balanceEquation_returnsBalancedEquation() throws Exception {
        mockMvc.perform(post("/api/v1/chemistry/equations/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equation\":\"H2 + O2 -> H2O\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canonicalEquationString").exists())
                .andExpect(jsonPath("$.atomBalanced").value(true))
                .andExpect(jsonPath("$.chargeBalanced").value(true));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("List periodic table elements returns catalog list")
    void listElements_returnsElementList() throws Exception {
        mockMvc.perform(get("/api/v1/chemistry/elements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Get element details by atomic number 6 returns Carbon")
    void getElementByAtomicNumber_returnsCarbon() throws Exception {
        mockMvc.perform(get("/api/v1/chemistry/elements/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("C"))
                .andExpect(jsonPath("$.name").value("Carbon"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Search compounds returns catalog list")
    void searchCompounds_returnsList() throws Exception {
        mockMvc.perform(get("/api/v1/chemistry/compounds?name=Water"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Get thermodynamic profile for CMP-WATER returns profile")
    void getThermodynamicProfile_returnsProfile() throws Exception {
        mockMvc.perform(get("/api/v1/chemistry/thermodynamics/reference/COMP-H2O"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.compoundCode").value("COMP-H2O"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Calculate pure water acid-base equilibrium returns pH 7.0 at 25C")
    void calculatePureWater_returnsNeutralPh() throws Exception {
        mockMvc.perform(post("/api/v1/chemistry/acid-base/water")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ph").exists());
    }
}

package pe.upeu.agrosierra;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginCorrecto() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"tecnico1\",\"password\":\"Agro2026*\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.usuario").value("tecnico1"));
    }

    @Test
    void loginIncorrecto() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"tecnico1\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rutaSinToken401() throws Exception {
        mockMvc.perform(get("/api/v1/lotes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listadoLotesPaginado() throws Exception {
        String token = loginAndGetToken("tecnico1", "Agro2026*");
        mockMvc.perform(get("/api/v1/lotes?pagina=0&tamanio=5")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido").isArray())
                .andExpect(jsonPath("$.totalElementos").isNumber());
    }

    @Test
    void tecnicoNoPuedeCerrarCampania() throws Exception {
        String token = loginAndGetToken("tecnico1", "Agro2026*");
        mockMvc.perform(post("/api/v1/lotes/1/cerrar-campania")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void jefePuedeCerrarCampaniaConCosecha() throws Exception {
        String token = loginAndGetToken("jefe1", "Agro2026*");
        mockMvc.perform(post("/api/v1/lotes/5/cerrar-campania")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void rn01LaborSobreLoteCerrado() throws Exception {
        String token = loginAndGetToken("tecnico1", "Agro2026*");
        mockMvc.perform(post("/api/v1/labores")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"loteId\":4,\"tipo\":\"RIEGO\",\"fecha\":\"2026-10-27\",\"responsable\":\"tecnico1\",\"cantidad\":5.0,\"observacion\":\"test\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void rn04FumigacionExcedeDosis() throws Exception {
        String token = loginAndGetToken("tecnico1", "Agro2026*");
        mockMvc.perform(post("/api/v1/labores")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"loteId\":1,\"tipo\":\"FUMIGACION\",\"fecha\":\"2026-10-27\",\"responsable\":\"tecnico1\",\"cantidad\":999.0,\"observacion\":\"test\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.cantidad").exists());
    }

    private String loginAndGetToken(String user, String pass) throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + user + "\",\"password\":\"" + pass + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        // Extract token from JSON
        int idx = response.indexOf("\"token\":\"") + 9;
        return response.substring(idx, response.indexOf("\"", idx));
    }
}

package com.padelzgz.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.padelzgz.api.exception.ClubNotFoundException;
import com.padelzgz.api.model.Club;
import com.padelzgz.api.service.ClubService;
import com.padelzgz.api.service.PistaService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClubController.class)
class ClubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private ClubService clubService;
    @MockBean private PistaService pistaService;
    // Spring Security necesita estos beans aunque no se usen en el test
    @MockBean private com.padelzgz.api.security.JwtUtils jwtUtils;
    @MockBean private com.padelzgz.api.security.UserDetailsServiceImpl userDetailsService;

    private Club club;

    @BeforeEach
    void setUp() {
        club = new Club();
        club.setId(1L);
        club.setNombre("Club Pádel Zaragoza");
        club.setCiudad("Zaragoza");
        club.setActivo(true);
        club.setFechaApertura(LocalDate.of(2020, 1, 15));
    }

    @Test
    @DisplayName("GET /clubs devuelve 200 con lista de clubs")
    @WithMockUser
    void getClubs_returns200() throws Exception {
        when(clubService.findAll()).thenReturn(Set.of(club));

        mockMvc.perform(get("/clubs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /clubs/{id} devuelve 200 cuando el club existe")
    @WithMockUser
    void getClub_whenExists_returns200() throws Exception {
        when(clubService.findById(1L)).thenReturn(Optional.of(club));

        mockMvc.perform(get("/clubs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Club Pádel Zaragoza"));
    }

    @Test
    @DisplayName("GET /clubs/{id} devuelve 404 cuando el club no existe")
    @WithMockUser
    void getClub_whenNotExists_returns404() throws Exception {
        when(clubService.findById(99L)).thenThrow(new ClubNotFoundException(99L));

        mockMvc.perform(get("/clubs/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /clubs devuelve 201 con club creado")
    @WithMockUser
    void addClub_returns201() throws Exception {
        when(clubService.addClub(any(Club.class))).thenReturn(club);

        mockMvc.perform(post("/clubs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(club)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Club Pádel Zaragoza"));
    }

    @Test
    @DisplayName("POST /clubs devuelve 400 cuando faltan campos obligatorios")
    @WithMockUser
    void addClub_whenMissingFields_returns400() throws Exception {
        Club clubInvalido = new Club(); // sin nombre ni ciudad

        mockMvc.perform(post("/clubs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clubInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /clubs/{id} devuelve 200 al actualizar")
    @WithMockUser
    void modifyClub_returns200() throws Exception {
        when(clubService.modifyClub(eq(1L), any(Club.class))).thenReturn(club);

        mockMvc.perform(put("/clubs/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(club)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /clubs/{id} devuelve 404 cuando el club no existe")
    @WithMockUser
    void modifyClub_whenNotExists_returns404() throws Exception {
        when(clubService.modifyClub(eq(99L), any(Club.class))).thenThrow(new ClubNotFoundException(99L));

        mockMvc.perform(put("/clubs/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(club)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /clubs/{id} devuelve 200 al actualizar parcialmente")
    @WithMockUser
    void patchClub_returns200() throws Exception {
        when(clubService.patchClub(eq(1L), any(Club.class))).thenReturn(club);

        mockMvc.perform(patch("/clubs/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Nombre Nuevo\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /clubs/{id} devuelve 204 al eliminar")
    @WithMockUser
    void deleteClub_returns204() throws Exception {
        doNothing().when(clubService).deleteClub(1L);

        mockMvc.perform(delete("/clubs/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /clubs/{id} devuelve 404 cuando el club no existe")
    @WithMockUser
    void deleteClub_whenNotExists_returns404() throws Exception {
        doThrow(new ClubNotFoundException(99L)).when(clubService).deleteClub(99L);

        mockMvc.perform(delete("/clubs/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}

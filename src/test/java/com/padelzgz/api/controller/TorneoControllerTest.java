package com.padelzgz.api.controller;

import com.padelzgz.api.exception.ClubNotFoundException;
import com.padelzgz.api.exception.TorneoNotFoundException;
import com.padelzgz.api.model.Club;
import com.padelzgz.api.model.Torneo;
import com.padelzgz.api.service.TorneoService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TorneoControllerTest {

    @Mock private TorneoService torneoService;
    @InjectMocks private TorneoController torneoController;

    private Torneo torneo;

    @BeforeEach
    void setUp() {
        Club club = new Club();
        club.setId(1L);
        club.setNombre("Club Test");

        torneo = new Torneo();
        torneo.setId(1L);
        torneo.setNombre("Torneo Verano ZGZ");
        torneo.setFechaInicio(LocalDate.of(2025, 7, 1));
        torneo.setFechaFin(LocalDate.of(2025, 7, 5));
        torneo.setMaxParticipantes(16);
        torneo.setInscripcionAbierta(true);
        torneo.setPrecioInscripcion(25.0f);
        torneo.setClub(club);
    }

    @Test
    @DisplayName("GET /torneos devuelve 200 con lista de torneos")
    void getTorneos_returns200() {
        when(torneoService.findAll()).thenReturn(Set.of(torneo));
        ResponseEntity<Set<Torneo>> response = torneoController.getTorneos(null, null, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("GET /torneos?inscripcionAbierta=true devuelve 200 filtrado")
    void getTorneos_withInscripcionAbiertaFilter_returns200() {
        when(torneoService.findByInscripcionAbierta(true)).thenReturn(Set.of(torneo));
        ResponseEntity<Set<Torneo>> response = torneoController.getTorneos("true", null, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("GET /torneos?inscripcionAbierta=true&clubId=1 devuelve 200 combinado")
    void getTorneos_withInscripcionAbiertaAndClubFilter_returns200() {
        when(torneoService.findByInscripcionAbiertaAndClubId(true, 1L)).thenReturn(Set.of(torneo));
        ResponseEntity<Set<Torneo>> response = torneoController.getTorneos("true", 1L, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /torneos?desde=2025-07-01&hasta=2025-07-31 devuelve 200 por rango de fechas")
    void getTorneos_withFechaRangeFilter_returns200() {
        LocalDate desde = LocalDate.of(2025, 7, 1);
        LocalDate hasta = LocalDate.of(2025, 7, 31);
        when(torneoService.findByFechaInicioBetween(desde, hasta)).thenReturn(Set.of(torneo));
        ResponseEntity<Set<Torneo>> response = torneoController.getTorneos(null, null, desde, hasta);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /torneos/{id} devuelve 200 cuando existe")
    void getTorneo_whenExists_returns200() {
        when(torneoService.findById(1L)).thenReturn(Optional.of(torneo));
        ResponseEntity<Torneo> response = torneoController.getTorneo(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Torneo Verano ZGZ", response.getBody().getNombre());
    }

    @Test
    @DisplayName("GET /torneos/{id} lanza 404 cuando no existe")
    void getTorneo_whenNotExists_throws404() {
        when(torneoService.findById(99L)).thenReturn(Optional.empty());
        assertThrows(TorneoNotFoundException.class, () -> torneoController.getTorneo(99L));
    }

    @Test
    @DisplayName("POST /torneos/club/{clubId} devuelve 201")
    void addTorneo_returns201() {
        when(torneoService.addTorneo(any(Torneo.class), eq(1L))).thenReturn(torneo);
        ResponseEntity<Torneo> response = torneoController.addTorneo(1L, torneo);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("POST /torneos/club/{clubId} lanza 404 cuando el club no existe")
    void addTorneo_whenClubNotFound_throws404() {
        when(torneoService.addTorneo(any(Torneo.class), eq(99L))).thenThrow(new ClubNotFoundException(99L));
        assertThrows(ClubNotFoundException.class, () -> torneoController.addTorneo(99L, torneo));
    }

    @Test
    @DisplayName("PUT /torneos/{id} devuelve 200")
    void modifyTorneo_returns200() {
        when(torneoService.modifyTorneo(eq(1L), any(Torneo.class))).thenReturn(torneo);
        ResponseEntity<Torneo> response = torneoController.modifyTorneo(1L, torneo);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /torneos/{id} lanza 404 cuando no existe")
    void modifyTorneo_whenNotExists_throws404() {
        when(torneoService.modifyTorneo(eq(99L), any(Torneo.class))).thenThrow(new TorneoNotFoundException(99L));
        assertThrows(TorneoNotFoundException.class, () -> torneoController.modifyTorneo(99L, torneo));
    }

    @Test
    @DisplayName("PATCH /torneos/{id} devuelve 200")
    void patchTorneo_returns200() {
        when(torneoService.patchTorneo(eq(1L), any(Map.class))).thenReturn(torneo);
        ResponseEntity<Torneo> response = torneoController.patchTorneo(1L, Map.of("nombre", "Torneo Otoño"));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /torneos/{id} devuelve 204")
    void deleteTorneo_returns204() {
        doNothing().when(torneoService).deleteTorneo(1L);
        ResponseEntity<Void> response = torneoController.deleteTorneo(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /torneos/{id} lanza 404 cuando no existe")
    void deleteTorneo_whenNotExists_throws404() {
        doThrow(new TorneoNotFoundException(99L)).when(torneoService).deleteTorneo(99L);
        assertThrows(TorneoNotFoundException.class, () -> torneoController.deleteTorneo(99L));
    }
}

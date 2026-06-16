package com.padelzgz.api.controller;

import com.padelzgz.api.exception.ClubNotFoundException;
import com.padelzgz.api.exception.PistaNotFoundException;
import com.padelzgz.api.model.Club;
import com.padelzgz.api.model.Pista;
import com.padelzgz.api.service.PistaService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PistaControllerTest {

    @Mock private PistaService pistaService;
    @InjectMocks private PistaController pistaController;

    private Pista pista;
    private Club club;

    @BeforeEach
    void setUp() {
        club = new Club();
        club.setId(1L);
        club.setNombre("Club Test");
        club.setCiudad("Zaragoza");

        pista = new Pista();
        pista.setId(1L);
        pista.setNumero(1);
        pista.setTipo("CRISTAL");
        pista.setInterior(true);
        pista.setPrecioHora(12.5f);
        pista.setActiva(true);
        pista.setClub(club);
    }

    @Test
    @DisplayName("GET /pistas devuelve 200 con lista de pistas")
    void getPistas_returns200() {
        when(pistaService.findAll()).thenReturn(Set.of(pista));
        ResponseEntity<Set<Pista>> response = pistaController.getPistas(null, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("GET /pistas?tipo=CRISTAL devuelve 200 con filtro por tipo")
    void getPistas_withTipoFilter_returns200() {
        when(pistaService.findByTipo("CRISTAL")).thenReturn(Set.of(pista));
        ResponseEntity<Set<Pista>> response = pistaController.getPistas("CRISTAL", null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("GET /pistas?tipo=CRISTAL&interior=true&activa=true devuelve 200 con 3 filtros")
    void getPistas_withAllFilters_returns200() {
        when(pistaService.findByTipoAndInteriorAndActiva("CRISTAL", true, true)).thenReturn(Set.of(pista));
        ResponseEntity<Set<Pista>> response = pistaController.getPistas("CRISTAL", "true", "true");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /pistas/{id} devuelve 200 cuando existe")
    void getPista_whenExists_returns200() {
        when(pistaService.findById(1L)).thenReturn(Optional.of(pista));
        ResponseEntity<Pista> response = pistaController.getPista(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("CRISTAL", response.getBody().getTipo());
    }

    @Test
    @DisplayName("GET /pistas/{id} lanza 404 cuando no existe")
    void getPista_whenNotExists_throws404() {
        when(pistaService.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PistaNotFoundException.class, () -> pistaController.getPista(99L));
    }

    @Test
    @DisplayName("POST /pistas/club/{clubId} devuelve 201")
    void addPista_returns201() {
        when(pistaService.addPista(any(Pista.class), eq(1L))).thenReturn(pista);
        ResponseEntity<Pista> response = pistaController.addPista(1L, pista);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("POST /pistas/club/{clubId} lanza 404 cuando el club no existe")
    void addPista_whenClubNotFound_throws404() {
        when(pistaService.addPista(any(Pista.class), eq(99L))).thenThrow(new ClubNotFoundException(99L));
        assertThrows(ClubNotFoundException.class, () -> pistaController.addPista(99L, pista));
    }

    @Test
    @DisplayName("PUT /pistas/{id} devuelve 200")
    void modifyPista_returns200() {
        when(pistaService.modifyPista(eq(1L), any(Pista.class))).thenReturn(pista);
        ResponseEntity<Pista> response = pistaController.modifyPista(1L, pista);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /pistas/{id} lanza 404 cuando no existe")
    void modifyPista_whenNotExists_throws404() {
        when(pistaService.modifyPista(eq(99L), any(Pista.class))).thenThrow(new PistaNotFoundException(99L));
        assertThrows(PistaNotFoundException.class, () -> pistaController.modifyPista(99L, pista));
    }

    @Test
    @DisplayName("PATCH /pistas/{id} devuelve 200")
    void patchPista_returns200() {
        when(pistaService.patchPista(eq(1L), any(Map.class))).thenReturn(pista);
        ResponseEntity<Pista> response = pistaController.patchPista(1L, Map.of("tipo", "PANORAMICA"));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /pistas/{id} devuelve 204")
    void deletePista_returns204() {
        doNothing().when(pistaService).deletePista(1L);
        ResponseEntity<Void> response = pistaController.deletePista(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /pistas/{id} lanza 404 cuando no existe")
    void deletePista_whenNotExists_throws404() {
        doThrow(new PistaNotFoundException(99L)).when(pistaService).deletePista(99L);
        assertThrows(PistaNotFoundException.class, () -> pistaController.deletePista(99L));
    }
}

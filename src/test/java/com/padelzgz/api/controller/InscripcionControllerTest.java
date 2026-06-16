package com.padelzgz.api.controller;

import com.padelzgz.api.dto.InscripcionInDTO;
import com.padelzgz.api.exception.InscripcionNotFoundException;
import com.padelzgz.api.exception.TorneoNotFoundException;
import com.padelzgz.api.exception.UsuarioNotFoundException;
import com.padelzgz.api.model.Inscripcion;
import com.padelzgz.api.model.Torneo;
import com.padelzgz.api.model.Usuario;
import com.padelzgz.api.service.InscripcionService;
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
class InscripcionControllerTest {

    @Mock private InscripcionService inscripcionService;
    @InjectMocks private InscripcionController inscripcionController;

    private Inscripcion inscripcion;
    private InscripcionInDTO dto;

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Torneo torneo = new Torneo();
        torneo.setId(1L);

        inscripcion = new Inscripcion();
        inscripcion.setId(1L);
        inscripcion.setFechaInscripcion(LocalDate.now());
        inscripcion.setEstado("PENDIENTE");
        inscripcion.setPagado(false);
        inscripcion.setNumeroPareja(1);
        inscripcion.setUsuario(usuario);
        inscripcion.setTorneo(torneo);

        dto = new InscripcionInDTO();
        dto.setFechaInscripcion(LocalDate.now());
        dto.setEstado("PENDIENTE");
        dto.setUsuarioId(1L);
        dto.setTorneoId(1L);
    }

    @Test
    @DisplayName("GET /inscripciones devuelve 200 con lista")
    void getInscripciones_returns200() {
        when(inscripcionService.findAll()).thenReturn(Set.of(inscripcion));
        ResponseEntity<Set<Inscripcion>> response = inscripcionController.getInscripciones(null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("GET /inscripciones?torneoId=1 devuelve 200 filtrado por torneo")
    void getInscripciones_withTorneoFilter_returns200() {
        when(inscripcionService.findByTorneoId(1L)).thenReturn(Set.of(inscripcion));
        ResponseEntity<Set<Inscripcion>> response = inscripcionController.getInscripciones(1L, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /inscripciones?torneoId=1&estado=PENDIENTE devuelve 200 con ambos filtros")
    void getInscripciones_withBothFilters_returns200() {
        when(inscripcionService.findByTorneoIdAndEstado(1L, "PENDIENTE")).thenReturn(Set.of(inscripcion));
        ResponseEntity<Set<Inscripcion>> response = inscripcionController.getInscripciones(1L, "PENDIENTE");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /inscripciones/{id} devuelve 200 cuando existe")
    void getInscripcion_whenExists_returns200() {
        when(inscripcionService.findById(1L)).thenReturn(Optional.of(inscripcion));
        ResponseEntity<Inscripcion> response = inscripcionController.getInscripcion(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("PENDIENTE", response.getBody().getEstado());
    }

    @Test
    @DisplayName("GET /inscripciones/{id} lanza 404 cuando no existe")
    void getInscripcion_whenNotExists_throws404() {
        when(inscripcionService.findById(99L)).thenReturn(Optional.empty());
        assertThrows(InscripcionNotFoundException.class, () -> inscripcionController.getInscripcion(99L));
    }

    @Test
    @DisplayName("POST /inscripciones devuelve 201")
    void addInscripcion_returns201() {
        when(inscripcionService.addInscripcion(any(InscripcionInDTO.class))).thenReturn(inscripcion);
        ResponseEntity<Inscripcion> response = inscripcionController.addInscripcion(dto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("POST /inscripciones devuelve 404 si el usuario no existe")
    void addInscripcion_whenUsuarioNotFound_throws404() {
        when(inscripcionService.addInscripcion(any(InscripcionInDTO.class))).thenThrow(new UsuarioNotFoundException(1L));
        assertThrows(UsuarioNotFoundException.class, () -> inscripcionController.addInscripcion(dto));
    }

    @Test
    @DisplayName("POST /inscripciones devuelve 404 si el torneo no existe")
    void addInscripcion_whenTorneoNotFound_throws404() {
        when(inscripcionService.addInscripcion(any(InscripcionInDTO.class))).thenThrow(new TorneoNotFoundException(1L));
        assertThrows(TorneoNotFoundException.class, () -> inscripcionController.addInscripcion(dto));
    }

    @Test
    @DisplayName("PUT /inscripciones/{id} devuelve 200")
    void modifyInscripcion_returns200() {
        when(inscripcionService.modifyInscripcion(eq(1L), any(InscripcionInDTO.class))).thenReturn(inscripcion);
        ResponseEntity<Inscripcion> response = inscripcionController.modifyInscripcion(1L, dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /inscripciones/{id} lanza 404 cuando no existe")
    void modifyInscripcion_whenNotExists_throws404() {
        when(inscripcionService.modifyInscripcion(eq(99L), any(InscripcionInDTO.class))).thenThrow(new InscripcionNotFoundException(99L));
        assertThrows(InscripcionNotFoundException.class, () -> inscripcionController.modifyInscripcion(99L, dto));
    }

    @Test
    @DisplayName("PATCH /inscripciones/{id} devuelve 200")
    void patchInscripcion_returns200() {
        when(inscripcionService.patchInscripcion(eq(1L), any(Map.class))).thenReturn(inscripcion);
        ResponseEntity<Inscripcion> response = inscripcionController.patchInscripcion(1L, Map.of("estado", "CONFIRMADA"));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /inscripciones/{id} devuelve 204")
    void deleteInscripcion_returns204() {
        doNothing().when(inscripcionService).deleteInscripcion(1L);
        ResponseEntity<Void> response = inscripcionController.deleteInscripcion(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /inscripciones/{id} lanza 404 cuando no existe")
    void deleteInscripcion_whenNotExists_throws404() {
        doThrow(new InscripcionNotFoundException(99L)).when(inscripcionService).deleteInscripcion(99L);
        assertThrows(InscripcionNotFoundException.class, () -> inscripcionController.deleteInscripcion(99L));
    }
}

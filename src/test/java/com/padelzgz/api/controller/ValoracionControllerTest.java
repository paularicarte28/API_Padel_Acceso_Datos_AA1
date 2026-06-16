package com.padelzgz.api.controller;

import com.padelzgz.api.exception.PistaNotFoundException;
import com.padelzgz.api.exception.UsuarioNotFoundException;
import com.padelzgz.api.exception.ValoracionNotFoundException;
import com.padelzgz.api.model.Pista;
import com.padelzgz.api.model.Usuario;
import com.padelzgz.api.model.Valoracion;
import com.padelzgz.api.service.ValoracionService;
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
class ValoracionControllerTest {

    @Mock private ValoracionService valoracionService;
    @InjectMocks private ValoracionController valoracionController;

    private Valoracion valoracion;

    @BeforeEach
    void setUp() {
        Pista pista = new Pista();
        pista.setId(1L);

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        valoracion = new Valoracion();
        valoracion.setId(1L);
        valoracion.setPuntuacion(5);
        valoracion.setComentario("Pista excelente");
        valoracion.setFechaValoracion(LocalDate.now());
        valoracion.setVerificada(false);
        valoracion.setVisibilidad("PUBLICA");
        valoracion.setPista(pista);
        valoracion.setUsuario(usuario);
    }

    @Test
    @DisplayName("GET /valoraciones devuelve 200 con lista")
    void getValoraciones_returns200() {
        when(valoracionService.findAll()).thenReturn(Set.of(valoracion));
        ResponseEntity<Set<Valoracion>> response = valoracionController.getValoraciones(null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("GET /valoraciones?pistaId=1 devuelve 200 filtrado por pista")
    void getValoraciones_withPistaFilter_returns200() {
        when(valoracionService.findByPistaId(1L)).thenReturn(Set.of(valoracion));
        ResponseEntity<Set<Valoracion>> response = valoracionController.getValoraciones(1L, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /valoraciones?pistaId=1&puntuacion=5 devuelve 200 con ambos filtros")
    void getValoraciones_withBothFilters_returns200() {
        when(valoracionService.findByPistaIdAndPuntuacion(1L, 5)).thenReturn(Set.of(valoracion));
        ResponseEntity<Set<Valoracion>> response = valoracionController.getValoraciones(1L, 5);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /valoraciones/{id} devuelve 200 cuando existe")
    void getValoracion_whenExists_returns200() {
        when(valoracionService.findById(1L)).thenReturn(Optional.of(valoracion));
        ResponseEntity<Valoracion> response = valoracionController.getValoracion(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody().getPuntuacion());
    }

    @Test
    @DisplayName("GET /valoraciones/{id} lanza 404 cuando no existe")
    void getValoracion_whenNotExists_throws404() {
        when(valoracionService.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ValoracionNotFoundException.class, () -> valoracionController.getValoracion(99L));
    }

    @Test
    @DisplayName("POST /valoraciones/pista/{pistaId}/usuario/{usuarioId} devuelve 201")
    void addValoracion_returns201() {
        when(valoracionService.addValoracion(any(Valoracion.class), eq(1L), eq(1L))).thenReturn(valoracion);
        ResponseEntity<Valoracion> response = valoracionController.addValoracion(1L, 1L, valoracion);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("POST /valoraciones lanza 404 si la pista no existe")
    void addValoracion_whenPistaNotFound_throws404() {
        when(valoracionService.addValoracion(any(Valoracion.class), eq(99L), eq(1L)))
                .thenThrow(new PistaNotFoundException(99L));
        assertThrows(PistaNotFoundException.class, () -> valoracionController.addValoracion(99L, 1L, valoracion));
    }

    @Test
    @DisplayName("POST /valoraciones lanza 404 si el usuario no existe")
    void addValoracion_whenUsuarioNotFound_throws404() {
        when(valoracionService.addValoracion(any(Valoracion.class), eq(1L), eq(99L)))
                .thenThrow(new UsuarioNotFoundException(99L));
        assertThrows(UsuarioNotFoundException.class, () -> valoracionController.addValoracion(1L, 99L, valoracion));
    }

    @Test
    @DisplayName("PUT /valoraciones/{id} devuelve 200")
    void modifyValoracion_returns200() {
        when(valoracionService.modifyValoracion(eq(1L), any(Valoracion.class))).thenReturn(valoracion);
        ResponseEntity<Valoracion> response = valoracionController.modifyValoracion(1L, valoracion);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /valoraciones/{id} lanza 404 cuando no existe")
    void modifyValoracion_whenNotExists_throws404() {
        when(valoracionService.modifyValoracion(eq(99L), any(Valoracion.class)))
                .thenThrow(new ValoracionNotFoundException(99L));
        assertThrows(ValoracionNotFoundException.class, () -> valoracionController.modifyValoracion(99L, valoracion));
    }

    @Test
    @DisplayName("PATCH /valoraciones/{id} devuelve 200")
    void patchValoracion_returns200() {
        when(valoracionService.patchValoracion(eq(1L), any(Map.class))).thenReturn(valoracion);
        ResponseEntity<Valoracion> response = valoracionController.patchValoracion(1L, Map.of("puntuacion", 4));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /valoraciones/{id} devuelve 204")
    void deleteValoracion_returns204() {
        doNothing().when(valoracionService).deleteValoracion(1L);
        ResponseEntity<Void> response = valoracionController.deleteValoracion(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /valoraciones/{id} lanza 404 cuando no existe")
    void deleteValoracion_whenNotExists_throws404() {
        doThrow(new ValoracionNotFoundException(99L)).when(valoracionService).deleteValoracion(99L);
        assertThrows(ValoracionNotFoundException.class, () -> valoracionController.deleteValoracion(99L));
    }
}

package com.padelzgz.api.controller;

import com.padelzgz.api.dto.ReservaInDTO;
import com.padelzgz.api.exception.PistaNotFoundException;
import com.padelzgz.api.exception.ReservaNotFoundException;
import com.padelzgz.api.exception.UsuarioNotFoundException;
import com.padelzgz.api.model.Pista;
import com.padelzgz.api.model.Reserva;
import com.padelzgz.api.model.Usuario;
import com.padelzgz.api.service.ReservaService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaControllerTest {

    @Mock private ReservaService reservaService;
    @InjectMocks private ReservaController reservaController;

    private Reserva reserva;
    private ReservaInDTO dto;

    @BeforeEach
    void setUp() {
        Pista pista = new Pista();
        pista.setId(1L);

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        reserva = new Reserva();
        reserva.setId(1L);
        reserva.setFecha(LocalDate.of(2025, 7, 15));
        reserva.setHoraInicio(LocalTime.of(10, 0));
        reserva.setHoraFin(LocalTime.of(11, 0));
        reserva.setPrecio(12.5f);
        reserva.setPagado(false);
        reserva.setPista(pista);
        reserva.setUsuario(usuario);

        dto = new ReservaInDTO();
        dto.setFecha(LocalDate.of(2025, 7, 15));
        dto.setHoraInicio("10:00");
        dto.setHoraFin("11:00");
        dto.setPistaId(1L);
        dto.setUsuarioId(1L);
    }

    @Test
    @DisplayName("GET /reservas devuelve 200 con lista de reservas")
    void getReservas_returns200() {
        when(reservaService.findAll()).thenReturn(Set.of(reserva));
        ResponseEntity<Set<Reserva>> response = reservaController.getReservas(null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("GET /reservas?fecha=2025-07-15 devuelve 200 filtrado por fecha")
    void getReservas_withFechaFilter_returns200() {
        LocalDate fecha = LocalDate.of(2025, 7, 15);
        when(reservaService.findByFecha(fecha)).thenReturn(Set.of(reserva));
        ResponseEntity<Set<Reserva>> response = reservaController.getReservas(fecha, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /reservas?fecha=2025-07-15&pagado=false devuelve 200 con ambos filtros")
    void getReservas_withBothFilters_returns200() {
        LocalDate fecha = LocalDate.of(2025, 7, 15);
        when(reservaService.findByFechaAndPagado(fecha, false)).thenReturn(Set.of(reserva));
        ResponseEntity<Set<Reserva>> response = reservaController.getReservas(fecha, "false");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /reservas/{id} devuelve 200 cuando existe")
    void getReserva_whenExists_returns200() {
        when(reservaService.findById(1L)).thenReturn(Optional.of(reserva));
        ResponseEntity<Reserva> response = reservaController.getReserva(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("GET /reservas/{id} lanza 404 cuando no existe")
    void getReserva_whenNotExists_throws404() {
        when(reservaService.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ReservaNotFoundException.class, () -> reservaController.getReserva(99L));
    }

    @Test
    @DisplayName("POST /reservas devuelve 201")
    void addReserva_returns201() {
        when(reservaService.addReserva(any(ReservaInDTO.class))).thenReturn(reserva);
        ResponseEntity<Reserva> response = reservaController.addReserva(dto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("POST /reservas devuelve 404 si la pista no existe")
    void addReserva_whenPistaNotFound_throws404() {
        when(reservaService.addReserva(any(ReservaInDTO.class))).thenThrow(new PistaNotFoundException(1L));
        assertThrows(PistaNotFoundException.class, () -> reservaController.addReserva(dto));
    }

    @Test
    @DisplayName("POST /reservas devuelve 404 si el usuario no existe")
    void addReserva_whenUsuarioNotFound_throws404() {
        when(reservaService.addReserva(any(ReservaInDTO.class))).thenThrow(new UsuarioNotFoundException(1L));
        assertThrows(UsuarioNotFoundException.class, () -> reservaController.addReserva(dto));
    }

    @Test
    @DisplayName("PUT /reservas/{id} devuelve 200")
    void modifyReserva_returns200() {
        when(reservaService.modifyReserva(eq(1L), any(ReservaInDTO.class))).thenReturn(reserva);
        ResponseEntity<Reserva> response = reservaController.modifyReserva(1L, dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /reservas/{id} lanza 404 cuando no existe")
    void modifyReserva_whenNotExists_throws404() {
        when(reservaService.modifyReserva(eq(99L), any(ReservaInDTO.class))).thenThrow(new ReservaNotFoundException(99L));
        assertThrows(ReservaNotFoundException.class, () -> reservaController.modifyReserva(99L, dto));
    }

    @Test
    @DisplayName("PATCH /reservas/{id} devuelve 200")
    void patchReserva_returns200() {
        when(reservaService.patchReserva(eq(1L), any(Map.class))).thenReturn(reserva);
        ResponseEntity<Reserva> response = reservaController.patchReserva(1L, Map.of("pagado", true));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /reservas/{id} devuelve 204")
    void deleteReserva_returns204() {
        doNothing().when(reservaService).deleteReserva(1L);
        ResponseEntity<Void> response = reservaController.deleteReserva(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /reservas/{id} lanza 404 cuando no existe")
    void deleteReserva_whenNotExists_throws404() {
        doThrow(new ReservaNotFoundException(99L)).when(reservaService).deleteReserva(99L);
        assertThrows(ReservaNotFoundException.class, () -> reservaController.deleteReserva(99L));
    }
}

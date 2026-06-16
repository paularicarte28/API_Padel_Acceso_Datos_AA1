package com.padelzgz.api.controller;

import com.padelzgz.api.exception.UsuarioNotFoundException;
import com.padelzgz.api.model.Usuario;
import com.padelzgz.api.service.UsuarioService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock private UsuarioService usuarioService;
    @InjectMocks private UsuarioController usuarioController;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Pau");
        usuario.setApellidos("Ricarte");
        usuario.setEmail("pau@padelzgz.com");
        usuario.setNivel("AVANZADO");
        usuario.setFechaRegistro(LocalDate.now());
    }

    @Test
    @DisplayName("GET /usuarios devuelve 200 con lista de usuarios")
    void getUsuarios_returns200() {
        when(usuarioService.findAll()).thenReturn(Set.of(usuario));
        ResponseEntity<Set<Usuario>> response = usuarioController.getUsuarios(null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("GET /usuarios?nivel=AVANZADO devuelve 200 filtrado por nivel")
    void getUsuarios_withNivelFilter_returns200() {
        when(usuarioService.findByNivel("AVANZADO")).thenReturn(Set.of(usuario));
        ResponseEntity<Set<Usuario>> response = usuarioController.getUsuarios("AVANZADO", null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("GET /usuarios?nivel=AVANZADO&nombre=Pau devuelve 200 con ambos filtros")
    void getUsuarios_withBothFilters_returns200() {
        when(usuarioService.findByNivelAndNombre("AVANZADO", "Pau")).thenReturn(Set.of(usuario));
        ResponseEntity<Set<Usuario>> response = usuarioController.getUsuarios("AVANZADO", "Pau");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /usuarios/{id} devuelve 200 cuando existe")
    void getUsuario_whenExists_returns200() {
        when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));
        ResponseEntity<Usuario> response = usuarioController.getUsuario(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Pau", response.getBody().getNombre());
    }

    @Test
    @DisplayName("GET /usuarios/{id} lanza 404 cuando no existe")
    void getUsuario_whenNotExists_throws404() {
        when(usuarioService.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UsuarioNotFoundException.class, () -> usuarioController.getUsuario(99L));
    }

    @Test
    @DisplayName("POST /usuarios devuelve 201")
    void addUsuario_returns201() {
        when(usuarioService.addUsuario(any(Usuario.class))).thenReturn(usuario);
        ResponseEntity<Usuario> response = usuarioController.addUsuario(usuario);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("PUT /usuarios/{id} devuelve 200")
    void modifyUsuario_returns200() {
        when(usuarioService.modifyUsuario(eq(1L), any(Usuario.class))).thenReturn(usuario);
        ResponseEntity<Usuario> response = usuarioController.modifyUsuario(1L, usuario);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /usuarios/{id} lanza 404 cuando no existe")
    void modifyUsuario_whenNotExists_throws404() {
        when(usuarioService.modifyUsuario(eq(99L), any(Usuario.class))).thenThrow(new UsuarioNotFoundException(99L));
        assertThrows(UsuarioNotFoundException.class, () -> usuarioController.modifyUsuario(99L, usuario));
    }

    @Test
    @DisplayName("PATCH /usuarios/{id} devuelve 200")
    void patchUsuario_returns200() {
        when(usuarioService.patchUsuario(eq(1L), any(Usuario.class))).thenReturn(usuario);
        ResponseEntity<Usuario> response = usuarioController.patchUsuario(1L, usuario);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /usuarios/{id} devuelve 204")
    void deleteUsuario_returns204() {
        doNothing().when(usuarioService).deleteUsuario(1L);
        ResponseEntity<Void> response = usuarioController.deleteUsuario(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /usuarios/{id} lanza 404 cuando no existe")
    void deleteUsuario_whenNotExists_throws404() {
        doThrow(new UsuarioNotFoundException(99L)).when(usuarioService).deleteUsuario(99L);
        assertThrows(UsuarioNotFoundException.class, () -> usuarioController.deleteUsuario(99L));
    }
}

package com.sky.Soporte.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.sky.Soporte.model.Soporte;
import com.sky.Soporte.model.UsuarioDTO;
import com.sky.Soporte.repository.SoporteRepository;
import com.sky.Soporte.service.SoporteService;

@ExtendWith(MockitoExtension.class)
public class SoporteServiceTest {

    @Mock
    private SoporteRepository soporteRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SoporteService soporteService;

    private Soporte soporte;

    @BeforeEach
    void setUp() {
        soporte = new Soporte();
        soporte.setIdUsuario(1L);
        soporte.setAsunto("Problema con el servicio");
        soporte.setDescripcion("No puedo acceder a mi cuenta");
    }

    // ─────────────────────────────────────────────
    // crearTicket
    // ─────────────────────────────────────────────

    @Test
    void crearTicket_exitoso() {
        // El servicio externo devuelve un usuario válido
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class)))
                .thenReturn(usuarioDTO);

        // El repo guarda y devuelve el ticket con estado=true
        Soporte guardado = new Soporte();
        guardado.setIdUsuario(1L);
        guardado.setAsunto("Problema con el servicio");
        guardado.setDescripcion("No puedo acceder a mi cuenta");
        guardado.setEstado(true);
        when(soporteRepository.save(any(Soporte.class))).thenReturn(guardado);

        Soporte resultado = soporteService.crearTicket(soporte);

        assertNotNull(resultado);
        assertTrue(resultado.isEstado()); // ticket abierto
        verify(soporteRepository, times(1)).save(soporte);
    }

    @Test
    void crearTicket_usuarioNoExiste_restTemplateRetornaNull() {
        // restTemplate devuelve null → usuario no encontrado
        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class)))
                .thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> soporteService.crearTicket(soporte));

        assertTrue(ex.getMessage().contains("Usuario no encontrado con id: 1"));
        verify(soporteRepository, never()).save(any());
    }

@Test
void crearTicket_usuarioNoExiste_http404() {
    // ✅ Caso específico: 404 del microservicio de usuarios
    when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class)))
            .thenThrow(HttpClientErrorException.NotFound.class);

    RuntimeException ex = assertThrows(RuntimeException.class,
            () -> soporteService.crearTicket(soporte));

    assertTrue(ex.getMessage().contains("Usuario no encontrado con id: 1"));
    verify(soporteRepository, never()).save(any());
}

@Test
void crearTicket_usuarioNoExiste_httpClientErrorException() {
    // ✅ Caso genérico: otro error 4xx (401, 403, etc.)
    when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));

    RuntimeException ex = assertThrows(RuntimeException.class,
            () -> soporteService.crearTicket(soporte));

    assertTrue(ex.getMessage().contains("Error al validar usuario: 403"));
    verify(soporteRepository, never()).save(any());
}

@Test
void crearTicket_servicioNoDisponible_resourceAccessException() {
    // ✅ Caso nuevo: microservicio caído o sin conexión
    when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class)))
            .thenThrow(new ResourceAccessException("Connection refused"));

    RuntimeException ex = assertThrows(RuntimeException.class,
            () -> soporteService.crearTicket(soporte));

    assertTrue(ex.getMessage().contains("Servicio de usuarios no disponible"));
    verify(soporteRepository, never()).save(any());
}
    @Test
    void crearTicket_servicioNoDisponible() {
        // Cualquier excepción de red que no sea HttpClientErrorException
        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class)))
                .thenThrow(new RuntimeException("Servicio no disponible"));

        assertThrows(RuntimeException.class,
                () -> soporteService.crearTicket(soporte));

        verify(soporteRepository, never()).save(any());
    }

    @Test
    void crearTicket_estableceEstadoTrue_antesDeGuardar() {
        // Verifica que el service fija estado=true antes de persistir
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class)))
                .thenReturn(usuarioDTO);
        when(soporteRepository.save(any(Soporte.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Soporte resultado = soporteService.crearTicket(soporte);

        assertTrue(resultado.isEstado());
    }

    // ─────────────────────────────────────────────
    // cerrarTicket
    // ─────────────────────────────────────────────

    @Test
    void cerrarTicket_exitoso() {
        soporte.setEstado(true);
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporte));
        when(soporteRepository.save(any(Soporte.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Soporte resultado = soporteService.cerrarTicket(1L);

        assertFalse(resultado.isEstado()); // ticket cerrado
        verify(soporteRepository).save(soporte);
    }

    @Test
    void cerrarTicket_ticketNoEncontrado() {
        when(soporteRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> soporteService.cerrarTicket(99L));

        assertTrue(ex.getMessage().contains("Ticket no encontrado con id: 99"));
        verify(soporteRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // actualizarTicket
    // ─────────────────────────────────────────────

    @Test
    void actualizarTicket_exitoso() {
        Soporte existente = new Soporte();
        existente.setAsunto("Asunto viejo");
        existente.setDescripcion("Descripción vieja");
        existente.setEstado(true);

        when(soporteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(soporteRepository.save(any(Soporte.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Soporte actualizado = new Soporte();
        actualizado.setAsunto("Nuevo asunto");
        actualizado.setDescripcion("Nueva descripción");
        actualizado.setEstado(false);

        Soporte resultado = soporteService.actualizarTicket(1L, actualizado);

        assertEquals("Nuevo asunto", resultado.getAsunto());
        assertEquals("Nueva descripción", resultado.getDescripcion());
        assertFalse(resultado.isEstado());
    }

    @Test
    void actualizarTicket_ticketNoEncontrado() {
        when(soporteRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> soporteService.actualizarTicket(99L, soporte));

        assertTrue(ex.getMessage().contains("Ticket no encontrado con id: 99"));
        verify(soporteRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // obtenerTodos
    // ─────────────────────────────────────────────

    @Test
    void obtenerTodos_retornaLista() {
        Soporte s1 = new Soporte();
        Soporte s2 = new Soporte();
        when(soporteRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

        List<Soporte> resultado = soporteService.obtenerTodos();

        assertEquals(2, resultado.size());
        verify(soporteRepository).findAll();
    }

    @Test
    void obtenerTodos_listaVacia() {
        when(soporteRepository.findAll()).thenReturn(List.of());

        List<Soporte> resultado = soporteService.obtenerTodos();

        assertTrue(resultado.isEmpty());
    }

    // ─────────────────────────────────────────────
    // obtenerTicket
    // ─────────────────────────────────────────────

    @Test
    void obtenerTicket_exitoso() {
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporte));

        Soporte resultado = soporteService.obtenerTicket(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdUsuario());
    }

    @Test
    void obtenerTicket_noEncontrado() {
        when(soporteRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> soporteService.obtenerTicket(99L));

        assertTrue(ex.getMessage().contains("Ticket no encontrado con id: 99"));
    }

    // ─────────────────────────────────────────────
    // eliminarTicket
    // ─────────────────────────────────────────────

    @Test
    void eliminarTicket_exitoso() {
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporte));
        doNothing().when(soporteRepository).delete(soporte);

        assertDoesNotThrow(() -> soporteService.eliminarTicket(1L));

        verify(soporteRepository).delete(soporte);
    }

    @Test
    void eliminarTicket_noEncontrado() {
        when(soporteRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> soporteService.eliminarTicket(99L));

        assertTrue(ex.getMessage().contains("Ticket no encontrado con id: 99"));
        verify(soporteRepository, never()).delete(any());
    }
}
package com.sky.Soporte.Controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.Soporte.controller.SoporteController;
import com.sky.Soporte.model.Soporte;
import com.sky.Soporte.service.SoporteService;

import java.util.List;

@WebMvcTest(SoporteController.class)
@ActiveProfiles("test")
public class SoporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SoporteService soporteService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── Helper para construir un Soporte de prueba ──────────────────────────
    private Soporte buildSoporte(Long idUsuario, String asunto, String descripcion, boolean estado) {
        Soporte s = new Soporte();
        s.setIdUsuario(idUsuario);
        s.setAsunto(asunto);
        s.setDescripcion(descripcion);
        s.setEstado(estado);
        return s;
    }

    // ── POST /api/v1/soporte ─────────────────────────────────────────────────

    @Test
    public void testCrearTicket_Exitoso() throws Exception {
        Soporte soporte = buildSoporte(1L, "Problema con el servicio",
                "No puedo acceder a mi cuenta", true); // estado=true: abierto

        Mockito.when(soporteService.crearTicket(Mockito.any(Soporte.class)))
                .thenReturn(soporte);

        mockMvc.perform(post("/api/v1/soporte")   // URL corregida
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(soporte)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.asunto").value("Problema con el servicio"))
                .andExpect(jsonPath("$.descripcion").value("No puedo acceder a mi cuenta"))
                .andExpect(jsonPath("$.estado").value(true)); // abierto al crear
    }

    @Test
    public void testCrearTicket_Fallo() throws Exception {
        Soporte soporte = buildSoporte(1L, "Problema", "Descripcion", true);

        Mockito.when(soporteService.crearTicket(Mockito.any(Soporte.class)))
                .thenThrow(new RuntimeException("Error al guardar"));

        mockMvc.perform(post("/api/v1/soporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(soporte)))
                .andExpect(status().isBadRequest()); // BAD_REQUEST, no 500
    }

    @Test
    public void testCrearTicket_UsuarioNoExiste() throws Exception {
        Soporte soporte = buildSoporte(999L, "Problema", "Usuario no existe", true);

        Mockito.when(soporteService.crearTicket(Mockito.any(Soporte.class)))
                .thenThrow(new RuntimeException("Usuario no encontrado con id: 999"));

        mockMvc.perform(post("/api/v1/soporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(soporte)))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/v1/soporte ──────────────────────────────────────────────────

    @Test
    public void testObtenerTodosTickets() throws Exception {
        Soporte soporte = buildSoporte(1L, "Problema con el servicio",
                "No puedo acceder a mi cuenta", true);

        Mockito.when(soporteService.obtenerTodos())
                .thenReturn(List.of(soporte)); // mock configurado

        mockMvc.perform(get("/api/v1/soporte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(1L))
                .andExpect(jsonPath("$[0].asunto").value("Problema con el servicio"))
                .andExpect(jsonPath("$[0].descripcion").value("No puedo acceder a mi cuenta"))
                .andExpect(jsonPath("$[0].estado").value(true));
    }

    // ── GET /api/v1/soporte/{ticketId} ───────────────────────────────────────

    @Test
    public void testObtenerTicket_Exitoso() throws Exception {
        Soporte soporte = buildSoporte(1L, "Problema con el servicio",
                "No puedo acceder a mi cuenta", true);

        Mockito.when(soporteService.obtenerTicket(1L))
                .thenReturn(soporte);

        mockMvc.perform(get("/api/v1/soporte/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.asunto").value("Problema con el servicio"))
                .andExpect(jsonPath("$.descripcion").value("No puedo acceder a mi cuenta"))
                .andExpect(jsonPath("$.estado").value(true));
    }

    @Test
    public void testObtenerTicket_NoEncontrado() throws Exception {
        Mockito.when(soporteService.obtenerTicket(999L))
                .thenThrow(new RuntimeException("Ticket no encontrado con id: 999"));

        mockMvc.perform(get("/api/v1/soporte/999"))
                .andExpect(status().isNotFound());
    }




    // ── PUT /api/v1/soporte/{ticketId} ───────────────────────────────────────

    @Test
    public void testActualizarTicket_Exitoso() throws Exception {
        Soporte soporte = buildSoporte(1L, "Problema con el servicio",
                "No puedo acceder a mi cuenta", true);

        Mockito.when(soporteService.actualizarTicket(Mockito.eq(1L), Mockito.any(Soporte.class)))
                .thenReturn(soporte); // mock configurado

        mockMvc.perform(put("/api/v1/soporte/1") // URL corregida
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(soporte)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.asunto").value("Problema con el servicio"))
                .andExpect(jsonPath("$.descripcion").value("No puedo acceder a mi cuenta"));
    }

    @Test
    public void testActualizarTicket_NoEncontrado() throws Exception {
        Soporte soporte = buildSoporte(1L, "Problema", "Descripcion", true);

        Mockito.when(soporteService.actualizarTicket(Mockito.eq(999L), Mockito.any(Soporte.class)))
                .thenThrow(new RuntimeException("Ticket no encontrado con id: 999"));

        mockMvc.perform(put("/api/v1/soporte/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(soporte)))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/v1/soporte/{ticketId}/cerrar ────────────────────────────────

    @Test
    public void testCerrarTicket_Exitoso() throws Exception {
        Soporte soporte = buildSoporte(1L, "Problema con el servicio",
                "No puedo acceder a mi cuenta", false); 

        Mockito.when(soporteService.cerrarTicket(1L))
                .thenReturn(soporte); // mock configurado

        mockMvc.perform(put("/api/v1/soporte/1/cerrar")) 
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value(false)); 
    }

    @Test
    public void testCerrarTicket_NoEncontrado() throws Exception {
        Mockito.when(soporteService.cerrarTicket(999L))
                .thenThrow(new RuntimeException("Ticket no encontrado con id: 999"));

        mockMvc.perform(put("/api/v1/soporte/999/cerrar"))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/v1/soporte/{ticketId} ────────────────────────────────────

    @Test
    public void testEliminarTicket_Exitoso() throws Exception {
        Mockito.doNothing().when(soporteService).eliminarTicket(1L); 

        mockMvc.perform(delete("/api/v1/soporte/1")) 
                .andExpect(status().isNoContent()); 
    }

    @Test
    public void testEliminarTicket_NoEncontrado() throws Exception {
        Mockito.doThrow(new RuntimeException("Ticket no encontrado con id: 999"))
                .when(soporteService).eliminarTicket(999L);

        mockMvc.perform(delete("/api/v1/soporte/999"))
                .andExpect(status().isNotFound());
    }
}
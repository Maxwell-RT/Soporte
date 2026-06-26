package com.sky.Soporte.Controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

        @MockitoBean
        private SoporteService soporteService;

        private final ObjectMapper objectMapper = new ObjectMapper();

        private static final List<Soporte> TICKETS_PRUEBA = List.of(
                        crearSoporte(1L, 1L, "Problema con el servicio", "No puedo acceder a mi cuenta", true),
                        crearSoporte(2L, 2L, "Error en la plataforma", "La página no carga correctamente", true),
                        crearSoporte(3L, 3L, "Cobro incorrecto", "Me cobraron dos veces", false),
                        crearSoporte(4L, 999L, "Usuario inexistente", "El usuario no existe", true),
                        crearSoporte(5L, 1L, "Servicio no disponible", "No hay conexión", true));

        private static Soporte crearSoporte(Long idSoporte, Long idUsuario,
                        String asunto, String descripcion, boolean estado) {
                Soporte s = new Soporte();
                s.setIdSoporte(idSoporte);
                s.setIdUsuario(idUsuario);
                s.setAsunto(asunto);
                s.setDescripcion(descripcion);
                s.setEstado(estado);
                return s;
        }

        // ── POST /api/v1/soporte ─────────────────────────────────────────────────

        @Test
        public void testCrearTicket_Exitoso() throws Exception {
                Soporte soporte = TICKETS_PRUEBA.get(0);

                Mockito.when(soporteService.crearTicket(Mockito.any(Soporte.class)))
                                .thenReturn(soporte);

                mockMvc.perform(post("/api/v1/soporte/crear")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(soporte)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.idUsuario").value(1L))
                                .andExpect(jsonPath("$.asunto").value("Problema con el servicio"))
                                .andExpect(jsonPath("$.descripcion").value("No puedo acceder a mi cuenta"))
                                .andExpect(jsonPath("$.estado").value(true));
        }

        @Test
        public void testCrearTicket_Fallo() throws Exception {
                Soporte soporte = TICKETS_PRUEBA.get(0);

                Mockito.when(soporteService.crearTicket(Mockito.any(Soporte.class)))
                                .thenThrow(new RuntimeException("Error al guardar"));

                mockMvc.perform(post("/api/v1/soporte/crear")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(soporte)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void testCrearTicket_UsuarioNoExiste() throws Exception {
                Soporte soporte = TICKETS_PRUEBA.get(3); // idUsuario=999

                Mockito.when(soporteService.crearTicket(Mockito.any(Soporte.class)))
                                .thenThrow(new RuntimeException("Usuario no encontrado con id: 999"));

                mockMvc.perform(post("/api/v1/soporte")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(soporte)))
                                .andExpect(status().isNotFound());
        }

        // ── GET /api/v1/soporte ──────────────────────────────────────────────────

        @Test
        public void testObtenerTodosTickets() throws Exception {
                Mockito.when(soporteService.obtenerTodos())
                                .thenReturn(TICKETS_PRUEBA); // devuelve toda la lista

                mockMvc.perform(get("/api/v1/soporte/listarT"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(5))
                                .andExpect(jsonPath("$[0].idUsuario").value(1L))
                                .andExpect(jsonPath("$[0].asunto").value("Problema con el servicio"))
                                .andExpect(jsonPath("$[1].asunto").value("Error en la plataforma"))
                                .andExpect(jsonPath("$[2].estado").value(false)); // ticket cerrado
        }

        // ── GET /api/v1/soporte/{ticketId} ───────────────────────────────────────

        @Test
        public void testObtenerTicket_Exitoso() throws Exception {
                Soporte soporte = TICKETS_PRUEBA.get(1); // ticket 2

                Mockito.when(soporteService.obtenerTicket(2L))
                                .thenReturn(soporte);

                mockMvc.perform(get("/api/v1/soporte/2"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idUsuario").value(2L))
                                .andExpect(jsonPath("$.asunto").value("Error en la plataforma"))
                                .andExpect(jsonPath("$.descripcion").value("La página no carga correctamente"));
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
                Soporte soporte = TICKETS_PRUEBA.get(3); // ticket 4

                Mockito.when(soporteService.actualizarTicket(Mockito.eq(4L), Mockito.any(Soporte.class)))
                                .thenReturn(soporte);

                mockMvc.perform(put("/api/v1/soporte/4")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(soporte)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idUsuario").value(999L))
                                .andExpect(jsonPath("$.asunto").value("Usuario inexistente"));
        }

        @Test
        public void testActualizarTicket_NoEncontrado() throws Exception {
                Soporte soporte = TICKETS_PRUEBA.get(0);

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
                Soporte soporte = TICKETS_PRUEBA.get(2); // ticket 3, estado=false (cerrado)

                Mockito.when(soporteService.cerrarTicket(3L))
                                .thenReturn(soporte);

                mockMvc.perform(put("/api/v1/soporte/3/cerrar"))
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
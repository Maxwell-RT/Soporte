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
public class SoporteControllerIT {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private SoporteService soporteService;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Test
        public void testCrearTicket_Exitoso() throws Exception {

                Soporte request = new Soporte();
                request.setIdUsuario(1L);
                request.setAsunto("Problema con el servicio");
                request.setDescripcion("No puedo acceder a mi cuenta");

                Soporte respuesta = new Soporte();
                respuesta.setIdUsuario(1L);
                respuesta.setAsunto("Problema con el servicio");
                respuesta.setDescripcion("No puedo acceder a mi cuenta");
                respuesta.setEstado(true);

                Mockito.when(soporteService.crearTicket(Mockito.any(Soporte.class)))
                                .thenReturn(respuesta);

                mockMvc.perform(post("/api/v1/soporte/crear")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.idUsuario").value(1L))
                                .andExpect(jsonPath("$.asunto").value("Problema con el servicio"))
                                .andExpect(jsonPath("$.estado").value(true));
        }

        @Test
        public void testCrearTicket_UsuarioNoEncontrado() throws Exception {
                Soporte request = new Soporte();
                request.setIdUsuario(999L);
                request.setAsunto("Problema con el servicio");
                request.setDescripcion("El usuario no existe");

                Mockito.when(soporteService.crearTicket(Mockito.any(Soporte.class)))
                                .thenThrow(new RuntimeException("Usuario no encontrado con id: 999"));

                mockMvc.perform(post("/api/v1/soporte")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                // ✅ 404 si aplicaste el fix del controller, o .isBadRequest() si no
                                .andExpect(status().isNotFound());
        }

        @Test
        public void testCrearTicket_ErrorGuardado() throws Exception {
                Soporte request = new Soporte();
                request.setIdUsuario(1L);
                request.setAsunto("Problema con el servicio");
                request.setDescripcion("Error al guardar");

                Mockito.when(soporteService.crearTicket(Mockito.any(Soporte.class)))
                                .thenThrow(new RuntimeException("Error al guardar en base de datos"));

                mockMvc.perform(post("/api/v1/soporte/crear")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void testObtenerTodos_Exitoso() throws Exception {
                Soporte s1 = new Soporte();
                s1.setIdUsuario(1L);
                s1.setAsunto("Ticket 1");
                s1.setEstado(true);

                Soporte s2 = new Soporte();
                s2.setIdUsuario(2L);
                s2.setAsunto("Ticket 2");
                s2.setEstado(false); // cerrado

                Mockito.when(soporteService.obtenerTodos())
                                .thenReturn(List.of(s1, s2));

                mockMvc.perform(get("/api/v1/soporte/listarT"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].asunto").value("Ticket 1"))
                                .andExpect(jsonPath("$[0].estado").value(true))
                                .andExpect(jsonPath("$[1].asunto").value("Ticket 2"))
                                .andExpect(jsonPath("$[1].estado").value(false)); // ticket cerrado
        }

        @Test
        public void testObtenerTodos_ListaVacia() throws Exception {
                Mockito.when(soporteService.obtenerTodos())
                                .thenReturn(List.of());

                mockMvc.perform(get("/api/v1/soporte/listarT"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        public void testObtenerTicket_Exitoso() throws Exception {
                Soporte soporte = new Soporte();
                soporte.setIdUsuario(1L);
                soporte.setAsunto("Ticket existente");
                soporte.setDescripcion("Descripción del ticket");
                soporte.setEstado(true);

                Mockito.when(soporteService.obtenerTicket(1L))
                                .thenReturn(soporte);

                mockMvc.perform(get("/api/v1/soporte/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.asunto").value("Ticket existente"))
                                .andExpect(jsonPath("$.estado").value(true));
        }

        @Test
        public void testObtenerTicket_NoEncontrado() throws Exception {
                Mockito.when(soporteService.obtenerTicket(999L))
                                .thenThrow(new RuntimeException("Ticket no encontrado con id: 999"));

                mockMvc.perform(get("/api/v1/soporte/999"))
                                .andExpect(status().isNotFound());
        }

        @Test
        public void testActualizarTicket_Exitoso() throws Exception {
                // El service actualiza asunto, descripcion Y estado
                Soporte request = new Soporte();
                request.setAsunto("Asunto actualizado");
                request.setDescripcion("Descripción actualizada");
                request.setEstado(true);

                Soporte actualizado = new Soporte();
                actualizado.setIdUsuario(1L);
                actualizado.setAsunto("Asunto actualizado");
                actualizado.setDescripcion("Descripción actualizada");
                actualizado.setEstado(true);

                Mockito.when(soporteService.actualizarTicket(Mockito.eq(1L), Mockito.any(Soporte.class)))
                                .thenReturn(actualizado);

                mockMvc.perform(put("/api/v1/soporte/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.asunto").value("Asunto actualizado"))
                                .andExpect(jsonPath("$.descripcion").value("Descripción actualizada"))
                                .andExpect(jsonPath("$.estado").value(true));
        }

        @Test
        public void testActualizarTicket_NoEncontrado() throws Exception {
                Soporte request = new Soporte();
                request.setAsunto("Asunto actualizado");

                Mockito.when(soporteService.actualizarTicket(Mockito.eq(999L), Mockito.any(Soporte.class)))
                                .thenThrow(new RuntimeException("Ticket no encontrado con id: 999"));

                mockMvc.perform(put("/api/v1/soporte/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound());
        }

        @Test
        public void testCerrarTicket_Exitoso() throws Exception {

                Soporte cerrado = new Soporte();
                cerrado.setIdUsuario(1L);
                cerrado.setAsunto("Ticket a cerrar");
                cerrado.setEstado(false);

                Mockito.when(soporteService.cerrarTicket(1L))
                                .thenReturn(cerrado);

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
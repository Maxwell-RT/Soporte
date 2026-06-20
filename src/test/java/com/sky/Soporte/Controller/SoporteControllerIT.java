package com.sky.Soporte.Controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.Soporte.model.Soporte;
import com.sky.Soporte.service.SoporteService; // mockea el service, no el repo

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SoporteControllerIT {

        @Autowired
        private MockMvc mockMvc;

        @Mock
        private SoporteService soporteService;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Test
        public void testCrearTicket_Exitoso() throws Exception {
                Soporte soporte = new Soporte();
                soporte.setIdUsuario(1L);
                soporte.setAsunto("Problema con el servicio");
                soporte.setDescripcion("No puedo acceder a mi cuenta");
                soporte.setEstado(true); // el service lo crea con estado=true (abierto)

                Mockito.when(soporteService.crearTicket(Mockito.any(Soporte.class)))
                                .thenReturn(soporte);

                mockMvc.perform(post("/api/v1/soporte") // URL corregida
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(soporte)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.idUsuario").value(1L))
                                .andExpect(jsonPath("$.asunto").value("Problema con el servicio"))
                                .andExpect(jsonPath("$.descripcion").value("No puedo acceder a mi cuenta"))
                                .andExpect(jsonPath("$.estado").value(true)); // abierto al crear
        }

        // Fallo del service → 400 BAD_REQUEST (Mismo problema una y otra vez)
        @Test
        public void testCrearTicket_Fallo() throws Exception {
                Soporte soporte = new Soporte();
                soporte.setIdUsuario(1L);
                soporte.setAsunto("Problema con el servicio");
                soporte.setDescripcion("No puedo acceder a mi cuenta");

                Mockito.when(soporteService.crearTicket(Mockito.any(Soporte.class)))
                                .thenThrow(new RuntimeException("Error al guardar"));

                mockMvc.perform(post("/api/v1/soporte")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(soporte)))
                                .andExpect(status().isBadRequest()); // controller lanza BAD_REQUEST
        }

        // Usuario inexistente → el service lanza RuntimeException → 400 BAD_REQUEST
        @Test
        public void testCrearTicket_UsuarioNoExiste() throws Exception {
                Soporte soporte = new Soporte();
                soporte.setIdUsuario(999L);
                soporte.setAsunto("Problema con el servicio");
                soporte.setDescripcion("El usuario no existe en el sistema");

                Mockito.when(soporteService.crearTicket(Mockito.any(Soporte.class)))
                                .thenThrow(new RuntimeException("Usuario no encontrado con id: 999"));

                mockMvc.perform(post("/api/v1/soporte")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(soporte)))
                                .andExpect(status().isBadRequest());
        }

        // Servicio externo no disponible → RuntimeException → 400 BAD_REQUEST
        @Test
        public void testCrearTicket_ServicioNoDisponible() throws Exception {
                Soporte soporte = new Soporte();
                soporte.setIdUsuario(1L);
                soporte.setAsunto("Problema con el servicio");
                soporte.setDescripcion("Servicio no disponible");

                Mockito.when(soporteService.crearTicket(Mockito.any(Soporte.class)))
                                .thenThrow(new RuntimeException("Servicio no disponible"));

                mockMvc.perform(post("/api/v1/soporte")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(soporte)))
                                .andExpect(status().isBadRequest());
        }

        // Error desconocido → RuntimeException → 400 BAD_REQUEST
        @Test
        public void testCrearTicket_ErrorDesconocido() throws Exception {
                Soporte soporte = new Soporte();
                soporte.setIdUsuario(1L);
                soporte.setAsunto("Problema con el servicio");
                soporte.setDescripcion("Error desconocido");

                Mockito.when(soporteService.crearTicket(Mockito.any(Soporte.class)))
                                .thenThrow(new RuntimeException("Error desconocido"));

                mockMvc.perform(post("/api/v1/soporte")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(soporte)))
                                .andExpect(status().isBadRequest());
        }
}
package com.sky.Soporte.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.sky.Soporte.model.Soporte;
import com.sky.Soporte.repository.SoporteRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SoporteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SoporteRepository soporteRepository;

    @BeforeEach
    public void setup() {
        Mockito.reset(soporteRepository);
    }

    @Test
    public void testCrearTicket() throws Exception {
        Soporte soporte = new Soporte();
        soporte.setIdUsuario(1L);
        soporte.setAsunto("Problema con el servicio");
        soporte.setDescripcion("No puedo acceder a mi cuenta");
        soporte.setEstado(false);

        Mockito.when(soporteRepository.save(Mockito.any(Soporte.class))).thenReturn(soporte);

        mockMvc.perform(post("/api/v1/Soporte/crear_ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(soporte)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.asunto").value("Problema con el servicio"))
                .andExpect(jsonPath("$.descripcion").value("No puedo acceder a mi cuenta"))
                .andExpect(jsonPath("$.estado").value(false));
    }

    @Test
    public void testCrearTicket_Fallo() throws Exception {
        Soporte soporte = new Soporte();
        soporte.setIdUsuario(1L);
        soporte.setAsunto("Problema con el servicio");
        soporte.setDescripcion("No puedo acceder a mi cuenta");
        soporte.setEstado(false);

        Mockito.when(soporteRepository.save(Mockito.any(Soporte.class)))
                .thenThrow(new RuntimeException("Error al guardar"));

        mockMvc.perform(post("/api/v1/Soporte/crear_ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(soporte)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testCrearTicket_DatosInvalidos() throws Exception {
        Soporte soporte = new Soporte();
        soporte.setIdUsuario(1L);
        soporte.setAsunto(""); // Asunto y descripcion son de caracter obligatorio, sin embargo se dejaran
                               // vacíos para simular datos inválidos
        soporte.setDescripcion("");
        soporte.setEstado(false);

        mockMvc.perform(post("/api/v1/Soporte/crear_ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(soporte)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCrearTicket_ServicioNoDisponible() throws Exception {
        Soporte soporte = new Soporte();
        soporte.setIdUsuario(1L);
        soporte.setAsunto("Problema con el servicio");
        soporte.setDescripcion("No puedo acceder a mi cuenta");
        soporte.setEstado(false);

        Mockito.when(soporteRepository.save(Mockito.any(Soporte.class)))
                .thenThrow(new RuntimeException("Servicio no disponible"));

        mockMvc.perform(post("/api/v1/Soporte/crear_ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(soporte)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testCrearTicket_UsuarioNoExiste() throws Exception {
        Soporte soporte = new Soporte();
        soporte.setIdUsuario(999L); // Usuario que no existe
        soporte.setAsunto("Problema con el servicio");
        soporte.setDescripcion("No puedo acceder a mi cuenta");
        soporte.setEstado(false);

        Mockito.when(soporteRepository.save(Mockito.any(Soporte.class)))
                .thenThrow(new RuntimeException("Usuario no existe"));

        mockMvc.perform(post("/api/v1/Soporte/crear_ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(soporte)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testCrearTicket_ErrorDesconocido() throws Exception {
        Soporte soporte = new Soporte();
        soporte.setIdUsuario(1L);
        soporte.setAsunto("Problema con el servicio");
        soporte.setDescripcion("No puedo acceder a mi cuenta");
        soporte.setEstado(false);

        Mockito.when(soporteRepository.save(Mockito.any(Soporte.class)))
                .thenThrow(new RuntimeException("Error desconocido"));

        mockMvc.perform(post("/api/v1/Soporte/crear_ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(soporte)))
                .andExpect(status().isInternalServerError());
    }
}
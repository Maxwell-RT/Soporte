package com.sky.Soporte.Controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.sky.Soporte.controller.SoporteController;
import com.sky.Soporte.model.Soporte;
import com.sky.Soporte.service.SoporteService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(SoporteController.class)
@ActiveProfiles("test")
public class SoporteControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SoporteService soporteService;


    private ObjectMapper objectMapper = new ObjectMapper();
    

    @Test
    public void testCrearTicket() throws Exception {
            Soporte soporte = new Soporte();
            soporte.setIdUsuario(1L);
            soporte.setAsunto("Problema con el servicio");
            soporte.setDescripcion("No puedo acceder a mi cuenta");
            soporte.setEstado(false);
    
            String json = objectMapper.writeValueAsString(soporte);
    
            mockMvc.perform(post("/api/v1/Soporte/crear_ticket")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
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

        String json = objectMapper.writeValueAsString(soporte);

        mockMvc.perform(post("/api/v1/Soporte/crear_ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isInternalServerError());
    }


    @Test
    public void actualizarTicket() throws Exception {
        Soporte soporte = new Soporte();
        soporte.setIdUsuario(1L);
        soporte.setAsunto("Problema con el servicio");
        soporte.setDescripcion("No puedo acceder a mi cuenta");
        soporte.setEstado(false);

        String json = objectMapper.writeValueAsString(soporte);

        mockMvc.perform(put("/api/v1/Soporte/ticket/guardar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.asunto").value("Problema con el servicio"))
                .andExpect(jsonPath("$.descripcion").value("No puedo acceder a mi cuenta"))
                .andExpect(jsonPath("$.estado").value(false));
    }



    @Test
    public void actualizarTicket_Fallo() throws Exception {
        Soporte soporte = new Soporte();
        soporte.setIdUsuario(1L);
        String json = objectMapper.writeValueAsString(soporte);

        mockMvc.perform(put("/api/v1/Soporte/ticket/guardar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testCrearTicket_DatosInvalidos() throws Exception {
        Soporte soporte = new Soporte();

        String json = objectMapper.writeValueAsString(soporte);

        mockMvc.perform(post("/api/v1/Soporte/crear_ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void EliminarTicket() throws Exception {
        mockMvc.perform(delete("/api/v1/Soporte/ticket/eliminar")
                .param("ticketid", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void EliminarTicket_Fallo() throws Exception {
        mockMvc.perform(delete("/api/v1/Soporte/ticket/eliminar")
                .param("ticketid", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void ObtenerTicket() throws Exception {
        mockMvc.perform(get("/api/v1/Soporte/ticket")
                .param("ticketid", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.asunto").value("Problema con el servicio"))
                .andExpect(jsonPath("$.descripcion").value("No puedo acceder a mi cuenta"))
                .andExpect(jsonPath("$.estado").value(false));
    }


    @Test
    public void ObtenerTicket_Fallo() throws Exception {
        mockMvc.perform(get("/api/v1/Soporte/ticket")
                .param("ticketid", "999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void ObtenerTodosTickets() throws Exception {
        mockMvc.perform(get("/api/v1/Soporte/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(1L))
                .andExpect(jsonPath("$[0].asunto").value("Problema con el servicio"))
                .andExpect(jsonPath("$[0].descripcion").value("No puedo acceder a mi cuenta"))
                .andExpect(jsonPath("$[0].estado").value(false));
    }   


        @Test
        public void guardartickets() throws Exception {
        Soporte soporte = new Soporte();
        soporte.setIdUsuario(1L);
        soporte.setAsunto("Problema con el servicio");
        soporte.setDescripcion("No puedo acceder a mi cuenta");
        soporte.setEstado(false);

        String json = objectMapper.writeValueAsString(soporte);

        mockMvc.perform(put("/api/v1/Soporte/ticket/guardar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.asunto").value("Problema con el servicio"))
                .andExpect(jsonPath("$.descripcion").value("No puedo acceder a mi cuenta"))
                .andExpect(jsonPath("$.estado").value(false));
    }



    //Abrir y cerrar tickets (VVVV Pruebas individuales aqui abajo VVVV)

    @Test
    public void AbrirTicket() throws Exception {
        Soporte soporte = new Soporte();
        soporte.setIdUsuario(1L);
        soporte.setAsunto("Problema con el servicio");
        soporte.setDescripcion("No puedo acceder a mi cuenta");
        soporte.setEstado(false);

        String json = objectMapper.writeValueAsString(soporte);

        mockMvc.perform(post("/api/v1/Soporte/crear_ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.asunto").value("Problema con el servicio"))
                .andExpect(jsonPath("$.descripcion").value("No puedo acceder a mi cuenta"))
                .andExpect(jsonPath("$.estado").value(false));
    }

    @Test
    public void AbrirTicket_Fallo() throws Exception { 
        Soporte soporte = new Soporte();
        soporte.setIdUsuario(1L);
        String json = objectMapper.writeValueAsString(soporte);

        mockMvc.perform(post("/api/v1/Soporte/crear_ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isInternalServerError());
    }

    @Test 
    public void cerrarticket() throws Exception {
        mockMvc.perform(put("/api/v1/Soporte/ticket/cerrar")
                .param("ticketid", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void cerrarticket_Fallo() throws Exception {
        mockMvc.perform(put("/api/v1/Soporte/ticket/cerrar")
                .param("ticketid", "999"))
                .andExpect(status().isNotFound());
    }
}


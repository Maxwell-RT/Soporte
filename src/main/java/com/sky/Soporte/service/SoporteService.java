package com.sky.Soporte.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.sky.Soporte.model.Soporte;
import com.sky.Soporte.model.UsuarioDTO;
import com.sky.Soporte.repository.SoporteRepository;

@Service
public class SoporteService {

    @Autowired
    private SoporteRepository soporteRepository;

    @Autowired
    private RestTemplate restTemplate;

    // Verifica que el usuario existe antes de crear el ticket
    public Soporte crearTicket(Soporte soporte) {
        String url = "http://localhost:8083/api/v1/usuarios/" + soporte.getIdUsuario();

        try {
            UsuarioDTO usuario = restTemplate.getForObject(url, UsuarioDTO.class);
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado con id: " + soporte.getIdUsuario());
            }

        } catch (HttpClientErrorException.NotFound e) {
            // 404 específico — el usuario no existe
            throw new RuntimeException("Usuario no encontrado con id: " + soporte.getIdUsuario());

        } catch (HttpClientErrorException e) {
            // Otro error HTTP 4xx
            throw new RuntimeException("Error al validar usuario: " + e.getStatusCode());

        } catch (ResourceAccessException e) {

            throw new RuntimeException("Servicio de usuarios no disponible");
        }

        soporte.setEstado(true);
        return soporteRepository.save(soporte);
    }

    // Cambia el estado del ticket a cerrado (false) en el repositorio local
    public Soporte cerrarTicket(Long ticketId) {
        Soporte soporte = soporteRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado con id: " + ticketId));

        soporte.setEstado(false);
        return soporteRepository.save(soporte);
    }

    // Actualiza asunto y descripción de un ticket existente
    public Soporte actualizarTicket(Long ticketId, Soporte soporte) {
        Soporte existente = soporteRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado con id: " + ticketId));

        existente.setAsunto(soporte.getAsunto());
        existente.setDescripcion(soporte.getDescripcion());
        existente.setEstado(soporte.isEstado());

        return soporteRepository.save(existente);
    }

    public List<Soporte> obtenerTodos() {
        return soporteRepository.findAll();
    }

    public Soporte obtenerTicket(Long ticketId) {
        return soporteRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado con id: " + ticketId));
    }

    public void eliminarTicket(Long ticketId) {
        Soporte soporte = soporteRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado con id: " + ticketId));
        soporteRepository.delete(soporte);
    }

    public List<Soporte> obtenerPorUsuario(Long usuarioId) {
        return soporteRepository.findByIdUsuario(usuarioId);
    }
}
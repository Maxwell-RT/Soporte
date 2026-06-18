package com.sky.Soporte.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import com.sky.Soporte.model.Soporte;
import com.sky.Soporte.model.UsuarioDTO;
import com.sky.Soporte.repository.SoporteRepository;

@Service
public class SoporteService {

    @Autowired
    private SoporteRepository soporteRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String USUARIOS_URL = "http://localhost:8083/api/usuarios";

    // Esto verifica que el usuario existe antes de crear el ticket
    public Soporte crearTicket(Soporte soporte) {
        String url = USUARIOS_URL + "/" + soporte.getIdUsuario();

        try {
            UsuarioDTO usuario = restTemplate.getForObject(url, UsuarioDTO.class);
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado con id: " + soporte.getIdUsuario());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Usuario no encontrado con id: " + soporte.getIdUsuario());
        }

        soporte.setEstado(true); 
        return soporteRepository.save(soporte);
    }

    // Esto cambia el estado del ticket a cerrado (false) en el repositorio local
    public Soporte cerrarTicket(Long ticketId) {
        Soporte soporte = soporteRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado con id: " + ticketId));

        soporte.setEstado(false); // false = cerrado
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
}
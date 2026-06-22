package com.sky.Soporte.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sky.Soporte.model.Soporte;
import com.sky.Soporte.service.SoporteService;

@RestController
@RequestMapping("api/v1/soporte")
public class SoporteController {

    @Autowired
    private SoporteService soporteService;

    @PostMapping
    public ResponseEntity<Soporte> crearTicket(@RequestBody Soporte soporte) {
        try {
            Soporte nuevoSoporte = soporteService.crearTicket(soporte);
            return new ResponseEntity<>(nuevoSoporte, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Si el mensaje indica usuario no encontrado → 404, resto → 400
            if (e.getMessage() != null && e.getMessage().startsWith("Usuario no encontrado")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // GET - Obtener todos los tickets en la lista
    @GetMapping
    public ResponseEntity<List<Soporte>> obtenerTodos() {
        List<Soporte> tickets = soporteService.obtenerTodos();
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    // GET - Obtener ticket por ID
    @GetMapping("/{ticketId}")
    public ResponseEntity<Soporte> obtenerTicket(@PathVariable Long ticketId) {
        try {
            Soporte soporte = soporteService.obtenerTicket(ticketId);
            return new ResponseEntity<>(soporte, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // PUT - Actualizar ticket existente
    @PutMapping("/{ticketId}")
    public ResponseEntity<Soporte> actualizarTicket(
            @PathVariable Long ticketId,
            @RequestBody Soporte soporte) {
        try {
            Soporte actualizado = soporteService.actualizarTicket(ticketId, soporte);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // PUT - Cerrar ticket (estado = false)
    @PutMapping("/{ticketId}/cerrar")
    public ResponseEntity<Soporte> cerrarTicket(@PathVariable Long ticketId) {
        try {
            Soporte cerrado = soporteService.cerrarTicket(ticketId);
            return new ResponseEntity<>(cerrado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE - Eliminar ticket
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> eliminarTicket(@PathVariable Long ticketId) {
        try {
            soporteService.eliminarTicket(ticketId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
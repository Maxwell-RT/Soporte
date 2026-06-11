package com.sky.Soporte.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.Soporte.model.Soporte;
import com.sky.Soporte.repository.SoporteRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/v1/Soporte")
public class SoporteController {

    @Autowired
    private SoporteRepository soporteRepository;

    @PostMapping("/path")
    public ResponseEntity<Soporte> postSoporte(@RequestBody Soporte soporte) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/ticket")
    public ResponseEntity<Soporte> getTicket(@RequestParam Long ticketid) {
        Soporte soporte = soporteRepository.findById(ticketid).orElse(null);
        if (soporte != null) {
            return new ResponseEntity<>(soporte, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/ticket")
    public ResponseEntity<Soporte> crearTicket(@RequestBody Soporte soporte) {
        Soporte nuevoSoporte = soporteRepository.save(soporte);
        return new ResponseEntity<>(nuevoSoporte, HttpStatus.CREATED);

    }

    @GetMapping("/tickets")
    public ResponseEntity<Iterable<Soporte>> getAllTickets() {
        Iterable<Soporte> tickets = soporteRepository.findAll();
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @PostMapping("/ticket/actualizar")
    public ResponseEntity<Soporte> actualizarTicket(@RequestParam Long ticketid, @RequestBody Soporte soporte) {
        Soporte existente = soporteRepository.findById(ticketid).orElse(null);
        if (existente != null) {
            existente.setDescripcion(soporte.getDescripcion());
            existente.setEstado(soporte.isEstado());
            Soporte actualizado = soporteRepository.save(existente);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/ticket/eliminar")
    public ResponseEntity<Void> eliminarTicket(@RequestParam Long ticketid) {
        if (soporteRepository.existsById(ticketid)) {
            soporteRepository.deleteById(ticketid);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/ticket/cerrar")
    public ResponseEntity<Soporte> cerrarTicket(@RequestParam Long ticketid) {
        Soporte soporte = soporteRepository.findById(ticketid).orElse(null);
        if (soporte != null) {
            soporte.setEstado(true);
            Soporte cerrado = soporteRepository.save(soporte);
            return new ResponseEntity<>(cerrado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
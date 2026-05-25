package com.sky.Soporte.controller;
import com.sky.Soporte.service.SoporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.Soporte.model.Soporte;
import com.sky.Soporte.repository.SoporteRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.*;
import com.sky.Soporte.service.SoporteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("api/v1/Soporte")
public class SoporteController {

@Autowired
    private SoporteRepository soporteRepository;



    @PostMapping("path")
    public ResponseEntity<Soporte> postSoporte(@RequestBody Soporte soporte){

        return new ResponseEntity<>(HttpStatus.OK);
    }
    





}

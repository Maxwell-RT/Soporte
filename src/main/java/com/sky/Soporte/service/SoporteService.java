package com.sky.Soporte.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sky.Soporte.model.Soporte;
import com.sky.Soporte.model.UsuarioDTO;
import com.sky.Soporte.repository.SoporteRepository;
@Service
public class SoporteService {
@Autowired
private SoporteRepository soporteRepository;

@Autowired
private RestTemplate restTemplate;

public Soporte crearticket(Soporte soporte){
String url= "http://localhost:8083/api/usuarios" + soporte.getIdUsuario();
UsuarioDTO usuario = restTemplate.getForObject(url, UsuarioDTO.class);
return soporteRepository.save(soporte);


}
public void cerrarticket(Long ticketid){

    String url= "http://localhost:8083/api/usuarios"+"/"+ticketid+"/Cerrar";
    restTemplate.put(url, null);
}



}

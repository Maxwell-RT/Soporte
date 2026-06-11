package com.sky.Soporte.service;

import java.util.List;

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

public Soporte actualizarTicket(Long ticketid, Soporte soporte){
    String url= "http://localhost:8083/api/usuarios"+"/"+ticketid+"/Actualizar";
    restTemplate.put(url, null);
    return soporteRepository.save(soporte);

}


public Soporte guardar(){
    return soporteRepository.save(new Soporte());
}

public List<Soporte> obtenerTodos(){
    return soporteRepository.findAll();
}

public Soporte eliminarticket(Long ticketid){
    Soporte soporte = soporteRepository.findById(ticketid).orElse(null);
    if (soporte != null) {
        soporteRepository.delete(soporte);
    }
    return soporte;
}



}
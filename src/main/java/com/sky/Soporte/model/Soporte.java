package com.sky.Soporte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity

public class Soporte {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idSoporte;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable=false)
    private String asunto;
    
    @Column(nullable=false)
    private String descripcion;

    private boolean estado;

    public Object crearticket(Soporte soporte) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'crearticket'");
    }



}

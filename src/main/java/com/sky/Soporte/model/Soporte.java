package com.sky.Soporte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "soporte")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Soporte {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idSoporte;

    @Column(nullable = false)
    private Long idUsuario;

    @Column(nullable=false)
    private String asunto;
    
    @Column(nullable=false)
    private String descripcion;

    private boolean estado;

    @Column
    private String nombreUsuario;



}

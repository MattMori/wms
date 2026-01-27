package com.mori.wms.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_racks", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"rua", "predio", "indice_vertical"})
})
public class Rack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_etiqueta", unique = true, nullable = false)
    private String codigoEtiqueta; // O QR Code continua obrigatório (é a identidade dele)

    @Column(nullable = false)
    private Integer rua;

    @Column(nullable = false)
    private Integer rack;

    @Column(name = "indice_vertical", nullable = false)
    private Integer indiceVertical;
    @Column(name = "nome_nivel") 
    private String nomeNivel; 

    @Column(name = "tipo_local", nullable = false)
    private String tipoLocal; // "ARMAZENAGEM" ou "PICKING"

    @Column(name = "capacidade_max_caixas", nullable = false)
    private Integer capacidadeMaxCaixas;
}
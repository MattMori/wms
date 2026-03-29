package com.mori.wms.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_racks", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "rua", "predio", "indice_vertical" })
})
public class Rack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // O código da etiqueta é um identificador único para cada rack, facilitando a localização e o controle.
    @Column(name = "codigo_etiqueta", unique = true, nullable = false)
    private String codigoEtiqueta;
    // rua, rack e indiceVertical formam a localização física do rack dentro do armazém, permitindo uma organização eficiente e fácil acesso aos produtos armazenados.
    @Column(nullable = false)
    private Integer rua;

    @Column(nullable = false)
    private Integer rack;

    @Column(name = "indice_vertical", nullable = false)
    private Integer indiceVertical;
    // O nome do nível é uma descrição adicional que pode ajudar na identificação visual e na organização dos racks.
    @Column(name = "nome_nivel")
    private String nomeNivel;
    @Column(name = "tipo_local", nullable = false)
    private String tipoLocal; 

    @Column(name = "capacidade_maxima_caixas", nullable = false)
    private Integer capacidadeMaxCaixas;

    public Integer getCapacidadeMaxCaixas() {
        return capacidadeMaxCaixas;
    }

    public void setCapacidadeMaxCaixas(Integer capacidadeMaxCaixas) {
        this.capacidadeMaxCaixas = capacidadeMaxCaixas;
    }
}
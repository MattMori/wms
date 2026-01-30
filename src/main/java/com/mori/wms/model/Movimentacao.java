package com.mori.wms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_movimentacoes")
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private String tipo; // "ENTRADA" ou "SAIDA"

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "rack_id")
    private Rack rack;

    private Integer quantidade;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Construtor vazio (obrigatório pro Hibernate)
    public Movimentacao() {
    }

    public Movimentacao(String tipo, Produto produto, Rack rack, Integer quantidade, Usuario usuario) {
        this.dataHora = LocalDateTime.now();
        this.tipo = tipo;
        this.produto = produto;
        this.rack = rack;
        this.quantidade = quantidade;
        this.usuario = usuario;
    }

}
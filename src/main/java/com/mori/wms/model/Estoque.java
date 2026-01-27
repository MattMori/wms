package com.mori.wms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A MÁGICA ACONTECE AQUI:
    // Criamos um link direto com a tabela de Produtos
    @ManyToOne 
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    // Criamos um link direto com a tabela de Racks
    @ManyToOne
    @JoinColumn(name = "rack_id", nullable = false)
    private Rack rack;

    @Column(nullable = false)
    private Integer quantidade;

    // Opcionais importantes para WMS (futuro)
    private String lote; // Lote de fabricação
    
    @Column(name = "data_entrada")
    private LocalDateTime dataEntrada;

    // Isso faz a data ser preenchida sozinha na hora que cria
    @PrePersist
    public void prePersist() {
        this.dataEntrada = LocalDateTime.now();
    }
}
package com.mori.wms.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O SKU é um código único para cada produto, facilitando a identificação.
    @Column(nullable = false, unique = true)
    private String sku;
    // O nome do produto é essencial para a identificação e descrição do item.
    @Column(nullable = false)
    private String nome;
    // itensPorCaixa contem a quantidade de itens que cabem em uma caixa, o que é importante para o controle de estoque.
    @Column(name = "itens_por_caixa", nullable = false)
    private Integer itensPorCaixa;

}
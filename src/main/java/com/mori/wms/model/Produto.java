package com.mori.wms.model;

import jakarta.persistence.*; // Importa as anotações do Banco de Dados
import lombok.Data; // Importa o Lombok para não escrever Getters/Setters

@Data // O Lombok gera Getters, Setters e toString automaticamente
@Entity // Diz ao Spring: "Isso aqui é uma tabela no banco"
@Table(name = "tb_produtos") // O nome exato da tabela no Postgres
public class Produto {

    @Id // Diz que este campo é a Chave Primária (PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Diz que é SERIAL (Auto-incremento)
    private Long id;

    @Column(nullable = false, unique = true) // Não pode ser nulo e não repete
    private String sku;

    @Column(nullable = false)
    private String nome;

    // Atenção: No Java é camelCase, no Banco é snake_case
    @Column(name = "itens_por_caixa", nullable = false)
    private Integer itensPorCaixa;

}
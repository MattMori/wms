package com.mori.wms.dto;

import lombok.Data;

@Data
public class TransferenciaDTO {
    private String sku;            // O que vai mover?
    private String etiquetaOrigem; // De onde sai? (Ex: R1-R6-0)
    private String etiquetaDestino;// Para onde vai? (Ex: R1-P1-0)
    private Integer quantidade;    // Quantos?
    private Long usuarioId;        // Quem está movendo?
}
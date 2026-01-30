package com.mori.wms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProdutoStatDTO {
    private String nomeProduto;
    private Long quantidadeTotal;
}
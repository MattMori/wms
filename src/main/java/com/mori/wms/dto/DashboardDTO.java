package com.mori.wms.dto;

import lombok.Data;
import java.util.List;

@Data
public class DashboardDTO {
    // Visão Macro (Armazém Inteiro)
    private Long capacidadeTotalDoArmazem; // Quantas caixas cabem no prédio todo?
    private Long totalCaixasEmEstoque; // Quantas caixas temos hoje?
    private Double porcentagemOcupacao; // % de lotação (Crítico!)

    // Os "Campeões" (Em Caixas)
    private List<ProdutoStatDTO> top5MaisEstocados;
    private List<ProdutoStatDTO> top5MenosEstocados;
}
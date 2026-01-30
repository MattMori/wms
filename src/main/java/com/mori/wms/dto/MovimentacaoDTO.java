package com.mori.wms.dto;

import lombok.Data;

@Data
public class MovimentacaoDTO {
    private String sku;
    private Long rackId;
    private String codigoEtiqueta;
    private Integer quantidade;
    private String lote;
    private Long usuarioId;
}
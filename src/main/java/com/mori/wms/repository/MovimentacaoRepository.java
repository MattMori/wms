package com.mori.wms.repository;

import com.mori.wms.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    // Buscar histórico de um produto específico
    List<Movimentacao> findByProdutoId(Long produtoId);
}
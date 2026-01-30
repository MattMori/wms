package com.mori.wms.repository;

import com.mori.wms.dto.ProdutoStatDTO;
import com.mori.wms.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

        // QUERY 1: Soma quantas CAIXAS reais existem (Quantidade / ItensPorCaixa)
        // COALESCE(p.itensPorCaixa, 1) serve para evitar divisão por zero se o cadastro
        // estiver ruim
        @Query("SELECT SUM(e.quantidade / COALESCE(p.itensPorCaixa, 1)) FROM Estoque e JOIN e.produto p")
        Long totalCaixasArmazenadas();

        // QUERY 2: Top 5 Produtos (Calculado em CAIXAS)
        @Query("SELECT new com.mori.wms.dto.ProdutoStatDTO(" +
                        "   e.produto.nome, " +
                        "   SUM(e.quantidade / COALESCE(e.produto.itensPorCaixa, 1))" + // A conversão mágica
                        ") " +
                        "FROM Estoque e " +
                        "GROUP BY e.produto.nome " +
                        "ORDER BY 2 DESC") // Ordena pelo 2º campo (a soma das caixas)
        List<ProdutoStatDTO> findTop5MaisEstocadosEmCaixas();

        // QUERY 3: Top 5 Menos Estocados (Calculado em CAIXAS)
        @Query("SELECT new com.mori.wms.dto.ProdutoStatDTO(" +
                        "   e.produto.nome, " +
                        "   SUM(e.quantidade / COALESCE(e.produto.itensPorCaixa, 1))" +
                        ") " +
                        "FROM Estoque e " +
                        "GROUP BY e.produto.nome " +
                        "ORDER BY 2 ASC")
        List<ProdutoStatDTO> findTop5MenosEstocadosEmCaixas();

        List<Estoque> findByRackId(Long rackId);
}
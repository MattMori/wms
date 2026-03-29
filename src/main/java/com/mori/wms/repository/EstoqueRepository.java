package com.mori.wms.repository;

import com.mori.wms.dto.ProdutoStatDTO;
import com.mori.wms.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

       
        @Query("SELECT SUM(e.quantidade / COALESCE(p.itensPorCaixa, 1)) FROM Estoque e JOIN e.produto p")
        Long totalCaixasArmazenadas();

        @Query("SELECT new com.mori.wms.dto.ProdutoStatDTO(" +
                        "   e.produto.nome, " +
                        "   SUM(e.quantidade / COALESCE(e.produto.itensPorCaixa, 1))" +
                        ") " +
                        "FROM Estoque e " +
                        "GROUP BY e.produto.nome " +
                        "ORDER BY 2 DESC")
        List<ProdutoStatDTO> findTop5MaisEstocadosEmCaixas();

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
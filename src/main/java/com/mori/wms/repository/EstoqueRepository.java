package com.mori.wms.repository;

import com.mori.wms.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    
    // Busca tudo que está num Rack específico
    // O Spring entende "RackId" e busca dentro do objeto Rack automaticamente
    List<Estoque> findByRackId(Long rackId);

    // Busca onde está um produto específico (Ex: Onde tem Mouse?)
    List<Estoque> findByProdutoId(Long produtoId);
}
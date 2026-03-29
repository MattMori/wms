package com.mori.wms.repository;

import com.mori.wms.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; 

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // Método para encontrar um produto pelo seu SKU, que é um identificador único.
    Optional<Produto> findBySku(String sku);

    
}
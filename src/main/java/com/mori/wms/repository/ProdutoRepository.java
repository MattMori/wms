package com.mori.wms.repository;

import com.mori.wms.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; 

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    // O Spring cria o SQL sozinho: SELECT * FROM produtos WHERE sku = ?
    Optional<Produto> findBySku(String sku);

    
}
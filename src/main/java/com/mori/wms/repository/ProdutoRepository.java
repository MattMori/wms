package com.mori.wms.repository;

import com.mori.wms.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// É uma interface (contrato). O Spring cria o código real sozinho em tempo de execução.
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // Só de estender JpaRepository, você já ganha:
    // .save() -> Salvar
    // .findAll() -> Listar tudo
    // .findById() -> Buscar um
    // .delete() -> Apagar
}
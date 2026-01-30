package com.mori.wms.controller;

import com.mori.wms.model.Produto;
import com.mori.wms.repository.ProdutoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(name = "1. Gestão de Produtos", description = "Catálogo mestre de SKUs e volumetria")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    // POST: Criar
    @PostMapping
    @Operation(summary = "Cadastrar novo SKU", description = "Cria um produto. 'itensPorCaixa' é obrigatório para o dashboard.")
    public ResponseEntity<?> criar(@RequestBody Produto produto) {
        // Validação Manual Simples
        if (produtoRepository.findBySku(produto.getSku()).isPresent()) {
            return ResponseEntity.badRequest().body("Erro: Já existe um produto com este SKU!");
        }
        if (produto.getItensPorCaixa() == null || produto.getItensPorCaixa() <= 0) {
            return ResponseEntity.badRequest().body("Erro: 'itensPorCaixa' deve ser maior que 0.");
        }

        return ResponseEntity.ok(produtoRepository.save(produto));
    }

    // PUT: Atualizar (Essencial para corrigir erros de cadastro)
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar Produto", description = "Permite corrigir nome ou quantidade por caixa.")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Produto produtoAtualizado) {
        return produtoRepository.findById(id)
                .map(produtoExistente -> {
                    // Atualiza apenas o que pode mudar (SKU geralmente não muda)
                    produtoExistente.setNome(produtoAtualizado.getNome());

                    // Se mudar a caixa, valida de novo
                    if (produtoAtualizado.getItensPorCaixa() != null && produtoAtualizado.getItensPorCaixa() > 0) {
                        produtoExistente.setItensPorCaixa(produtoAtualizado.getItensPorCaixa());
                    }

                    produtoRepository.save(produtoExistente);
                    return ResponseEntity.ok(produtoExistente);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // GET: Listar
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        return ResponseEntity.ok(produtoRepository.findAll());
    }

    // GET: Por ID
    @GetMapping("/{id}")
    public ResponseEntity<Produto> obterPorId(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE: Com proteção
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar SKU", description = "Cuidado: Só deleta se não tiver movimentação atrelada.")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            produtoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Captura o erro do banco de dados (Foreign Key Constraint)
            return ResponseEntity.badRequest().body(
                    "Erro: Não é possível deletar este produto pois ele possui histórico de estoque ou movimentações.");
        }
    }
}   
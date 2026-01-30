package com.mori.wms.controller;

import com.mori.wms.dto.MovimentacaoDTO;
import com.mori.wms.model.Estoque;
import com.mori.wms.model.Movimentacao;
import com.mori.wms.model.Produto;
import com.mori.wms.model.Rack;
import com.mori.wms.model.Usuario;
import com.mori.wms.repository.EstoqueRepository;
import com.mori.wms.repository.ProdutoRepository;
import com.mori.wms.repository.RackRepository;
import com.mori.wms.repository.UsuarioRepository;
import com.mori.wms.repository.MovimentacaoRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
@RequiredArgsConstructor
@Tag(name = "Operação Logística", description = "Entrada, Saída e Conferência")
public class EstoqueController {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RackRepository rackRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    // ==========================================
    // 1. ENTRADA (INBOUND)
    // ==========================================
    @PostMapping("/entrada")
    public ResponseEntity<String> adicionarEstoque(@RequestBody MovimentacaoDTO dto) {

        // 1. Validar Usuário (AQUI ESTAVA O ERRO: Faltava buscar o operador)
        Usuario operador = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado! ID inválido."));

        // 2. Validar Produto
        Produto produto = produtoRepository.findBySku(dto.getSku())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado! SKU inválido: " + dto.getSku()));

        // 3. Validar Rack
        Rack rack;
        if (dto.getCodigoEtiqueta() != null && !dto.getCodigoEtiqueta().isEmpty()) {
            rack = rackRepository.findByCodigoEtiqueta(dto.getCodigoEtiqueta())
                    .orElseThrow(() -> new RuntimeException("Etiqueta não encontrada: " + dto.getCodigoEtiqueta()));
        } else if (dto.getRackId() != null) {
            rack = rackRepository.findById(dto.getRackId())
                    .orElseThrow(() -> new RuntimeException("Rack ID não encontrado!"));
        } else {
            throw new RuntimeException("Erro: Informe a Etiqueta do Rack ou o ID!");
        }

        // 4. Salvar Estoque
        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setRack(rack);
        estoque.setQuantidade(dto.getQuantidade());
        estoque.setLote(dto.getLote());

        estoqueRepository.save(estoque);

        // 5. Gravar Histórico (Agora a variável 'operador' existe!)
        Movimentacao log = new Movimentacao("ENTRADA", produto, rack, dto.getQuantidade(), operador);
        movimentacaoRepository.save(log);

        return ResponseEntity.ok("Sucesso! " + produto.getNome() + " guardado em: " + rack.getCodigoEtiqueta());
    }

    // ==========================================
    // 2. CONSULTAS (GET)
    // ==========================================

    @GetMapping("/rack/{rackId}")
    public ResponseEntity<List<Estoque>> verConteudoDoRack(@PathVariable Long rackId) {
        return ResponseEntity.ok(estoqueRepository.findByRackId(rackId));
    }

    @GetMapping("/bip-rack/{etiqueta}")
    public ResponseEntity<List<Estoque>> consultarPorBip(@PathVariable String etiqueta) {
        Rack rack = rackRepository.findByCodigoEtiqueta(etiqueta)
                .orElseThrow(() -> new RuntimeException("Etiqueta de Rack não encontrada!"));
        return ResponseEntity.ok(estoqueRepository.findByRackId(rack.getId()));
    }

    // ==========================================
    // 3. SAÍDA (BAIXA)
    // ==========================================
    @PostMapping("/saida")
    public ResponseEntity<String> registrarSaida(@RequestBody MovimentacaoDTO dto) {

        // 1. Validar Usuário (AQUI TAMBÉM FALTAVA)
        Usuario operador = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        // 2. Validar Produto
        Produto produto = produtoRepository.findBySku(dto.getSku())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + dto.getSku()));

        // 3. Validar Rack (Precisamos do objeto Rack para o histórico)
        Rack rack;
        if (dto.getCodigoEtiqueta() != null && !dto.getCodigoEtiqueta().isEmpty()) {
            rack = rackRepository.findByCodigoEtiqueta(dto.getCodigoEtiqueta())
                    .orElseThrow(() -> new RuntimeException("Etiqueta não encontrada!"));
        } else {
            rack = rackRepository.findById(dto.getRackId())
                    .orElseThrow(() -> new RuntimeException("Rack ID não encontrado!"));
        }

        // 4. Buscar Estoque Específico
        List<Estoque> estoques = estoqueRepository.findByRackId(rack.getId());

        Estoque estoqueAlvo = estoques.stream()
                .filter(e -> e.getProduto().getId().equals(produto.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Esse produto não está neste rack!"));

        // 5. Gravar Histórico ANTES de deletar
        Movimentacao log = new Movimentacao("SAIDA", produto, rack, dto.getQuantidade(), operador);
        movimentacaoRepository.save(log);

        // 6. Lógica da Matemática
        int novaQuantidade = estoqueAlvo.getQuantidade() - dto.getQuantidade();

        if (novaQuantidade < 0) {
            return ResponseEntity.badRequest().body("Erro: Você tentou tirar mais do que tem!");
        } else if (novaQuantidade == 0) {
            estoqueRepository.delete(estoqueAlvo);
            return ResponseEntity.ok("Produto zerou e foi removido do rack.");
        } else {
            estoqueAlvo.setQuantidade(novaQuantidade);
            estoqueRepository.save(estoqueAlvo);
            return ResponseEntity.ok("Quantidade atualizada. Restam: " + novaQuantidade);
        }
    }
}
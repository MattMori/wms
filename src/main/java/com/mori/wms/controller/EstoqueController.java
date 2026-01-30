package com.mori.wms.controller;

import com.mori.wms.dto.MovimentacaoDTO;
import com.mori.wms.model.Estoque;
import com.mori.wms.model.Movimentacao;
import com.mori.wms.model.Produto;
import com.mori.wms.model.Rack;
import com.mori.wms.repository.EstoqueRepository;
import com.mori.wms.repository.ProdutoRepository;
import com.mori.wms.repository.RackRepository;
import com.mori.wms.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
@RequiredArgsConstructor
@Tag(name = "Gestão de Racks", description = "Criação de corredores, prateleiras e endereços físicos")

public class EstoqueController {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RackRepository rackRepository;
    private final com.mori.wms.repository.MovimentacaoRepository movimentacaoRepository;

    // POST: Entrada de Mercadoria (Agora via SKU)
    @PostMapping("/entrada")
    public ResponseEntity<String> adicionarEstoque(@RequestBody MovimentacaoDTO dto) {

        // 1. Validar Produto (Pelo SKU que o operador bipou)
        Produto produto = produtoRepository.findBySku(dto.getSku())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado! SKU inválido: " + dto.getSku()));

        // 2. Validar Rack
        Rack rack;

        if (dto.getCodigoEtiqueta() != null && !dto.getCodigoEtiqueta().isEmpty()) {
            rack = rackRepository.findByCodigoEtiqueta(dto.getCodigoEtiqueta())
                    .orElseThrow(
                            () -> new RuntimeException("Etiqueta de Rack não encontrada: " + dto.getCodigoEtiqueta()));
        } else if (dto.getRackId() != null) {
            rack = rackRepository.findById(dto.getRackId())
                    .orElseThrow(() -> new RuntimeException("Rack ID não encontrado!"));
        } else {
            throw new RuntimeException("Erro: Informe a Etiqueta do Rack ou o ID!");
        }

        // 3. Verifica se já existe esse produto nesse rack para somar (Opcional, mas
        // recomendado)

        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setRack(rack);
        estoque.setQuantidade(dto.getQuantidade());
        estoque.setLote(dto.getLote());

        estoqueRepository.save(estoque);
        Movimentacao log = new Movimentacao("ENTRADA", produto, rack, dto.getQuantidade(), operador);
        movimentacaoRepository.save(log);
        return ResponseEntity.ok("Sucesso! " + produto.getNome() + " guardado em: " + rack.getCodigoEtiqueta());
    }

    // GET: Ver o que tem dentro de um Rack pelo ID do Rack
    @GetMapping("/rack/{rackId}")
    public ResponseEntity<List<Estoque>> verConteudoDoRack(@PathVariable Long rackId) {
        return ResponseEntity.ok(estoqueRepository.findByRackId(rackId));
    }

    // 1. O "BIP" DO RACK (Consulta por etiqueta, não ID)
    @GetMapping("/bip-rack/{etiqueta}")
    public ResponseEntity<List<Estoque>> consultarPorBip(@PathVariable String etiqueta) {

        // Traduz a etiqueta (ex: "R1-RK5-0") para o objeto Rack
        Rack rack = rackRepository.findByCodigoEtiqueta(etiqueta)
                .orElseThrow(() -> new RuntimeException("Etiqueta de Rack não encontrada!"));

        // Retorna tudo que tem dentro desse ID recuperado
        return ResponseEntity.ok(estoqueRepository.findByRackId(rack.getId()));
    }

    // 2. A SAÍDA / BAIXA (Com lógica de apagar se zerar)
    @PostMapping("/saida")
    public ResponseEntity<String> registrarSaida(@RequestBody MovimentacaoDTO dto) {

        // Busca o produto pelo SKU
        Produto produto = produtoRepository.findBySku(dto.getSku())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + dto.getSku()));

        List<Estoque> estoques = estoqueRepository.findByRackId(dto.getRackId());

        // Filtra na memória qual linha tem o produto certo
        Estoque estoqueAlvo = estoques.stream()
                .filter(e -> e.getProduto().getId().equals(produto.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Esse produto não está neste rack!"));

        // Lógica da Matemática
        int novaQuantidade = estoqueAlvo.getQuantidade() - dto.getQuantidade();

        if (novaQuantidade < 0) {
            return ResponseEntity.badRequest().body("Erro: Você tentou tirar mais do que tem!");
        } else if (novaQuantidade == 0) {
            estoqueRepository.delete(estoqueAlvo);
            return ResponseEntity.ok("Produto zerou e foi removido do rack.");
        } else {
            // Se sobrou algo, apenas atualiza
            estoqueAlvo.setQuantidade(novaQuantidade);
            estoqueRepository.save(estoqueAlvo);

            Movimentacao log = new Movimentacao("SAIDA", produto, rack, dto.getQuantidade(), operador);
            movimentacaoRepository.save(log);
            return ResponseEntity.ok("Quantidade atualizada. Restam: " + novaQuantidade);
        }
    }

}
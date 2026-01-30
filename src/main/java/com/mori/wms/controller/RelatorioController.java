package com.mori.wms.controller;

import com.mori.wms.dto.DashboardDTO;
import com.mori.wms.dto.ProdutoStatDTO;
import com.mori.wms.repository.EstoqueRepository;
import com.mori.wms.repository.RackRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@Tag(name = "5. Relatórios Gerenciais", description = "KPIs Volumétricos (Caixas)")
public class RelatorioController {

    private final RackRepository rackRepository;
    private final EstoqueRepository estoqueRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard() {
        DashboardDTO kpi = new DashboardDTO();

        // 1. CAPACIDADE MÁXIMA (Teto)
        long capacidadeTotalCaixas = rackRepository.findAll().stream()
                .mapToLong(r -> r.getCapacidadeMaximaCaixas())
                .sum();

        // 2. ESTOQUE ATUAL (Chão)
        Long caixasAtuais = estoqueRepository.totalCaixasArmazenadas();
        if (caixasAtuais == null)
            caixasAtuais = 0L;

        // 3. CÁLCULO DE % OCUPAÇÃO
        double taxa = 0.0;
        if (capacidadeTotalCaixas > 0) {
            taxa = ((double) caixasAtuais / capacidadeTotalCaixas) * 100;
        }

        // 4. TOPS (Já vem em caixas do banco)
        List<ProdutoStatDTO> mais = estoqueRepository.findTop5MaisEstocadosEmCaixas()
                .stream().limit(5).collect(Collectors.toList());

        List<ProdutoStatDTO> menos = estoqueRepository.findTop5MenosEstocadosEmCaixas()
                .stream().limit(5).collect(Collectors.toList());

        kpi.setCapacidadeTotalDoArmazem(capacidadeTotalCaixas);
        kpi.setTotalCaixasEmEstoque(caixasAtuais);
        kpi.setPorcentagemOcupacao(Math.round(taxa * 100.0) / 100.0);
        kpi.setTop5MaisEstocados(mais);
        kpi.setTop5MenosEstocados(menos);

        return ResponseEntity.ok(kpi);
    }

    // EXTRA: Nível de Ocupação de UM Rack específico
    @GetMapping("/rack-ocupacao/{rackId}")
    public ResponseEntity<String> getOcupacaoRack(@PathVariable Long rackId) {
        var rack = rackRepository.findById(rackId)
                .orElseThrow(() -> new RuntimeException("Rack não encontrado"));

        // Busca tudo que tem nesse rack e converte pra caixas
        long caixasNoRack = estoqueRepository.findByRackId(rackId).stream()
                .mapToLong(e -> e.getQuantidade()
                        / (e.getProduto().getItensPorCaixa() > 0 ? e.getProduto().getItensPorCaixa() : 1))
                .sum();

        double porcentagem = ((double) caixasNoRack / rack.getCapacidadeMaximaCaixas()) * 100;

        return ResponseEntity.ok(String.format(
                "Rack: %s | Ocupação: %d/%d Caixas (%.1f%%)",
                rack.getCodigoEtiqueta(), caixasNoRack, rack.getCapacidadeMaximaCaixas(), porcentagem));
    }
}
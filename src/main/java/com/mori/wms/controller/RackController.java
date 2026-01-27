package com.mori.wms.controller;

import com.mori.wms.model.Rack;
import com.mori.wms.repository.RackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/racks")
@RequiredArgsConstructor
public class RackController {

    private final RackRepository rackRepository;

    // POST ESPECIAL: Cria o rack inteiro de uma vez
    @PostMapping("/batch")
    public ResponseEntity<String> criarRackCompleto(@RequestBody com.mori.wms.dto.RackCreationDTO dto) {

        // Loop do 0 até o total de andares (ex: 0 a 7)
        for (int i = 0; i < dto.getTotalAndares(); i++) {
            Rack rack = new Rack();
            rack.setRua(dto.getRua());
            rack.setRack(dto.getRack());
            rack.setIndiceVertical(i);

            // Lógica automática de nomes e tipos
            if (i == 0) {
                rack.setNomeNivel("TERRA");
                rack.setTipoLocal("ARMAZENAGEM");
                rack.setCapacidadeMaxCaixas(100);
            } else if (i == dto.getTotalAndares() - 1) {
                rack.setNomeNivel("CÉU");
                rack.setTipoLocal("ARMAZENAGEM");
                rack.setCapacidadeMaxCaixas(100

                );
            } else {
                // Todos os níveis do meio (1 a 6)
                rack.setNomeNivel(null); // Sem nome, só número
                rack.setTipoLocal("PICKING");
                rack.setCapacidadeMaxCaixas(0); // Picking não conta caixa fechada
            }

            // Gera a etiqueta:
            String etiqueta = String.format("R%d-R%d-%d", dto.getRua(), dto.getRack(), i);
            rack.setCodigoEtiqueta(etiqueta);

            rackRepository.save(rack); // Salva a linha no banco
        }

        return ResponseEntity.ok("Rack criado com sucesso! Foram gerados " + dto.getTotalAndares() + " endereços.");
    }

    // POST: Criar um endereço novo (Ex: A-01-TERRA)
    @PostMapping
    public ResponseEntity<Rack> criar(@RequestBody Rack rack) {
        return ResponseEntity.ok(rackRepository.save(rack));
    }

    // GET: Raio-X de um Rack específico (Traz do Chão ao Céu)
    @GetMapping("/{rua}/{rackNumero}")
    public ResponseEntity<List<Rack>> buscarRackFisico(
            @PathVariable Integer rua,
            @PathVariable Integer rackNumero) {

        List<Rack> andaresDoRack = rackRepository.findByRuaAndRack(rua, rackNumero);

        return ResponseEntity.ok(andaresDoRack);
    }

    // GET: Listar todo o mapa do armazém
    @GetMapping
    public ResponseEntity<List<Rack>> listarTodos() {
        return ResponseEntity.ok(rackRepository.findAll());
    }

    // GET: Listar racks por número da rua
    @GetMapping("/rua/{rua}")
    public ResponseEntity<List<Rack>> listarPorRua(@PathVariable Integer rua) {
        List<Rack> racksNaRua = rackRepository.findByRua(rua);
        return ResponseEntity.ok(racksNaRua);
    }

}
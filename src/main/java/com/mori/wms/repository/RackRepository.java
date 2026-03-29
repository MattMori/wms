package com.mori.wms.repository;

import com.mori.wms.model.Rack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; 

@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {
    // Método para encontrar racks por número da rua, facilitando a localização dentro do armazém.
    List<Rack> findByRua(Integer rua);
    Optional<Rack> findByCodigoEtiqueta(String codigoEtiqueta); 
    // Método para encontrar racks por número da rua e número do rack, permitindo uma busca mais específica.
    List<Rack> findByRuaAndRack(Integer rua, Integer rack);
}
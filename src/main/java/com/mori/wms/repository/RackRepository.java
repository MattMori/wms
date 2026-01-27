package com.mori.wms.repository;

import com.mori.wms.model.Rack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {
    
    // Método para encontrar racks por número da rua
    List<Rack> findByRua(Integer rua);

    List<Rack> findByRuaAndRack(Integer rua, Integer rack);
    
}
package com.mori.wms.repository;

import com.mori.wms.model.Rack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; 

@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {

    List<Rack> findByRua(Integer rua);
    
    Optional<Rack> findByCodigoEtiqueta(String codigoEtiqueta); 

    List<Rack> findByRuaAndRack(Integer rua, Integer rack);
}
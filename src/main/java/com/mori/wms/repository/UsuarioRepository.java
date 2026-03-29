package com.mori.wms.repository;

import com.mori.wms.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método para encontrar um usuário pelo número de matrícula.
    UserDetails findByMatricula(String matricula);
}
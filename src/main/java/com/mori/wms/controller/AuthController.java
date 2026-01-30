package com.mori.wms.controller;

import com.mori.wms.dto.LoginDTO;
import com.mori.wms.dto.RegisterDTO;
import com.mori.wms.model.Usuario;
import com.mori.wms.repository.UsuarioRepository;
import com.mori.wms.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Login e Registro de Usuários")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    @Operation(summary = "Entrar no Sistema", description = "Retorna um Token JWT para usar nas outras requisições.")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginDTO data) {
        // 1. Cria o token do Spring (não é o JWT ainda)
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.matricula(), data.senha());

        // 2. O Manager vai no banco, checa a senha criptografada e valida
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // 3. Se deu certo, gera o JWT
        var token = tokenService.gerarToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    @Operation(summary = "Criar Usuário", description = "Endpoint aberto para criar o primeiro usuário do sistema.")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        if (this.usuarioRepository.findByMatricula(data.matricula()) != null) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());

        Usuario newUser = new Usuario();
        newUser.setNome(data.nome());
        newUser.setMatricula(data.matricula());
        newUser.setSenha(encryptedPassword);

        this.usuarioRepository.save(newUser);

        return ResponseEntity.ok().build();
    }
}
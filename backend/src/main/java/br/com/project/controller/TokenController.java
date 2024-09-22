package br.com.project.controller;

import br.com.project.dto.LoginRequestDTO;
import br.com.project.dto.LoginResponseDTO;
import br.com.project.errors.StanderError;
import br.com.project.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@SecurityRequirements
@Schema(name = "Endpoints relacionados a autenticação")
public class TokenController {

    TokenService service;


    @PostMapping("/login")
    @Operation(summary = "Login", description = "Realiza login e retorna um token de autenticação", responses = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Credenciais inválidas", content = {@Content(schema = @Schema(implementation = StanderError.class))})
    })
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponseDTO = service.login(loginRequestDTO);
        return ResponseEntity.ok(loginResponseDTO);
    }
}

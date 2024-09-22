package br.com.project.controller;

import br.com.project.config.security.Scope;
import br.com.project.dto.LivroDTO;
import br.com.project.service.RecomendacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize(Scope.ADMIN)
@RequestMapping("recomendacao")
@Schema(name = "Endpoints para recomendação de livros")
public class RecomendacaoController {

    private final RecomendacaoService service;

    @GetMapping
    @Operation(summary = "Gera um lista de recomendações", description = "Gera uma lista de recomendações de livros baseado no histórico de empréstimos do usuário, caso o id do usuário seja informado, ou baseado no histórico de empréstimos de todos os usuários, caso o id do usuário não seja informado", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de recomendações gerada com sucesso")
    })
    public ResponseEntity<List<LivroDTO>> getRecomendacoes(@RequestParam(required = false) Long idUsuario) {
        List<LivroDTO> livroDTOList = service.getRecomendacoes(idUsuario);
        return new ResponseEntity<>(livroDTOList, HttpStatus.OK);
    }
}

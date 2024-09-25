package br.com.project.controller;

import br.com.project.config.security.Scope;
import br.com.project.dto.UsuarioDTO;
import br.com.project.entity.Usuario;
import br.com.project.errors.StanderError;
import br.com.project.service.UsuarioService;
import br.com.project.util.pesquisa.Pesquisa;
import br.com.project.util.pesquisa.PesquisaRequest;
import br.com.project.util.pesquisa.PesquisaResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("usuario")
@PreAuthorize(Scope.ADMIN)
@Schema(name = "Endpoints relacionados a usuarios")
public class UsuarioController {

    private final UsuarioService service;
    private final Pesquisa<Usuario> pesquisa;

    @GetMapping
    @Operation(summary = "Busca usuario por id", responses = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = {@Content(schema = @Schema(implementation = StanderError.class))})
    })
    public ResponseEntity<UsuarioDTO> getUsuarioById(@RequestParam Long id) {
        UsuarioDTO usuarioById = service.getUsuarioDTOById(id);
        return new ResponseEntity<>(usuarioById, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Cria usuario", responses = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
    })
    public ResponseEntity<UsuarioDTO> createUsuario(@RequestBody @Validated UsuarioDTO usuario) {
        UsuarioDTO createdUsuario = service.createUsuario(usuario);
        return new ResponseEntity<>(createdUsuario, HttpStatus.CREATED);
    }

    @PutMapping
    @Operation(summary = "Atualiza usuario", responses = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = {@Content(schema = @Schema(implementation = StanderError.class))})
    })
    public ResponseEntity<UsuarioDTO> updateUsuario(@RequestParam Long id, @RequestBody @Validated UsuarioDTO usuario) {
        UsuarioDTO updateUsuario = service.updateUsuario(id, usuario);
        return new ResponseEntity<>(updateUsuario, HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "Deleta usuario", responses = {
            @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não pode ser excluído, pois existem empréstimos vinculados a ele", content = {@Content(schema = @Schema(implementation = StanderError.class))}),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = {@Content(schema = @Schema(implementation = StanderError.class))})
    })
    public ResponseEntity<String> deleteUsuario(@RequestParam Long id) throws BadRequestException {
        String returnMessage = service.deleteUsuario(id);
        return new ResponseEntity<>(returnMessage, HttpStatus.OK);
    }

    @PostMapping("pesquisa")
    @Operation(summary = "Pesquisa usuario", responses = {
            @ApiResponse(responseCode = "200", description = "Pesquisa realizada com sucesso")
    })
    public PesquisaResult<UsuarioDTO> pesquisa(@RequestBody PesquisaRequest request) {
        PesquisaResult<Usuario> pesquisaResult = Objects.requireNonNull(pesquisa).pesquisar(request, Usuario.class);

        return PesquisaResult.<UsuarioDTO>builder()
                .pagina(pesquisaResult.getPagina())
                .totalRegistros(pesquisaResult.getTotalRegistros())
                .registros(pesquisaResult.getRegistros().stream().map(UsuarioDTO::fromEntity).toList())
                .build();
    }

    @GetMapping("verifica-exclusao")
    @Operation(summary = "Verifica se pode excluir usuario", responses = {
            @ApiResponse(responseCode = "200", description = "Usuário pode ser excluído"),
            @ApiResponse(responseCode = "400", description = "Usuário não pode ser excluído, pois existem empréstimos vinculados a ele", content = {@Content(schema = @Schema(implementation = StanderError.class))}),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = {@Content(schema = @Schema(implementation = StanderError.class))})
    })
    public ResponseEntity<Boolean> canDeleteUsuario(@RequestParam Long id) throws BadRequestException {
        service.canDeleteUsuario(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}

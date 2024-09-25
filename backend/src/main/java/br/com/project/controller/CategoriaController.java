package br.com.project.controller;

import br.com.project.config.security.Scope;
import br.com.project.dto.CategoriaDTO;
import br.com.project.entity.Categoria;
import br.com.project.errors.StanderError;
import br.com.project.service.CategoriaService;
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
@PreAuthorize(Scope.ADMIN)
@RequestMapping("categoria")
@Schema(name = "Endpoints relacionados a categorias")
public class CategoriaController {

    private final CategoriaService service;
    private final Pesquisa<Categoria> pesquisa;

    @GetMapping
    @Operation(summary = "Busca categoria por id", responses = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = {@Content(schema = @Schema(implementation = StanderError.class))})
    })
    public ResponseEntity<CategoriaDTO> getCategoriaById(@RequestParam Long id) {
        CategoriaDTO categoriaById = service.getCategoriaDTOById(id);
        return new ResponseEntity<>(categoriaById, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Cria categoria", responses = {
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso")
    })
    public ResponseEntity<CategoriaDTO> createCategoria(@RequestBody @Validated CategoriaDTO categoria) {
        CategoriaDTO createdCategoria = service.createCategoria(categoria);
        return new ResponseEntity<>(createdCategoria, HttpStatus.CREATED);
    }

    @PutMapping
    @Operation(summary = "Atualiza categoria", responses = {
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = {@Content(schema = @Schema(implementation = StanderError.class))})
    })
    public ResponseEntity<CategoriaDTO> updateCategoria(@RequestParam Long id, @RequestBody @Validated CategoriaDTO categoria) {
        CategoriaDTO updatedCategoria = service.updateCategoria(id, categoria);
        return new ResponseEntity<>(updatedCategoria, HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "Deleta categoria", responses = {
            @ApiResponse(responseCode = "200", description = "Categoria deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Categoria não pode ser excluída, pois existem livros vinculados a ela", content = {@Content(schema = @Schema(implementation = StanderError.class))}),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = {@Content(schema = @Schema(implementation = StanderError.class))})
    })
    public ResponseEntity<String> deleteCategoria(@RequestParam Long id) throws BadRequestException {
        String returnMessage = service.deleteCategoria(id);
        return new ResponseEntity<>(returnMessage, HttpStatus.OK);
    }

    @PostMapping("pesquisa")
    @Operation(summary = "Pesquisa categoria", responses = {
            @ApiResponse(responseCode = "200", description = "Pesquisa realizada com sucesso")
    })
    public PesquisaResult<CategoriaDTO> pesquisa(@RequestBody PesquisaRequest request) {
        PesquisaResult<Categoria> pesquisaResult = Objects.requireNonNull(pesquisa).pesquisar(request, Categoria.class);

        return PesquisaResult.<CategoriaDTO>builder()
                .pagina(pesquisaResult.getPagina())
                .totalRegistros(pesquisaResult.getTotalRegistros())
                .registros(pesquisaResult.getRegistros().stream().map(CategoriaDTO::fromEntity).toList())
                .build();
    }

    @GetMapping("verifica-exclusao")
    @Operation(summary = "Verifica se pode excluir categoria", responses = {
            @ApiResponse(responseCode = "200", description = "Categoria pode ser excluída"),
            @ApiResponse(responseCode = "400", description = "Categoria não pode ser excluída, pois existem livros vinculados a ela", content = {@Content(schema = @Schema(implementation = StanderError.class))}),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = {@Content(schema = @Schema(implementation = StanderError.class))})
    })

    public ResponseEntity<Boolean> canDeleteCategoria(@RequestParam Long id) throws BadRequestException {
        service.canDeleteCategoria(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

}

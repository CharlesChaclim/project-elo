package br.com.project.controller;

import br.com.project.config.security.Scope;
import br.com.project.dto.LivroDTO;
import br.com.project.entity.Livro;
import br.com.project.errors.StanderError;
import br.com.project.service.GoogleBooksService;
import br.com.project.service.LivroService;
import br.com.project.util.pesquisa.Pesquisa;
import br.com.project.util.pesquisa.PesquisaRequest;
import br.com.project.util.pesquisa.PesquisaResult;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("livro")
@PreAuthorize(Scope.ADMIN)
@Schema(name = "Endpoints relacionados a livros")
public class LivroController {

    private final LivroService service;
    private final Pesquisa<Livro> pesquisa;
    private final GoogleBooksService googleBooksService;

    @GetMapping
    @Operation(summary = "Busca livro por id", responses = {
            @ApiResponse(responseCode = "200", description = "Livro encontrado"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado", content = {@Content(schema = @Schema(implementation = StanderError.class))})
    })
    public ResponseEntity<LivroDTO> getLivroById(@RequestParam Long id) {
        LivroDTO categoriaById = service.getLivroDTOById(id);
        return new ResponseEntity<>(categoriaById, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Cria livro", responses = {
            @ApiResponse(responseCode = "201", description = "Livro criado com sucesso")
    })
    public ResponseEntity<LivroDTO> createLivro(@RequestBody @Validated LivroDTO categoria) {
        LivroDTO createdCategoria = service.createLivro(categoria);
        return new ResponseEntity<>(createdCategoria, HttpStatus.CREATED);
    }

    @PutMapping
    @Operation(summary = "Atualiza livro", responses = {
            @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado", content = {@Content(schema = @Schema(implementation = StanderError.class))})
    })
    public ResponseEntity<LivroDTO> updateLivro(@RequestParam Long id, @RequestBody @Validated LivroDTO livro) {
        LivroDTO updatedCategoria = service.updateLivro(id, livro);
        return new ResponseEntity<>(updatedCategoria, HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "Deleta livro", responses = {
            @ApiResponse(responseCode = "200", description = "Livro deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Livro não pode ser deletado, pois existem empréstimos vinculados a ele", content = {@Content(schema = @Schema(implementation = StanderError.class))}),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado", content = {@Content(schema = @Schema(implementation = StanderError.class))})
    })
    public ResponseEntity<String> deleteLivro(@RequestParam Long id) throws BadRequestException {
        String returnMessage = service.deleteLivro(id);
        return new ResponseEntity<>(returnMessage, HttpStatus.OK);
    }

    @PostMapping("pesquisa")
    @Operation(summary = "Pesquisa livro", responses = {
            @ApiResponse(responseCode = "200", description = "Pesquisa realizada com sucesso")
    })
    public PesquisaResult<LivroDTO> pesquisa(@RequestBody PesquisaRequest request) {
        PesquisaResult<Livro> pesquisaResult = Objects.requireNonNull(pesquisa).pesquisar(request, Livro.class);

        PesquisaResult<LivroDTO> pesquisaResultResponse = new PesquisaResult<>();
        pesquisaResultResponse.setRegistros(pesquisaResult.getRegistros().stream().map(LivroDTO::fromEntity).toList());
        pesquisaResultResponse.setPagina(pesquisaResult.getPagina());
        pesquisaResultResponse.setTotalRegistros(pesquisaResult.getTotalRegistros());
        return pesquisaResultResponse;
    }

    @GetMapping("verifica-exclusao")
    @Operation(summary = "Verifica se pode excluir livro", responses = {
            @ApiResponse(responseCode = "200", description = "Livro pode ser excluído"),
            @ApiResponse(responseCode = "400", description = "Livro não pode ser excluído, pois existem empréstimos vinculados a ele", content = {@Content(schema = @Schema(implementation = StanderError.class))}),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado", content = {@Content(schema = @Schema(implementation = StanderError.class))})
    })
    public ResponseEntity<Boolean> canDeleteLivro(@RequestParam Long id) throws BadRequestException {
        service.canDeleteLivro(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/api/buscar/livro")
    @Operation(summary = "Busca livro no Google Books pelo id do livro cadastrado no sistema",
            responses = {@ApiResponse(responseCode = "200", description = "Livro encontrado"),
                    @ApiResponse(responseCode = "404", description = "Livro não encontrado"),
                    @ApiResponse(responseCode = "500", description = "Erro ao buscar livro no Google Books")
            })
    public ResponseEntity<Volume> buscarLivrosPorId(@RequestParam Long idLivro) {
        Volume volume = googleBooksService.buscarLivroPorIdLivro(idLivro);
        return ResponseEntity.ok(volume);
    }

    @GetMapping("/api/buscar")
    @Operation(summary = "Busca livros por título",
            parameters = @Parameter(name = "titulo", example = "Harry Potter prisioneiro"),
            responses = {@ApiResponse(responseCode = "200", description = "Livros encontrados"),
                    @ApiResponse(responseCode = "500", description = "Erro ao buscar livros no Google Books")
    })
    public ResponseEntity<Volumes> buscarLivrosPorTitulo(@RequestParam String titulo) {
        Volumes volumes = googleBooksService.buscarLivrosPorTitulo(titulo);
        return ResponseEntity.ok(volumes);
    }

    @PostMapping("/api/adicionar")
    @Operation(summary = "Adiciona livro",
            parameters = {@Parameter(name = "idGoogleBooks", example = "9TcQCwAAQBAJ"), @Parameter(name = "idCategoria", example = "12")},
            responses = {@ApiResponse(responseCode = "200", description = "Livro adicionado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Erro ao adicionar livro"),
                    @ApiResponse(responseCode = "500", description = "Erro ao buscar livro no Google Books")
    })
    public ResponseEntity<LivroDTO> adicionarLivro(@RequestParam String idGoogleBooks, @RequestParam Long idCategoria) throws BadRequestException {
        LivroDTO livroAdicionado = googleBooksService.adicionarLivro(idGoogleBooks, idCategoria);
        return ResponseEntity.ok(livroAdicionado);
    }

}

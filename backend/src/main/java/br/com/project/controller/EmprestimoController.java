package br.com.project.controller;

import br.com.project.config.security.Scope;
import br.com.project.dto.EmprestimoDTO;
import br.com.project.dto.EmprestimoUpdateDTO;
import br.com.project.errors.StanderError;
import br.com.project.service.EmprestimoService;
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

@RestController
@RequiredArgsConstructor
@PreAuthorize(Scope.ADMIN)
@RequestMapping("emprestimo")
@Schema(name = "Endpoints relacionados a emprestimos")
public class EmprestimoController {

    private final EmprestimoService service;

    @PostMapping
    @Operation(summary = "Cria emprestimo", responses = {
            @ApiResponse(responseCode = "201", description = "Emprestimo criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Emprestimo não pode ser realizado, pois o livro está emprestado", content = {@Content(schema = @Schema(implementation = StanderError.class))})
    })
    public ResponseEntity<EmprestimoDTO> createEmprestimo(@RequestBody @Validated EmprestimoDTO emprestimo) throws BadRequestException {
        EmprestimoDTO createdEmprestimo = service.createEmprestimo(emprestimo);
        return new ResponseEntity<>(createdEmprestimo, HttpStatus.CREATED);
    }

    @PutMapping
    @Operation(summary = "Atualizador de data de devolução e status do emprestimo", responses = {
            @ApiResponse(responseCode = "200", description = "Emprestimo atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Emprestimo não encontrado", content = {@Content(schema = @Schema(implementation = StanderError.class))}),
            @ApiResponse(responseCode = "404", description = "Emprestimo não pode ser realizado, pois o livro está emprestado", content = {@Content(schema = @Schema(implementation = StanderError.class))})
    })
    public ResponseEntity<EmprestimoDTO> patchEmprestimo(@RequestParam Long id, @RequestBody @Validated EmprestimoUpdateDTO emprestimo) throws BadRequestException {
        EmprestimoDTO updatedEmprestimo = service.patchEmprestimo(id, emprestimo);
        return new ResponseEntity<>(updatedEmprestimo, HttpStatus.OK);
    }

}

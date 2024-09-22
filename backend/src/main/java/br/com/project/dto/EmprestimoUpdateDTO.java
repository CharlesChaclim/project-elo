package br.com.project.dto;

import br.com.project.anotations.valueOfEnum.ValueOfEnum;
import br.com.project.enumeration.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Emprestimo Update")
public class EmprestimoUpdateDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Identificador do empréstimo")
    private Long id;

    @NotBlank(message = "{not.blank}")
    @ValueOfEnum(enumClass = Status.class)
    @Schema(description = "Status do empréstimo", example = "ATIVO")
    private String status;

    @NotNull(message = "{not.blank}")
    @Schema(description = "Data de devolução do livro", example = "2021-12-31")
    private LocalDate dataDevolucao;


    public boolean isAtivo() {
        return Status.ATIVO.equals(Status.valueOf(status));
    }
}


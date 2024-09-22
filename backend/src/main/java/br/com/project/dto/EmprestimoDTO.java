package br.com.project.dto;

import br.com.project.anotations.valueOfEnum.ValueOfEnum;
import br.com.project.entity.Emprestimo;
import br.com.project.entity.Livro;
import br.com.project.entity.Usuario;
import br.com.project.enumeration.Status;
import br.com.project.util.MessageUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Emprestimo")
public class EmprestimoDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Identificador do empréstimo")
    private Long id;

    @NotNull(message = "{not.blank}")
    @Schema(description = "Identificador do livro", example = "1")
    private Long idLivro;

    @NotBlank(message = "{not.blank}")
    @ValueOfEnum(enumClass = Status.class)
    @Size(max = 20, message = "{size.max}")
    @Schema(description = "Status do empréstimo", example = "ATIVO")
    private String status;

    @NotNull(message = "{not.blank}")
    @Schema(description = "Identificador do usuário", example = "1")
    private Long idUsuario;

    @NotNull(message = "{not.blank}")
    @Schema(description = "Data de devolução do livro", example = "2021-12-31")
    private LocalDate dataDevolucao;

    @NotNull(message = "{not.blank}")
    @PastOrPresent(message = "{date.of.loan.not.in.future}")
    @Schema(description = "Data de empréstimo do livro", example = "2021-12-31")
    private LocalDate dataEmprestimo;

    private void checkDataDevolucaoAfterDataEmprestimo() {
        if (this.getDataDevolucao().isBefore(this.dataEmprestimo)) {
            throw new IllegalArgumentException(MessageUtil.get("erro.data.devolucao.invalida"));
        }
    }

    public static EmprestimoDTO fromEntity(Emprestimo emprestimo) {
        return EmprestimoDTO.builder()
                .id(emprestimo.getId())
                .idLivro(emprestimo.getLivro().getId())
                .status(emprestimo.getStatus().toString())
                .idUsuario(emprestimo.getUsuario().getId())
                .dataDevolucao(emprestimo.getDataDevolucao())
                .dataEmprestimo(emprestimo.getDataEmprestimo())
                .build();
    }

    public Emprestimo toEntity() {
        checkDataDevolucaoAfterDataEmprestimo();
        return Emprestimo.builder()
                .dataDevolucao(this.dataDevolucao)
                .dataEmprestimo(this.dataEmprestimo)
                .status(Status.valueOf(this.status))
                .livro(Livro.builder().id(this.idLivro).build())
                .usuario(Usuario.builder().id(this.idUsuario).build())
                .build();
    }

}


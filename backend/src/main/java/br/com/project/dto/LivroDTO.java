package br.com.project.dto;

import br.com.project.entity.Categoria;
import br.com.project.entity.Emprestimo;
import br.com.project.entity.Livro;
import com.google.api.services.books.model.Volume;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Livro")
public class LivroDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Identificador do livro")
    private Long id;

    @NotBlank(message = "{not.blank}")
    @Size(max = 20, message = "{size.max}")
    @Schema(description = "ISBN do livro", example = "978-3-16-148410-0")
    private String isbn;

    @NotBlank(message = "{not.blank}")
    @Size(max = 255, message = "{size.max}")
    @Schema(description = "Autor do livro", example = "João da Silva")
    private String autor;

    @NotBlank(message = "{not.blank}")
    @Size(max = 255, message = "{size.max}")
    @Schema(description = "Título do livro", example = "A volta dos que não foram")
    private String titulo;

    @NotNull(message = "{not.blank}")
    @Schema(description = "Identificador da categoria", example = "1")
    private Long idCategoria;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "nome da categoria")
    private String nomeCategoria;

    @NotNull(message = "{not.blank}")
    @Schema(description = "Data de publicação do livro", example = "2021-12-31")
    private LocalDate dataPublicacao;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Informações do empréstimo")
    private EmprestimoDTO emprestimo;

    public static LivroDTO fromEntity(Livro livro) {
        Optional<Emprestimo> optionalEmprestimo = Optional.empty();

        if(livro.getEmprestimos() != null && !livro.getEmprestimos().isEmpty()) {
            optionalEmprestimo = livro.getEmprestimos().stream().filter(Emprestimo::isAtivo).findFirst();
        }

        return LivroDTO.builder()
                .id(livro.getId())
                .isbn(livro.getIsbn())
                .autor(livro.getAutor())
                .titulo(livro.getTitulo())
                .dataPublicacao(livro.getDataPublicacao())
                .idCategoria(livro.getCategoria().getId())
                .nomeCategoria(livro.getCategoria().getNome())
                .emprestimo(optionalEmprestimo.map(EmprestimoDTO::fromEntity).orElse(null))
                .build();

    }

    public static LivroDTO fromVolume(Volume volume, Long idCategoria) {
        return LivroDTO.builder()
                .idCategoria(idCategoria)
                .titulo(volume.getVolumeInfo().getTitle())
                .autor(volume.getVolumeInfo().getAuthors().get(0))
                .dataPublicacao(LocalDate.parse(volume.getVolumeInfo().getPublishedDate()))
                .isbn(volume.getVolumeInfo().getIndustryIdentifiers().get(0).getIdentifier())
                .build();
    }

    public Livro toEntity() {
        return Livro.builder()
                .isbn(this.isbn)
                .autor(this.autor)
                .titulo(this.titulo)
                .dataPublicacao(this.dataPublicacao)
                .categoria(Categoria.builder().id(this.idCategoria).build())
                .build();
    }

}


package br.com.project.dto;

import br.com.project.entity.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Categoria")
public class CategoriaDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Identificador da categoria")
    private Long id;

    @NotBlank(message = "{not.blank}")
    @Size(max = 100, message = "{size.max}")
    @Schema(description = "Nome da categoria", example = "Romance")
    private String nome;

    public Categoria toEntity() {
        return Categoria.builder()
                .nome(this.nome)
                .build();
    }

    public static CategoriaDTO fromEntity(Categoria categoria) {
        return CategoriaDTO.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .build();

    }

}


package br.com.project.dto;

import br.com.project.entity.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Usuario")
public class UsuarioDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Identificador do usuário")
    private Long id;

    @NotBlank(message = "{not.blank}")
    @Size(max = 100, message = "{size.max}")
    @Schema(description = "Nome do usuário", example = "João da Silva")
    private String nome;

    @NotBlank(message = "{not.blank}")
    @Size(max = 20, message = "{size.max}")
    @Schema(description = "Telefone do usuário", example = "48999998888")
    private String telefone;

    @NotBlank(message = "{not.blank}")
    @Email(message = "{email.not.valid}")
    @Size(max = 255, message = "{size.max}")
    @Schema(description = "E-mail do usuário", example = "email@email.com.be")
    private String email;

    @NotNull(message = "{not.blank}")
    @PastOrPresent(message = "{date.of.registration.not.in.future}")
    @Schema(description = "Data de cadastro do usuário", example = "2021-12-31")
    private LocalDate dataCadastro;

    public Usuario toEntity() {
        return Usuario.builder()
                .nome(this.nome)
                .email(this.email)
                .telefone(this.telefone)
                .dataCadastro(this.dataCadastro)
                .build();
    }

    public static UsuarioDTO fromEntity(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .telefone(usuario.getTelefone())
                .dataCadastro(usuario.getDataCadastro())
                .build();
    }

}
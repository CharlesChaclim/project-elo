package br.com.project.util.pesquisa;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

enum Ordenacao {
    ASC, DESC
}

@Data
public class PesquisaOrdenacao {

    @Schema(example = "id")
    private String campo;

    @Schema(example = "ASC")
    private Ordenacao ordenacao;

}

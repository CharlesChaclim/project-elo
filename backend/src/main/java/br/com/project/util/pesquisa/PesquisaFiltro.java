package br.com.project.util.pesquisa;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PesquisaFiltro {

    @Schema(example = "id")
    private String campo;

    @Schema(example = "IGUAL")
    private Comparacao comparacao;

    @Schema(example = "1")
    private Object valor;

}
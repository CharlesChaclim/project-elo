package br.com.project.util.pesquisa;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class PesquisaRequest {

    @Schema(example = "1")
    private Integer pagina;

    @Schema(example = "10")
    private Integer quantidadeRegistros;

    private List<PesquisaFiltro> filtros;

    private List<PesquisaOrdenacao> ordenacao;

}

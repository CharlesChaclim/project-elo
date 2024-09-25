package br.com.project.util.pesquisa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PesquisaResult<T> {

    private int pagina;

    @JsonProperty("total_registros")
    private Long totalRegistros;

    private List<T> registros;

}

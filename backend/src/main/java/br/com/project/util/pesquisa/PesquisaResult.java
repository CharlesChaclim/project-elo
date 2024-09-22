package br.com.project.util.pesquisa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PesquisaResult<T> {

    private int pagina;

    @JsonProperty("total_registros")
    private Long totalRegistros;

    private List<T> registros;

}

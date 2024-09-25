package br.com.project.util.pesquisa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PesquisaResult<T> {

    private int pagina;

    @JsonProperty("total_registros")
    private Long totalRegistros;

    private List<T> registros;

}

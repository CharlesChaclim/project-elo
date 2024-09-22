package br.com.project.util.pesquisa;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResultadoBusca<T> {

    private List<T> registros;
    private Long totalRegistros;

    public ResultadoBusca(List<T> registros, Long totalRegistros) {
        this.registros = registros;
        this.totalRegistros = totalRegistros;
    }

}

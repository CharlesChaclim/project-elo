package br.com.project.service;

import br.com.project.dto.LivroDTO;
import br.com.project.entity.Livro;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RecomendacaoService {

    private final LivroService livroService;
    private final UsuarioService usuarioService;

    private List<Livro> findLivrosMaisEmprestadosExcluindo() {
        return livroService.findLivrosMaisEmprestados();
    }

    private List<Livro> findLivrosMaisEmprestadosByUsuarioExcluindoOsJaEmprestados(Long idUsuario) {
        usuarioService.getUsuarioById(idUsuario);
        return livroService.findLivrosMaisEmprestadosByUsuarioExcluindoOsJaEmprestados(idUsuario);
    }

    public List<LivroDTO> getRecomendacoes(Long idUsuario) {
        return Optional.ofNullable(idUsuario)
                .map(this::findLivrosMaisEmprestadosByUsuarioExcluindoOsJaEmprestados)
                .orElseGet(this::findLivrosMaisEmprestadosExcluindo)
                .stream().map(LivroDTO::fromEntity).toList();
    }
}

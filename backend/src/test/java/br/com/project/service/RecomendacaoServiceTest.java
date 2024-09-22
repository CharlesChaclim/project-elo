package br.com.project.service;

import br.com.project.dto.LivroDTO;
import br.com.project.entity.Categoria;
import br.com.project.entity.Livro;
import br.com.project.entity.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecomendacaoServiceTest {

    @Mock
    private LivroService livroService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private RecomendacaoService recomendacaoService;


    @Test
    @DisplayName("Deve retornar uma lista de livros mais emprestados")
    public void getRecomendacoesShouldReturnLivrosMaisEmprestadosExcluindoWhenIdUsuarioIsNull() {
        Livro livro = new Livro();
        livro.setId(1L);
        livro.setCategoria(Categoria.builder().id(1L).build());
        List<Livro> livros = Collections.singletonList(livro);

        when(livroService.findLivrosMaisEmprestados()).thenReturn(livros);

        List<LivroDTO> result = recomendacaoService.getRecomendacoes(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(livro.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("Deve retornar uma lista de livros mais emprestados por usuario excluindo os ja emprestados")
    public void getRecomendacoesShouldReturnLivrosMaisEmprestadosByUsuarioExcluindoOsJaEmprestadosWhenIdUsuarioIsNotNull() {
        Livro livro = new Livro();
        livro.setId(1L);
        livro.setCategoria(Categoria.builder().id(1L).build());
        List<Livro> livros = Collections.singletonList(livro);
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioService.getUsuarioById(anyLong())).thenReturn(usuario);
        when(livroService.findLivrosMaisEmprestadosByUsuarioExcluindoOsJaEmprestados(anyLong())).thenReturn(livros);

        List<LivroDTO> result = recomendacaoService.getRecomendacoes(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(livro.getId(), result.get(0).getId());
    }
}
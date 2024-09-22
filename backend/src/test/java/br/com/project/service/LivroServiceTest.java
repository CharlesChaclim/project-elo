package br.com.project.service;

import br.com.project.dto.LivroDTO;
import br.com.project.entity.Emprestimo;
import br.com.project.entity.Livro;
import br.com.project.entity.Categoria;
import br.com.project.exception.ObjectNotFoundException;
import br.com.project.repository.LivroRepository;
import br.com.project.util.MessageUtil;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class LivroServiceTest {

    @Mock
    private LivroRepository repository;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private LivroService service;

    private Livro livro;
    private LivroDTO livroDTO;

    @BeforeEach
    public void setup() {
        livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Livro Teste");
        livro.setCategoria(Categoria.builder().id(1L).build());

        livroDTO = new LivroDTO();
        livroDTO.setId(1L);
        livroDTO.setTitulo("Livro Teste");
        livroDTO.setIdCategoria(1L);
    }

    @Test
    @DisplayName("Deve retornar uma lista de livros")
    public void getLivroByIdShouldReturnLivroWhenFound() {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(livro));

        Livro result = service.getLivroById(1L);

        assertNotNull(result);
        assertEquals(livro.getId(), result.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando livro não for encontrado")
    public void getLivroByIdShouldThrowExceptionWhenNotFound() {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.getLivroById(1L));
    }

    @Test
    @DisplayName("Deve retornar uma lista de livros")
    public void getLivroDTOByIdShouldReturnLivroDTOWhenFound() {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(livro));

        LivroDTO result = service.getLivroDTOById(1L);

        assertNotNull(result);
        assertEquals(livroDTO.getId(), result.getId());
    }

    @Test
    @DisplayName("Deve criar um livro")
    public void createLivroShouldReturnCreatedLivroDTO() {
        Mockito.when(repository.save(any(Livro.class))).thenReturn(livro);
        Mockito.when(categoriaService.getCategoriaById(anyLong())).thenReturn(new Categoria());

        LivroDTO result = service.createLivro(livroDTO);

        assertNotNull(result);
        assertEquals(livroDTO.getId(), result.getId());
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateLivroShouldUpdateAllFieldsExceptId() {
        Livro updatedLivro = new Livro();
        updatedLivro.setId(1L);
        updatedLivro.setTitulo("Livro Atualizado");
        updatedLivro.setAutor("Autor Atualizado");
        updatedLivro.setIsbn("ISBN Atualizado");
        updatedLivro.setDataPublicacao(LocalDate.now());
        updatedLivro.setCategoria(Categoria.builder().id(1L).build());

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(livro));
        Mockito.when(repository.save(any(Livro.class))).thenReturn(updatedLivro);
        Mockito.when(categoriaService.getCategoriaById(anyLong())).thenReturn(new Categoria());

        LivroDTO updatedLivroDTO = new LivroDTO();
        updatedLivroDTO.setId(1L);
        updatedLivroDTO.setTitulo("Livro Atualizado");
        updatedLivroDTO.setAutor("Autor Atualizado");
        updatedLivroDTO.setIsbn("ISBN Atualizado");
        updatedLivroDTO.setDataPublicacao(LocalDate.now());
        updatedLivroDTO.setIdCategoria(1L);

        LivroDTO result = service.updateLivro(1L, updatedLivroDTO);

        assertNotNull(result);
        assertEquals(updatedLivroDTO.getId(), result.getId());
        assertEquals(updatedLivroDTO.getTitulo(), result.getTitulo());
        assertEquals(updatedLivroDTO.getAutor(), result.getAutor());
        assertEquals(updatedLivroDTO.getIsbn(), result.getIsbn());
        assertEquals(updatedLivroDTO.getDataPublicacao(), result.getDataPublicacao());
        assertEquals(updatedLivroDTO.getIdCategoria(), result.getIdCategoria());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando, não existem empréstimos para o livro")
    public void canDeleteLivroShouldNotThrowExceptionWhenNoEmprestimosExist() {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(livro));

        assertDoesNotThrow(() -> service.canDeleteLivro(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção quando, existem empréstimos para o livro")
    public void canDeleteLivroShouldThrowExceptionWhenEmprestimosExist() {
        livro.setEmprestimos(Collections.singletonList(new Emprestimo()));

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(livro));

        assertThrows(BadRequestException.class, () -> service.canDeleteLivro(1L));
    }

    @Test
    @DisplayName("Deve deletar um livro e retornar mensagem de sucesso")
    public void deleteLivroShouldReturnSuccessMessage() throws BadRequestException {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(livro));
        Mockito.doNothing().when(repository).deleteById(anyLong());

        String result = service.deleteLivro(1L);

        assertEquals(MessageUtil.get("livro.deleted"), result);
    }

    @Test
    @DisplayName("Deve retornar uma lista de livros")
    public void findLivrosMaisEmprestadosShouldReturnList() {
        List<Livro> livros = Collections.singletonList(livro);
        Mockito.when(repository.findLivrosMaisEmprestados()).thenReturn(livros);

        List<Livro> result = service.findLivrosMaisEmprestados();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve retornar uma lista de livros")
    public void findLivrosMaisEmprestadosByUsuarioExcluindoOsJaEmprestadosShouldReturnList() {
        List<Livro> livros = Collections.singletonList(livro);
        Mockito.when(repository.findLivrosMaisEmprestadosByUsuarioExcluindoOsJaEmprestados(anyLong())).thenReturn(livros);

        List<Livro> result = service.findLivrosMaisEmprestadosByUsuarioExcluindoOsJaEmprestados(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
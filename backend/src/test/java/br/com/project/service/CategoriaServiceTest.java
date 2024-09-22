package br.com.project.service;

import br.com.project.dto.CategoriaDTO;
import br.com.project.entity.Categoria;
import br.com.project.entity.Livro;
import br.com.project.exception.ObjectNotFoundException;
import br.com.project.repository.CategoriaRepository;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaService service;

    private Categoria categoria;
    private CategoriaDTO categoriaDTO;

    @BeforeEach
    public void setup() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Categoria Teste");
        categoria.setLivros(new ArrayList<>());

        categoriaDTO = new CategoriaDTO();
        categoriaDTO.setId(1L);
        categoriaDTO.setNome("Categoria Teste");
    }

    @Test
    @DisplayName("Deve retornar uma lista de categorias")
    public void testGetCategoriaById() {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(categoria));

        Categoria result = service.getCategoriaById(1L);

        assertNotNull(result);
        assertEquals(categoria.getId(), result.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar categoria por id, quando não encontrar")
    public void testGetCategoriaByIdNotFound() {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.getCategoriaById(1L));
    }

    @Test
    @DisplayName("Deve retornar uma lista de categoriasDTO")
    public void testGetCategoriaDTOById() {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(categoria));

        CategoriaDTO result = service.getCategoriaDTOById(1L);

        assertNotNull(result);
        assertEquals(categoriaDTO.getId(), result.getId());
    }

    @Test
    @DisplayName("Deve criar uma categoria")
    public void testCreateCategoria() {
        Mockito.when(repository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaDTO result = service.createCategoria(categoriaDTO);

        assertNotNull(result);
        assertEquals(categoriaDTO.getId(), result.getId());
    }

    @Test
    @DisplayName("Deve atualizar uma categoria")
    public void updateCategoriaShouldUpdateAllFieldsExceptId() {
        Categoria updatedCategoria = new Categoria();
        updatedCategoria.setId(1L);
        updatedCategoria.setNome("Categoria Atualizada");
        updatedCategoria.setLivros(new ArrayList<>());

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(categoria));
        Mockito.when(repository.save(any(Categoria.class))).thenReturn(updatedCategoria);

        CategoriaDTO updatedCategoriaDTO = new CategoriaDTO();
        updatedCategoriaDTO.setId(1L);
        updatedCategoriaDTO.setNome("Categoria Atualizada");

        CategoriaDTO result = service.updateCategoria(1L, updatedCategoriaDTO);

        assertNotNull(result);
        assertEquals(updatedCategoriaDTO.getId(), result.getId());
        assertEquals(updatedCategoriaDTO.getNome(), result.getNome());
        assertEquals(categoria.getId(), result.getId());
    }

    @Test
    @DisplayName("Não deve lançar exceção, caso seja possível deletar uma categoria")
    public void testCanDeleteCategoria() {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(categoria));

        assertDoesNotThrow(() -> service.canDeleteCategoria(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção, caso não seja possível deletar uma categoria")
    public void testCanDeleteCategoriaWithLivros() {
        categoria.setLivros(Collections.singletonList(new Livro()));

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(categoria));

        assertThrows(BadRequestException.class, () -> service.canDeleteCategoria(1L));
    }

    @Test
    @DisplayName("Deve deletar uma categoria")
    public void testDeleteCategoria() throws BadRequestException {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(categoria));
        Mockito.doNothing().when(repository).deleteById(anyLong());

        String result = service.deleteCategoria(1L);

        assertEquals(MessageUtil.get("categoria.deleted"), result);
    }
}
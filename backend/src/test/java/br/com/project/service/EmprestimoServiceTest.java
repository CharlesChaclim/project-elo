package br.com.project.service;

import br.com.project.dto.EmprestimoDTO;
import br.com.project.dto.EmprestimoUpdateDTO;
import br.com.project.entity.Emprestimo;
import br.com.project.entity.Livro;
import br.com.project.entity.Usuario;
import br.com.project.enumeration.Status;
import br.com.project.exception.ObjectNotFoundException;
import br.com.project.repository.EmprestimoRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository repository;

    @Mock
    private LivroService livroService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private EmprestimoService service;

    private Emprestimo emprestimo;
    private EmprestimoDTO emprestimoDTO;

    @BeforeEach
    public void setup() {
        emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setLivro(new Livro());
        emprestimo.setUsuario(new Usuario());
        emprestimo.setStatus(Status.ATIVO);
        emprestimo.setDataDevolucao(LocalDate.now());
        emprestimo.setDataEmprestimo(LocalDate.now());

        emprestimoDTO = new EmprestimoDTO();
        emprestimoDTO.setId(1L);
        emprestimoDTO.setIdLivro(1L);
        emprestimoDTO.setIdUsuario(1L);
        emprestimoDTO.setStatus(Status.ATIVO.name());
        emprestimoDTO.setDataDevolucao(LocalDate.now());
        emprestimoDTO.setDataEmprestimo(LocalDate.now());
    }

    @Test
    @DisplayName("Deve retornar um empréstimo")
    public void getEmprestimoByIdShouldReturnEmprestimoWhenFound() {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(emprestimo));

        Emprestimo result = service.getEmprestimoById(1L);

        assertNotNull(result);
        assertEquals(emprestimo.getId(), result.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando empréstimo não for encontrado")
    public void getEmprestimoByIdShouldThrowExceptionWhenNotFound() {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.getEmprestimoById(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção, quando empréstimo ativo for encontrado")
    public void existsEmprestimoAtivoShouldThrowExceptionWhenEmprestimoAtivoExists() {
        Mockito.when(repository.existsByLivroIdAndStatus(anyLong(), eq(Status.ATIVO))).thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.existsEmprestimoAtivo(1L));
    }

    @Test
    @DisplayName("Não deve lançar exceção, quando empréstimo ativo não for encontrado")
    public void existsEmprestimoAtivoShouldNotThrowExceptionWhenNoEmprestimoAtivoExists() {
        Mockito.when(repository.existsByLivroIdAndStatus(anyLong(), eq(Status.ATIVO))).thenReturn(false);

        assertDoesNotThrow(() -> service.existsEmprestimoAtivo(1L));
    }

    @Test
    @DisplayName("Deve criar um empréstimo")
    public void createEmprestimoShouldReturnCreatedEmprestimoDTO() throws BadRequestException {
        Mockito.when(repository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        Mockito.when(livroService.getLivroById(anyLong())).thenReturn(emprestimo.getLivro());
        Mockito.when(usuarioService.getUsuarioById(anyLong())).thenReturn(emprestimo.getUsuario());

        EmprestimoDTO result = service.createEmprestimo(emprestimoDTO);

        assertNotNull(result);
        assertEquals(emprestimoDTO.getId(), result.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar um empréstimo com data de devolução inválida")
    public void createEmprestimoShouldThrowExceptionWhenDataDevolucaoBeforeDataEmprestimo() {
        emprestimoDTO.setDataDevolucao(LocalDate.now().minusDays(1));

        assertThrows(IllegalArgumentException.class, () -> service.createEmprestimo(emprestimoDTO));
    }

    @Test
    @DisplayName("Deve atualizar um empréstimo")
    public void patchEmprestimoShouldUpdateEmprestimoFields() throws BadRequestException {
        EmprestimoUpdateDTO updateDTO = new EmprestimoUpdateDTO();
        updateDTO.setDataDevolucao(LocalDate.now().plusDays(7));
        updateDTO.setStatus(Status.CONCLUIDO.name());

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(emprestimo));
        Mockito.when(repository.save(any(Emprestimo.class))).thenReturn(emprestimo);

        EmprestimoDTO result = service.patchEmprestimo(1L, updateDTO);

        assertNotNull(result);
        assertEquals(updateDTO.getDataDevolucao(), result.getDataDevolucao());
        assertEquals(updateDTO.getStatus(), result.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar um empréstimo com data de devolução inválida")
    public void patchEmprestimoShouldThrowExceptionWhenDataDevolucaoBeforeDataEmprestimo() {
        EmprestimoUpdateDTO updateDTO = new EmprestimoUpdateDTO();
        updateDTO.setStatus(Status.CONCLUIDO.name());
        updateDTO.setDataDevolucao(LocalDate.now().minusDays(1));

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(emprestimo));

        assertThrows(IllegalArgumentException.class, () -> service.patchEmprestimo(1L, updateDTO));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar um empréstimo com status ativo e livro emprestado")
    public void patchEmprestimoShouldThrowExceptionWhenEmprestimoStatusAtivoAndLivroEmprestado() {
        EmprestimoUpdateDTO updateDTO = new EmprestimoUpdateDTO();
        updateDTO.setStatus(Status.ATIVO.name());
        emprestimo.setStatus(Status.CONCLUIDO);

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(emprestimo));
        Mockito.when(repository.existsByLivroIdAndStatus(eq(emprestimo.getLivro().getId()), eq(Status.ATIVO))).thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.patchEmprestimo(1L, updateDTO));
    }
}
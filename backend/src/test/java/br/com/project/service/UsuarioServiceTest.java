package br.com.project.service;

import br.com.project.dto.UsuarioDTO;
import br.com.project.entity.Emprestimo;
import br.com.project.entity.Usuario;
import br.com.project.exception.ObjectNotFoundException;
import br.com.project.repository.UsuarioRepository;
import br.com.project.util.MessageUtil;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    @BeforeEach
    public void setUp() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Original Name");
        usuario.setEmail("original@example.com");
        usuario.setTelefone("1234567890");

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setNome("Updated Name");
        usuarioDTO.setEmail("updated@example.com");
        usuarioDTO.setTelefone("0987654321");
    }

    @Test
    @DisplayName("Deve retornar um usuario")
    public void getUsuarioByIdShouldReturnUsuarioWhenIdExists() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(repository.findById(anyLong())).thenReturn(Optional.of(usuario));

        Usuario result = service.getUsuarioById(1L);

        assertNotNull(result);
        assertEquals(usuario.getId(), result.getId());
    }

    @Test
    @DisplayName("Deve lançar ObjectNotFoundException quando o usuario não for encontrado")
    public void getUsuarioByIdShouldThrowObjectNotFoundExceptionWhenIdDoesNotExist() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.getUsuarioById(1L));
    }

    @Test
    @DisplayName("Deve retornar um usuarioDTO")
    public void getUsuarioDTOByIdShouldReturnUsuarioDTOWhenIdExists() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(repository.findById(anyLong())).thenReturn(Optional.of(usuario));

        UsuarioDTO result = service.getUsuarioDTOById(1L);

        assertNotNull(result);
        assertEquals(usuario.getId(), result.getId());
    }

    @Test
    @DisplayName("Deve criar um usuario")
    public void createUsuarioShouldReturnCreatedUsuarioDTO() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);

        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioDTO result = service.createUsuario(usuarioDTO);

        assertNotNull(result);
        assertEquals(usuarioDTO.getId(), result.getId());
    }

    @Test
    @DisplayName("Deve atualizar um usuario")
    public void updateUsuarioShouldUpdateAllFieldsExceptId() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Original Name");
        usuario.setEmail("original@example.com");
        usuario.setTelefone("1234567890");

        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setId(1L);
        updatedUsuario.setNome("Updated Name");
        updatedUsuario.setEmail("updated@example.com");
        updatedUsuario.setTelefone("0987654321");

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setNome("Updated Name");
        usuarioDTO.setEmail("updated@example.com");
        usuarioDTO.setTelefone("0987654321");

        when(repository.findById(anyLong())).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenReturn(updatedUsuario);

        UsuarioDTO result = service.updateUsuario(1L, usuarioDTO);

        assertNotNull(result);
        assertEquals(usuarioDTO.getId(), result.getId());
        assertEquals(usuarioDTO.getNome(), result.getNome());
        assertEquals(usuarioDTO.getEmail(), result.getEmail());
        assertEquals(usuarioDTO.getTelefone(), result.getTelefone());
        assertEquals(usuario.getId(), result.getId());
    }

    @Test
    @DisplayName("Deve lançar ObjectNotFoundException quando o usuario não for encontrado")
    public void canDeleteUsuarioShouldNotThrowExceptionWhenUsuarioHasNoEmprestimos() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(repository.findById(anyLong())).thenReturn(Optional.of(usuario));

        assertDoesNotThrow(() -> service.canDeleteUsuario(1L));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando o usuario tiver emprestimos")
    public void canDeleteUsuarioShouldThrowBadRequestExceptionWhenUsuarioHasEmprestimos() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmprestimos(Collections.singletonList(new Emprestimo()));

        when(repository.findById(anyLong())).thenReturn(Optional.of(usuario));

        assertThrows(BadRequestException.class, () -> service.canDeleteUsuario(1L));
    }

    @Test
    @DisplayName("Deve deletar um usuario")
    public void deleteUsuarioShouldReturnSuccessMessageWhenUsuarioIsDeleted() throws BadRequestException {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(repository.findById(anyLong())).thenReturn(Optional.of(usuario));
        doNothing().when(repository).deleteById(anyLong());

        String result = service.deleteUsuario(1L);

        assertEquals(MessageUtil.get("usuario.deleted"), result);
    }
}
package br.com.project.service;

import br.com.project.dto.UsuarioDTO;
import br.com.project.entity.Usuario;
import br.com.project.exception.ObjectNotFoundException;
import br.com.project.repository.UsuarioRepository;
import br.com.project.util.MessageUtil;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    public Usuario getUsuarioById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException(MessageUtil.get("usuario.not.found")));
    }

    public UsuarioDTO getUsuarioDTOById(Long id) {
        return UsuarioDTO.fromEntity(getUsuarioById(id));
    }

    public UsuarioDTO createUsuario(UsuarioDTO usuarioDTO) {
        return UsuarioDTO.fromEntity(repository.save(usuarioDTO.toEntity()));
    }

    private static Usuario setNewDataToUsuario(UsuarioDTO usuarioDTO, Usuario usuario) {
        Usuario newUsuario = usuarioDTO.toEntity();
        newUsuario.setId(usuario.getId());
        return newUsuario;
    }

    public UsuarioDTO updateUsuario(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = getUsuarioById(id);

        Usuario newUsuario = setNewDataToUsuario(usuarioDTO, usuario);

        return UsuarioDTO.fromEntity(repository.save(newUsuario));
    }

    public void canDeleteUsuario(Long id) throws BadRequestException {
        Usuario usuario = getUsuarioById(id);

        if (!usuario.getEmprestimos().isEmpty()) {
            throw new BadRequestException(MessageUtil.get("usuario.cannot.delete"));
        }
    }

    public String deleteUsuario(Long id) throws BadRequestException {
        canDeleteUsuario(id);

        repository.deleteById(id);

        return MessageUtil.get("usuario.deleted");
    }

}

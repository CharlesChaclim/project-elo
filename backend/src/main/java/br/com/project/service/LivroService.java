package br.com.project.service;

import br.com.project.dto.LivroDTO;
import br.com.project.entity.Livro;
import br.com.project.exception.ObjectNotFoundException;
import br.com.project.repository.LivroRepository;
import br.com.project.util.MessageUtil;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LivroService {

    private final LivroRepository repository;
    private final CategoriaService categoriaService;

    public Livro getLivroById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException(MessageUtil.get("livro.not.found")));
    }

    public LivroDTO getLivroDTOById(Long id) {
        return LivroDTO.fromEntity(getLivroById(id));
    }

    public void existCategoria(LivroDTO livroDTO) {
        categoriaService.getCategoriaById(livroDTO.getIdCategoria());
    }

    public LivroDTO createLivro(LivroDTO livroDTO) {
        existCategoria(livroDTO);
        return LivroDTO.fromEntity(repository.save(livroDTO.toEntity()));
    }

    private Livro setNewLivroData(LivroDTO livroDTO, Livro livro) {
        Livro newLivro = livroDTO.toEntity();
        newLivro.setId(livro.getId());
        existCategoria(livroDTO);
        return newLivro;
    }

    public LivroDTO updateLivro(Long id, LivroDTO livroDTO) {
        Livro livro = getLivroById(id);
        Livro newLivro = setNewLivroData(livroDTO, livro);
        return LivroDTO.fromEntity(repository.save(newLivro));
    }

    public void canDeleteLivro(Long id) throws BadRequestException {
        Livro livro = getLivroById(id);

        if (!livro.getEmprestimos().isEmpty()) {
            throw new BadRequestException(MessageUtil.get("livro.cannot.delete"));
        }
    }

    public String deleteLivro(Long id) throws BadRequestException {
        canDeleteLivro(id);
        repository.deleteById(id);
        return MessageUtil.get("livro.deleted");
    }

    public List<Livro> findLivrosMaisEmprestados() {
        return repository.findLivrosMaisEmprestados();
    }

    public List<Livro> findLivrosMaisEmprestadosByUsuarioExcluindoOsJaEmprestados(Long idUsuario) {
        return repository.findLivrosMaisEmprestadosByUsuarioExcluindoOsJaEmprestados(idUsuario);
    }
}

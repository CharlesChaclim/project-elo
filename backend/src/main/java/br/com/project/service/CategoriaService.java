package br.com.project.service;

import br.com.project.dto.CategoriaDTO;
import br.com.project.entity.Categoria;
import br.com.project.exception.ObjectNotFoundException;
import br.com.project.repository.CategoriaRepository;
import br.com.project.util.MessageUtil;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;

    public Categoria getCategoriaById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException(MessageUtil.get("categoria.not.found")));
    }

    public CategoriaDTO getCategoriaDTOById(Long id) {
        return CategoriaDTO.fromEntity(getCategoriaById(id));
    }

    public CategoriaDTO createCategoria(CategoriaDTO categoriaDTO) {
        return CategoriaDTO.fromEntity(repository.save(categoriaDTO.toEntity()));
    }

    private static Categoria setNewCategoriaData(CategoriaDTO categoriaDTO, Categoria categoria) {
        Categoria newCategoria = categoriaDTO.toEntity();
        newCategoria.setId(categoria.getId());
        return newCategoria;
    }

    public CategoriaDTO updateCategoria(Long id, CategoriaDTO categoriaDTO) {
        Categoria categoria = getCategoriaById(id);
        Categoria newCategoria = setNewCategoriaData(categoriaDTO, categoria);
        return CategoriaDTO.fromEntity(repository.save(newCategoria));
    }

    public void canDeleteCategoria(Long id) throws BadRequestException {
        Categoria categoria = getCategoriaById(id);

        if (!categoria.getLivros().isEmpty()) {
            throw new BadRequestException(MessageUtil.get("categoria.cannot.delete"));
        }
    }

    public String deleteCategoria(Long id) throws BadRequestException {
        canDeleteCategoria(id);
        repository.deleteById(id);
        return MessageUtil.get("categoria.deleted");
    }
}

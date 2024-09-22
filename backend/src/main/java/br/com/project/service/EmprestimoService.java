package br.com.project.service;

import br.com.project.dto.EmprestimoDTO;
import br.com.project.dto.EmprestimoUpdateDTO;
import br.com.project.entity.Emprestimo;
import br.com.project.enumeration.Status;
import br.com.project.exception.ObjectNotFoundException;
import br.com.project.repository.EmprestimoRepository;
import br.com.project.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository repository;

    private final LivroService livroService;
    private final UsuarioService usuarioService;

    public Emprestimo getEmprestimoById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException(MessageUtil.get("emprestimo.not.found")));
    }

    public void existsEmprestimoAtivo(Long id) throws BadRequestException {
        if (repository.existsByLivroIdAndStatus(id, Status.ATIVO)) {
            throw new BadRequestException(MessageUtil.get("livro.emprestado"));
        }
    }

    public EmprestimoDTO createEmprestimo(EmprestimoDTO emprestimoDTO) throws BadRequestException {
        Emprestimo emprestimo = emprestimoDTO.toEntity();
        emprestimo.setLivro(livroService.getLivroById(emprestimo.getLivro().getId()));
        emprestimo.setUsuario(usuarioService.getUsuarioById(emprestimo.getUsuario().getId()));

        existsEmprestimoAtivo(emprestimo.getLivro().getId());

        return EmprestimoDTO.fromEntity(repository.save(emprestimoDTO.toEntity()));
    }

    private void setEmprestimoData(EmprestimoUpdateDTO emprestimoDTO, Emprestimo emprestimo) throws BadRequestException {
        if (!emprestimo.isAtivo() && emprestimoDTO.isAtivo()) {
            existsEmprestimoAtivo(emprestimo.getLivro().getId());
        }

        if (emprestimoDTO.getDataDevolucao().isBefore(emprestimo.getDataEmprestimo())) {
            throw new IllegalArgumentException(MessageUtil.get("erro.data.devolucao.invalida"));
        }

        emprestimo.setDataDevolucao(emprestimoDTO.getDataDevolucao());
        emprestimo.setStatus(Status.valueOf(emprestimoDTO.getStatus()));
    }

    public EmprestimoDTO patchEmprestimo(Long id, EmprestimoUpdateDTO emprestimoDTO) throws BadRequestException {
        Emprestimo emprestimo = getEmprestimoById(id);
        setEmprestimoData(emprestimoDTO, emprestimo);

        return EmprestimoDTO.fromEntity(repository.save(emprestimo));
    }
}

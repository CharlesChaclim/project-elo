package br.com.project.repository;

import br.com.project.entity.Emprestimo;
import br.com.project.enumeration.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    boolean existsByLivroIdAndStatus(Long id, Status status);
}

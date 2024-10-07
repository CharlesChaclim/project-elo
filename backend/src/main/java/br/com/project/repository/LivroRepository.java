package br.com.project.repository;

import br.com.project.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    @Query(value =
            """
                    SElECT l
                    FROM Livro l
                    LEFT JOIN Emprestimo e ON l.id = e.livro.id
                    GROUP BY l.id
                    ORDER BY COUNT(e.id) DESC, l.id ASC
                    """)
    List<Livro> findLivrosMaisEmprestados();

    @Query(value =
            """
                    SELECT l
                    FROM Livro l
                    JOIN Categoria c ON l.categoria.id = c.id
                    LEFT JOIN Emprestimo e ON l.id = e.livro.id
                    LEFT JOIN Usuario u ON e.usuario.id = u.id
                    WHERE u.id <> :idUsuario OR u.id IS NULL
                    GROUP BY l.id
                    ORDER BY COUNT(e.id) DESC, l.id ASC
                    """)
    List<Livro> findLivrosMaisEmprestadosByUsuarioExcluindoOsJaEmprestados(@Param("idUsuario") Long idUsuario);

}
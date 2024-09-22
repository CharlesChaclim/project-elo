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
                    SElECT l.*
                    FROM livros l
                    LEFT JOIN emprestimos e ON l.id = e.livro_id
                    GROUP BY l.id
                    ORDER BY COUNT(e.id) DESC, l.id ASC
                    """,
            nativeQuery = true)
    List<Livro> findLivrosMaisEmprestados();

    @Query(value =
            """
                    SELECT l.*
                    FROM livros l
                    JOIN categorias c ON l.categoria_id = c.id
                    LEFT JOIN emprestimos e ON l.id = e.livro_id
                    LEFT JOIN usuarios u ON e.usuario_id = u.id
                    WHERE u.id <> :idUsuario OR u.id IS NULL
                    GROUP BY l.id
                    ORDER BY COUNT(e.id) DESC, l.id ASC
                    """,
            nativeQuery = true)
    List<Livro> findLivrosMaisEmprestadosByUsuarioExcluindoOsJaEmprestados(@Param("idUsuario") Long idUsuario);

}
package br.com.project.util.pesquisa;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@Component
public class Pesquisa<T> {

    private final EntityManager entityManager;

    public Pesquisa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static String alterarCampoParaNomeAtributo(Class<?> clazz, String nomeCampo) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(JsonProperty.class))
                .filter(field -> field.getAnnotation(JsonProperty.class).value().equals(nomeCampo))
                .map(Field::getName)
                .findFirst()
                .orElse(nomeCampo);
    }

    public PesquisaResult<T> pesquisar(PesquisaRequest request, Class<T> entityClass) {
        validarCampoExistente(entityClass, request.getFiltros(), request.getOrdenacao());
        PesquisaResult<T> pesquisaResult = new PesquisaResult<>();

        ResultadoBusca<T> busca = buscarRegistros(request, entityClass);
        pesquisaResult.setRegistros(busca.getRegistros());
        pesquisaResult.setTotalRegistros(busca.getTotalRegistros());

        return pesquisaResult;
    }

    public ResultadoBusca<T> buscarRegistros(PesquisaRequest request, Class<T> entityClass) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);

        Predicate[] predicates = criarPredicados(criteriaBuilder, root, request);
        criteriaQuery.where(predicates);

        List<Order> orders = adicionarOrdenacao(criteriaBuilder, root, request);
        criteriaQuery.orderBy(orders);

        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        List<T> resultados = query
                .setFirstResult((request.getPagina() - 1) * request.getQuantidadeRegistros())
                .setMaxResults(request.getQuantidadeRegistros()).getResultList();

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(entityClass);

        Predicate[] countPredicates = criarPredicados(criteriaBuilder, countRoot, request);
        countQuery.select(criteriaBuilder.count(countRoot));
        countQuery.where(countPredicates);

        Long totalRegistros = entityManager.createQuery(countQuery).getSingleResult();

        return new ResultadoBusca<>(resultados, totalRegistros);
    }

    private Predicate[] criarPredicados(CriteriaBuilder criteriaBuilder, Root<T> root, PesquisaRequest request) {
        if (request.getFiltros() != null) {
            return request.getFiltros().stream()
                    .map(filtro -> criarPredicate(criteriaBuilder, root, filtro))
                    .toArray(Predicate[]::new);
        }

        return new Predicate[]{};
    }

    private List<Order> adicionarOrdenacao(CriteriaBuilder criteriaBuilder, Root<T> root, PesquisaRequest request) {
        if (request.getOrdenacao() != null) {
            return request.getOrdenacao().stream()
                    .map(ordenacao -> {
                        Path<?> campo = getPath(root, ordenacao.getCampo(), JoinType.LEFT);
                        if (ordenacao.getOrdenacao() == Ordenacao.ASC) {
                            return criteriaBuilder.asc(campo);
                        }

                        return criteriaBuilder.desc(campo);
                    }).toList();
        }

        return new ArrayList<>();
    }

    private Path<?> getPath(Path<?> root, String campo, JoinType joinType) {
        String[] campos = campo.split("\\.");
        Path<?> path = root;
        for (String part : campos) {
            if (campos[campos.length - 1].equals(part)) {
                path = path.get(part);
            } else if (path instanceof From) {
                path = ((From<?, ?>) path).join(part, joinType);
            } else {
                throw new IllegalArgumentException("O caminho deve ser uma instância de From para realizar o join.");
            }
        }
        return path;
    }

    private Predicate criarPredicate(CriteriaBuilder criteriaBuilder, Root<?> root, PesquisaFiltro filtro) {
        Object valor = converterParaLocalDateTimeCamposDeData(filtro.getValor());

        Path<?> path = getPath(root, filtro.getCampo(), JoinType.INNER);

        return switch (filtro.getComparacao()) {
            case COMECA_COM ->
                    criteriaBuilder.like(criteriaBuilder.upper(path.as(String.class)), valor.toString().toUpperCase() + "%");
            case CONTEM ->
                    criteriaBuilder.like(criteriaBuilder.upper(path.as(String.class)), "%" + valor.toString().toUpperCase() + "%");
            case IGUAL -> {
                if (valor == null) {
                    yield criteriaBuilder.isNull(path);
                }

                if (path.getJavaType().getTypeName().equals("java.time.LocalDate")) {
                    yield criteriaBuilder.like(criteriaBuilder.upper(path.as(String.class)), valor.toString().toUpperCase());
                }

                yield criteriaBuilder.equal(path, valor);
            }
        };
    }

    private void validarCampoExistente(Class<?> entityClass, List<PesquisaFiltro> filtros, List<PesquisaOrdenacao> ordenacoes) {
        if (filtros != null) {
            filtros.forEach((filtro -> {
                filtro.setCampo(alterarCampoParaNomeAtributo(entityClass, filtro.getCampo()));
                validarCampoExistente(entityClass, filtro.getCampo());
            }));
        }

        if (ordenacoes != null) {
            ordenacoes.forEach(ordenacao -> {
                ordenacao.setCampo(alterarCampoParaNomeAtributo(entityClass, ordenacao.getCampo()));
                validarCampoExistente(entityClass, ordenacao.getCampo());
            });

        }
    }

    private void validarCampoExistente(Class<?> entityClass, String campo) {
        try {
            if (campo.contains(".")) {
                String[] partes = campo.split("\\.");
                Class<?> currentClass = entityClass;
                for (String parte : partes) {
                    Field field = currentClass.getDeclaredField(parte);
                    currentClass = field.getType();
                    if (Collection.class.isAssignableFrom(currentClass)) {
                        currentClass = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    }
                }
            } else {
                entityClass.getDeclaredField(campo);
            }
        } catch (NoSuchFieldException | SecurityException e) {
            throw new IllegalArgumentException("Campo '" + campo + "' não encontrado na classe " + entityClass.getName(), e);
        }
    }

    public Object converterParaLocalDateTimeCamposDeData(Object valor) {
        if (valor instanceof String valorStr) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm[:ss]");
            try {
                return LocalDateTime.parse(valorStr, formatter);
            } catch (DateTimeParseException e) {
                return valor;
            }
        }
        return valor;
    }

}

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
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;


@Component
public class Pesquisa<T> {

    private final EntityManager entityManager;

    public Pesquisa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public PesquisaResult<T> pesquisar(PesquisaRequest request, Class<T> entityClass) {
        return pesquisar(request, entityClass, null);
    }

    public PesquisaResult<T> pesquisar(PesquisaRequest request, Class<T> entityClass, Class<Object> dtoClass) {
        if (dtoClass != null) {
            validarCampoExistente(dtoClass, request.getFiltros(), request.getOrdenacao());
        } else {
            validarCampoExistente(entityClass, request.getFiltros(), request.getOrdenacao());
        }
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

        List<Predicate> predicates = criarPredicados(criteriaBuilder, root, request);
        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        adicionarOrdenacao(criteriaBuilder, criteriaQuery, root, request);

        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        List<T> resultados = query
                .setFirstResult((request.getPagina() - 1) * request.getQuantidadeRegistros())
                .setMaxResults(request.getQuantidadeRegistros()).getResultList();

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(entityClass);

        List<Predicate> countPredicates = criarPredicados(criteriaBuilder, countRoot, request);
        countQuery.select(criteriaBuilder.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));

        Long totalRegistros = entityManager.createQuery(countQuery).getSingleResult();

        return new ResultadoBusca<>(resultados, totalRegistros);
    }

    private List<Predicate> criarPredicados(CriteriaBuilder criteriaBuilder, Root<T> root, PesquisaRequest request) {
        List<Predicate> predicates = new ArrayList<>();
        if (request.getFiltros() != null) {
            for (PesquisaFiltro filtro : request.getFiltros()) {
                Predicate predicate = criarPredicate(criteriaBuilder, root, filtro.getCampo(), filtro.getValor(), filtro.getComparacao());
                predicates.add(predicate);
            }
        }
        return predicates;
    }

    private void adicionarOrdenacao(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> criteriaQuery, Root<T> root, PesquisaRequest request) {
        if (request.getOrdenacao() != null) {
            List<Order> ordenacoes = new ArrayList<>();
            for (PesquisaOrdenacao ordenacao : request.getOrdenacao()) {
                Path<?> campo = getPath(root, ordenacao.getCampo(), JoinType.LEFT);
                if (ordenacao.getOrdenacao() == Ordenacao.ASC) {
                    ordenacoes.add(criteriaBuilder.asc(campo));
                } else {
                    ordenacoes.add(criteriaBuilder.desc(campo));
                }
            }
            criteriaQuery.orderBy(ordenacoes);
        }
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
                throw new IllegalArgumentException("Path must be an instance of From to perform join");
            }
        }
        return path;
    }

    private Predicate criarPredicate(CriteriaBuilder criteriaBuilder, Root<?> root, String campo, Object valor, Comparacao comparacao) {
        valor = converterParaLocalDateTimeCamposDeData(valor);
        if (!(isNumeric(valor) || !(valor instanceof Temporal) || valor instanceof Enum)) {
            throw new IllegalArgumentException("Comparação inválida para tipo de campo não numérico, temporal ou enum!");
        }

        Path<?> path = getPath(root, campo, JoinType.INNER);

        if (path.getJavaType().isEnum() && valor instanceof String) {
            valor = Enum.valueOf((Class<Enum>) path.getJavaType(), (String) valor);
        }

        switch (comparacao) {
            case COMECA_COM:
                return criteriaBuilder.like(criteriaBuilder.upper(path.as(String.class)), valor.toString().toUpperCase() + "%");
            case CONTEM:
                return criteriaBuilder.like(criteriaBuilder.upper(path.as(String.class)), "%" + valor.toString().toUpperCase() + "%");
            case IGUAL:
                if (valor instanceof Enum) {
                    return criteriaBuilder.equal(path.as(Enum.class), valor);
                } else if (valor == null) {
                    return criteriaBuilder.isNull(path);
                } else if (path.getJavaType().getTypeName().equals("java.time.LocalDate")) {
                    return criteriaBuilder.like(criteriaBuilder.upper(path.as(String.class)), valor.toString().toUpperCase());
                } else {
                    return criteriaBuilder.equal(path, valor);
                }
            default:
                throw new IllegalArgumentException("Comparação não suportada: " + comparacao);
        }
    }

    private void validarCampoExistente(Class<?> entityClass, List<PesquisaFiltro> filtros, List<PesquisaOrdenacao> ordenacoes) {
        if (filtros != null) {
            for (PesquisaFiltro filtro : filtros) {
                filtro.setCampo(alterarCampoParaNomeAtributo(entityClass, filtro.getCampo()));
                validarCampoExistente(entityClass, filtro.getCampo());
            }
        }

        if (ordenacoes != null) {
            for (PesquisaOrdenacao ordenacao : ordenacoes) {
                ordenacao.setCampo(alterarCampoParaNomeAtributo(entityClass, ordenacao.getCampo()));
                validarCampoExistente(entityClass, ordenacao.getCampo());
            }
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

    public static boolean isNumeric(Object str) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        return str != null && pattern.matcher(str.toString()).matches();
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

    public static String alterarCampoParaNomeAtributo(Class<?> clazz, String nomeCampo) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonProperty.class)) {
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                if (jsonProperty.value().equals(nomeCampo)) {
                    return field.getName();
                }
            }
        }
        return nomeCampo;
    }

}

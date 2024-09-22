package br.com.project.service;

import br.com.project.dto.LivroDTO;
import br.com.project.entity.Livro;
import br.com.project.exception.ObjectNotFoundException;
import br.com.project.util.MessageUtil;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volume.VolumeInfo.IndustryIdentifiers;
import com.google.api.services.books.model.Volumes;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GoogleBooksService {

    private final Books books;
    private final LivroService livroService;

    public GoogleBooksService(LivroService livroService) {
        this.livroService = livroService;
        this.books = new Books.Builder(new NetHttpTransport(), new JacksonFactory(), null)
                .setApplicationName("MyApplicationName")
                .build();
    }

    public Volume buscarLivroPorIdLivro(Long idLivro) {
        try {
            Livro livro = livroService.getLivroById(idLivro);
            List<Volume> volumes = books.volumes().list(livro.getTitulo()).execute().getItems();

            if (volumes == null || volumes.isEmpty()) {
                throw new ObjectNotFoundException(MessageUtil.get("erro.livro.nao.encontrado"));
            }

            return volumes.get(0);
        } catch (IOException e) {
            throw new RuntimeException(MessageUtil.get("erro.buscando.livro"), e);
        }
    }

    public Volumes buscarLivrosPorTitulo(String titulo) {
        try {
            Volumes volumes = books.volumes().list(titulo).execute();

            if (volumes.getItems() == null) {
                throw new ObjectNotFoundException(MessageUtil.get("erro.livro.nao.encontrado"));
            }

            return volumes;
        } catch (IOException e) {
            throw new RuntimeException(MessageUtil.get("erro.buscando.livro"), e);
        }
    }

    public void verificarVolumeInfo(Volume volume) throws BadRequestException {
        String titulo = volume.getVolumeInfo().getTitle();
        List<String> autores = volume.getVolumeInfo().getAuthors();
        String dataPublicacao = volume.getVolumeInfo().getPublishedDate();
        List<IndustryIdentifiers> identifiers = volume.getVolumeInfo().getIndustryIdentifiers();

        if (titulo == null) {
            throw new BadRequestException(MessageUtil.get("erro.titulo.nulo"));
        }

        if (autores == null || autores.isEmpty() || autores.get(0) == null) {
            throw new BadRequestException(MessageUtil.get("erro.autor.nulo"));
        }

        if (dataPublicacao == null) {
            throw new BadRequestException(MessageUtil.get("erro.data.publicacao.nula"));
        }

        if (identifiers == null || identifiers.isEmpty() || identifiers.get(0).getIdentifier() == null) {
            throw new BadRequestException(MessageUtil.get("erro.isbn.nulo"));
        }
    }

    public LivroDTO adicionarLivro(String idGoogleBooks, Long idCategoria) throws BadRequestException {
        try {
            Volume volume = books.volumes().get(idGoogleBooks).execute();
            verificarVolumeInfo(volume);
            return livroService.createLivro(LivroDTO.fromVolume(volume, idCategoria));
        } catch (BadRequestException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(MessageUtil.get("erro.buscando.livro"), e);
        }

    }
}
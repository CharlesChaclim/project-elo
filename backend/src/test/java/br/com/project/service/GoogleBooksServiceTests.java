package br.com.project.service;

import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volume.VolumeInfo.IndustryIdentifiers;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GoogleBooksServiceTests {

    @Mock
    private LivroService livroService;

    @InjectMocks
    private GoogleBooksService googleBooksService;

    @BeforeEach
    public void setup() {
        googleBooksService = new GoogleBooksService(livroService);
    }

    @Test
    @DisplayName("Deve verificar informações do volume")
    public void verificarVolumeInfoShouldNotThrowException() {
        Volume volume = new Volume();
        Volume.VolumeInfo volumeInfo = new Volume.VolumeInfo();
        volumeInfo.setTitle("Test Title");
        volumeInfo.setAuthors(Collections.singletonList("Test Author"));
        volumeInfo.setPublishedDate("2023-01-01");
        volumeInfo.setIndustryIdentifiers(Collections.singletonList(new IndustryIdentifiers().setIdentifier("1234567890")));
        volume.setVolumeInfo(volumeInfo);

        assertDoesNotThrow(() -> googleBooksService.verificarVolumeInfo(volume));
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar informações do volume com título nulo")
    public void verificarVolumeInfoShouldThrowExceptionWhenTitleIsNull() {
        Volume volume = new Volume();
        Volume.VolumeInfo volumeInfo = new Volume.VolumeInfo();
        volumeInfo.setAuthors(Collections.singletonList("Test Author"));
        volumeInfo.setPublishedDate("2023-01-01");
        volumeInfo.setIndustryIdentifiers(Collections.singletonList(new IndustryIdentifiers().setIdentifier("1234567890")));
        volume.setVolumeInfo(volumeInfo);

        assertThrows(BadRequestException.class, () -> googleBooksService.verificarVolumeInfo(volume));
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar informações do volume com autor nulo")
    public void verificarVolumeInfoShouldThrowExceptionWhenAuthorIsNull() {
        Volume volume = new Volume();
        Volume.VolumeInfo volumeInfo = new Volume.VolumeInfo();
        volumeInfo.setTitle("Test Title");
        volumeInfo.setPublishedDate("2023-01-01");
        volumeInfo.setIndustryIdentifiers(Collections.singletonList(new IndustryIdentifiers().setIdentifier("1234567890")));
        volume.setVolumeInfo(volumeInfo);

        assertThrows(BadRequestException.class, () -> googleBooksService.verificarVolumeInfo(volume));
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar informações do volume com data de publicação nula")
    public void verificarVolumeInfoShouldThrowExceptionWhenPublishedDateIsNull() {
        Volume volume = new Volume();
        Volume.VolumeInfo volumeInfo = new Volume.VolumeInfo();
        volumeInfo.setTitle("Test Title");
        volumeInfo.setAuthors(Collections.singletonList("Test Author"));
        volumeInfo.setIndustryIdentifiers(Collections.singletonList(new IndustryIdentifiers().setIdentifier("1234567890")));
        volume.setVolumeInfo(volumeInfo);

        assertThrows(BadRequestException.class, () -> googleBooksService.verificarVolumeInfo(volume));
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar informações do volume com ISBN nulo")
    public void verificarVolumeInfoShouldThrowExceptionWhenIsbnIsNull() {
        Volume volume = new Volume();
        Volume.VolumeInfo volumeInfo = new Volume.VolumeInfo();
        volumeInfo.setTitle("Test Title");
        volumeInfo.setAuthors(Collections.singletonList("Test Author"));
        volumeInfo.setPublishedDate("2023-01-01");
        volumeInfo.setIndustryIdentifiers(Collections.singletonList(new IndustryIdentifiers()));
        volume.setVolumeInfo(volumeInfo);

        assertThrows(BadRequestException.class, () -> googleBooksService.verificarVolumeInfo(volume));
    }
}
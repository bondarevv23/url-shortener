package com.github.bondarevv23.urlshortener.service;

import com.github.bondarevv23.urlshortener.api.domain.Request;
import com.github.bondarevv23.urlshortener.core.domain.Reduction;
import com.github.bondarevv23.urlshortener.core.exception.AliasAlreadyExistsException;
import com.github.bondarevv23.urlshortener.core.exception.UnknownAliasException;
import com.github.bondarevv23.urlshortener.core.repository.UrlRepository;
import com.github.bondarevv23.urlshortener.core.service.AliasGeneratorService;
import com.github.bondarevv23.urlshortener.core.service.AliasGeneratorServiceImpl;
import com.github.bondarevv23.urlshortener.core.service.UrlServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UrlServiceImplTest {

    UrlRepository repository;

    UrlServiceImpl service;

    @Value("${alias.size})")
    private int aliasSize;

    @BeforeAll
    void init() {
        repository = Mockito.mock(UrlRepository.class);
        AliasGeneratorService generator = new AliasGeneratorServiceImpl(aliasSize);
        service = new UrlServiceImpl(repository, generator);
    }

    @BeforeEach
    void resetMock() {
        reset(repository);
    }

    @Test
    void whenGetLink_thenFindByAliasCalls() {
        // given
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        String alias = "alias";
        when(repository.findByAlias(alias)).thenReturn(Optional.of(new Reduction(1L, alias, "link")));

        // when
        service.getLink(alias);

        // then
        verify(repository).findByAlias(captor.capture());
        assertThat(captor.getValue()).isEqualTo(alias);
    }

    @Test
    void whenGetLinkByUnknownAlias_thenUnknownAliasExceptionThrows() {
        // given
        String alias = "alias";
        when(repository.findByAlias(alias)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.getLink(alias)).isInstanceOf(UnknownAliasException.class);
    }

    @Test
    void whenCreateLinkWithoutAlias_thenRandomAliasGenerates() {
        // given
        var request = new Request("url", null);
        var captor = ArgumentCaptor.forClass(Reduction.class);
        when(repository.save(any(Reduction.class))).thenReturn(new Reduction());

        // when
        var response =  service.createLink(request);

        // then
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getUrl()).isEqualTo(request.url());
        assertThat(captor.getValue().getAlias()).isNotNull();
        assertThat(response.alias()).isEqualTo(captor.getValue().getAlias());
    }

    @Test
    void whenCreateLinkWithAlias_thenSaveMethodCalls() {
        // given
        var request = new Request("url", "alias");
        when(repository.existsByAlias("alias")).thenReturn(false);
        var captor = ArgumentCaptor.forClass(Reduction.class);
        when(repository.save(any(Reduction.class))).thenReturn(new Reduction());

        // when
        var response = service.createLink(request);

        // then
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getAlias()).isEqualTo("alias");
        assertThat(captor.getValue().getUrl()).isEqualTo("url");
        assertThat(response.alias()).isEqualTo("alias");
    }

    @Test
    void whenCreateLinkWithAlreadyEExistedAlias_thenAliasAlreadyExistsExceptionThrows() {
        // given
        String alias = "alias";
        when(repository.existsByAlias(alias)).thenReturn(true);
        var request = new Request("url", alias);

        // when

        // then
        assertThatThrownBy(() -> service.createLink(request)).isInstanceOf(AliasAlreadyExistsException.class);
    }
}

package com.github.bondarevv23.urlshortener.core.service;

import com.github.bondarevv23.urlshortener.api.domain.Request;
import com.github.bondarevv23.urlshortener.api.domain.Response;
import com.github.bondarevv23.urlshortener.core.domain.Reduction;
import com.github.bondarevv23.urlshortener.core.exception.AliasAlreadyExistsException;
import com.github.bondarevv23.urlshortener.core.exception.UnknownAliasException;
import com.github.bondarevv23.urlshortener.core.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository repository;
    private final AliasGeneratorService generator;

    @Override
    public String getLink(String alias) {
        return repository.findByAlias(alias)
                .map(Reduction::getUrl)
                .orElseThrow(() -> new UnknownAliasException(alias));
    }

    @Override
    public Response createLink(Request request) {
        String alias = getAlias(request);
        Reduction reduction = Reduction.builder()
                .url(request.url())
                .alias(alias)
                .build();
        repository.save(reduction);
        return new Response(alias);
    }

    private String getAlias(Request request) {
        return Optional.ofNullable(request.alias())
                .map(alias -> {
                    if (repository.existsByAlias(alias)) {
                        throw new AliasAlreadyExistsException(alias);
                    }
                    return alias;
                })
                .orElseGet(() -> {
                    String alias = generator.generate();
                    while (repository.existsByAlias(alias)) {
                        alias = generator.generate();
                    }
                    return alias;
                });
    }
}

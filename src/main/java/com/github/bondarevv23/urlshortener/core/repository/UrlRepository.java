package com.github.bondarevv23.urlshortener.core.repository;

import com.github.bondarevv23.urlshortener.core.domain.Reduction;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RepositoryDefinition(domainClass = Reduction.class, idClass = Long.class)
public interface UrlRepository {

    Reduction save(Reduction reduction);

    Optional<Reduction> findByAlias(String alias);

    boolean existsByAlias(String alias);

}

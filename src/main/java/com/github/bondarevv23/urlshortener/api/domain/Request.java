package com.github.bondarevv23.urlshortener.api.domain;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

public record Request (
        @URL
        String url,

        @Length(min = 7, max = 255)
        String alias
) { }

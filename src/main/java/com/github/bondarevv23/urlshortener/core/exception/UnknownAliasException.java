package com.github.bondarevv23.urlshortener.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UnknownAliasException extends RuntimeException {
    private final String alias;
}

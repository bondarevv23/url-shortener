package com.github.bondarevv23.urlshortener.core.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;


@RequiredArgsConstructor
public class AliasGeneratorServiceImpl implements AliasGeneratorService {

    private final int aliasSize;

    @Override
    public String generate() {
        return RandomStringUtils.random(aliasSize, true, true);
    }

}

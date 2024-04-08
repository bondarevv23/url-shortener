package com.github.bondarevv23.urlshortener.core.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;


@Service
public class AliasGeneratorServiceImpl implements AliasGeneratorService {

    private static final int ALIAS_SIZE = 7;

    @Override
    public String generate() {
        return RandomStringUtils.random(ALIAS_SIZE, true, true);
    }

}

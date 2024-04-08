package com.github.bondarevv23.urlshortener.core.service;

import com.github.bondarevv23.urlshortener.api.domain.Request;
import com.github.bondarevv23.urlshortener.api.domain.Response;

public interface UrlService {

    String getLink(String alias);

    Response createLink(Request request);
}

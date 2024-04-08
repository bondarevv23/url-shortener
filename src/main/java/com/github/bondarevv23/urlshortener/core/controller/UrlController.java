package com.github.bondarevv23.urlshortener.core.controller;

import com.github.bondarevv23.urlshortener.api.controller.UrlControllerApi;
import com.github.bondarevv23.urlshortener.api.domain.Request;
import com.github.bondarevv23.urlshortener.api.domain.Response;
import com.github.bondarevv23.urlshortener.core.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class UrlController implements UrlControllerApi {

    private final UrlService service;

    @Override
    public RedirectView redirect(String alias) {
        return new RedirectView(service.getLink(alias));
    }

    @Override
    public ResponseEntity<Response> createLink(Request request) {
        return ResponseEntity.ok(service.createLink(request));
    }
}

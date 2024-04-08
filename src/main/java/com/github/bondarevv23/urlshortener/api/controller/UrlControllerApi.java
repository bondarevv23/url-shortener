package com.github.bondarevv23.urlshortener.api.controller;

import com.github.bondarevv23.urlshortener.api.domain.Request;
import com.github.bondarevv23.urlshortener.api.domain.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.servlet.view.RedirectView;

@RequestMapping
public interface UrlControllerApi {

    @GetMapping(path = "/{alias}")
    RedirectView redirect(@PathVariable("alias") String alias);

    @PostMapping
    ResponseEntity<Response> createLink(@RequestBody @Valid Request request);

}

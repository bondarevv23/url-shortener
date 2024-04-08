package com.github.bondarevv23.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.bondarevv23.urlshortener.api.domain.Request;
import com.github.bondarevv23.urlshortener.api.domain.Response;
import com.github.bondarevv23.urlshortener.core.repository.UrlRepository;
import com.github.bondarevv23.urlshortener.core.service.UrlService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class UrlControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    UrlService service;

    @Autowired
    UrlRepository repository;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @AfterEach
    void cleanData() {
        repository.deleteAll();
    }

    @Test
    void whenGetRequest_thenAcceptRedirect() throws Exception {
        // given
        String alias = "gooogle";
        service.createLink(new Request("https://www.google.com/", alias));

        // when

        // then
        mockMvc.perform(get("/{alias}", alias))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("https://www.google.com/")
                );
    }

    @Test
    void whenGetRequestWithUnknownAlias_thenNotFound() throws Exception {
        // given
        String alias = "unknown";

        // when

        // then
        mockMvc.perform(get("/{alias}", alias))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.alias").value(alias),
                        jsonPath("$.message").isNotEmpty()
                );
    }

    @Test
    void whenCreateLinkWithAlias_thenAliasReturnedAndLinkSaved() throws Exception {
        // given
        String url = "https://www.google.com/";
        String alias = "gooogle";
        Request request = new Request(url, alias);
        String stringRequest = objectMapper.writeValueAsString(request);

        // when

        // then
        mockMvc.perform(
                post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.alias").value(alias)
        );
        assertThat(service.getLink(alias)).isEqualTo(url);
    }

    @Test
    void whenCreateLinkWithoutAlias_thenAliasCreatedAndLinkSaved() throws Exception {
        // given
        String url = "https://www.google.com/";
        Request request = new Request(url, null);
        String stringRequest = objectMapper.writeValueAsString(request);

        // when

        // then
        var response = mockMvc.perform(
                post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest)
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.alias").isNotEmpty()
        ).andReturn().getResponse().getContentAsString();
        String alias = objectMapper.readValue(response, Response.class).alias();
        assertThat(service.getLink(alias)).isEqualTo(url);
    }

    @Test
    void whenCreateLinkByBadRequest_thenBadRequestReturned() throws Exception {
        // given
        var request = new Request("not url", "alias");
        String stringRequest = objectMapper.writeValueAsString(request);

        // when

        // then
        mockMvc.perform(
                post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest)
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.message").isNotEmpty(),
                jsonPath("$.errors.length()").value(2)
        );
    }

    @Test
    void whenCreateLinkWithExistedAlias_thenBadRequestReturned() throws Exception {
        // given
        String url = "https://www.google.com/";
        String alias = "gooogle";
        service.createLink(new Request(url, alias));
        var request = new Request("https://www.google.com/123", alias);
        String stringRequest = objectMapper.writeValueAsString(request);

        // when

        // then
        mockMvc.perform(
                post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest)
        ).andExpectAll(
                status().isBadRequest()
        );
    }
}

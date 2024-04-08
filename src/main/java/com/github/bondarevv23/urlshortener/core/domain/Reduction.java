package com.github.bondarevv23.urlshortener.core.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Entity
@Getter
@Service
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reduction_tab")
public class Reduction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reduction_seq")
    @SequenceGenerator(name = "reduction_seq", allocationSize = 10)
    private Long id;

    @Column(name = "alias", unique = true)
    private String alias;

    @Column(name = "url")
    private String url;

}

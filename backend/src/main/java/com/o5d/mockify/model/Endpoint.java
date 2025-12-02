/* (C)2025 */
package com.o5d.mockify.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String method;

    private int delay;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private String encoding;

    @Column(nullable = false)
    private String responseType;

    @Column(nullable = false)
    private String responseStatus;

    private String jwt;

    @Lob
    @Column(nullable = false)
    private String body;

    @Builder.Default private boolean status = true;

    @Builder.Default private boolean security = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Project project;

    @Builder.Default
    @OneToMany(mappedBy = "endpoint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Header> headers = new ArrayList<>();
}

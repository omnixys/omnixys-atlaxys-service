package com.omnixys.address.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "language")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Language extends BaseEntity {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(length = 2, unique = true)
    private String iso2;

    @Column(length = 3, unique = true)
    private String iso3;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "languages")
    private List<Country> countries = new ArrayList<>();
}

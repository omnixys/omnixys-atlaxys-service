package com.omnixys.atlaxys.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(
        name = "state",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"country_id", "name"}),
                @UniqueConstraint(columnNames = {"country_id", "code"})
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class State extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @EqualsAndHashCode.Include
    @Column(nullable = false, length = 20)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(nullable = false, length = 150)
    private String name;

    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL)
    private List<City> cities = new ArrayList<>();
}


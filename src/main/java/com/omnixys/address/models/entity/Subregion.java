package com.omnixys.address.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "subregion",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "continent_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Subregion extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "continent_id")
    private Continent continent;

    @OneToMany(mappedBy = "subregion")
    private List<Country> countries = new ArrayList<>();
}

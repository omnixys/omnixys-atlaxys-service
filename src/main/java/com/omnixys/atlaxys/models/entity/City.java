package com.omnixys.atlaxys.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.locationtech.jts.geom.Point;


@Entity
@Table(name = "city")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class City extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "geography(Point,4326)")
    private Point location;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
    private List<PostalCode> postalCodes = new ArrayList<>();
}


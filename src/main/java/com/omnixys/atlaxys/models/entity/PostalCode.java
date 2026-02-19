package com.omnixys.atlaxys.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.locationtech.jts.geom.Point;


@Entity
@Table(name = "postal_code")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class PostalCode extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(nullable = false, length = 20)
    private String zip;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Column(columnDefinition = "geography(Point,4326)")
    private Point location;

    @Column(name = "accuracy")
    private Integer accuracy;

    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;
}

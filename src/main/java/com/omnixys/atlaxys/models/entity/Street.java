package com.omnixys.atlaxys.models.entity;


import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.awt.PointShapeFactory;
import org.locationtech.jts.geom.Point;

import java.util.UUID;

@Entity
@Table(
        name = "street",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_street_city_name", columnNames = {"city_id", "name"})
        },
        indexes = {
                @Index(name = "idx_street_city", columnList = "city_id")
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Street extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Represents a point location for the street (e.g., centroid).
     * Stored as GEOGRAPHY(Point, 4326) in PostGIS.
     */
    @Column(name = "location", columnDefinition = "geography(Point,4326)")
    private Point location;
}

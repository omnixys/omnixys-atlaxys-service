package com.omnixys.address.models.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.UUID;

@Entity
@Table(
        name = "address",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_address",
                        columnNames = {"country_id", "city_id", "street_id", "house_number"}
                )
        },
        indexes = {
                @Index(name = "idx_address_city", columnList = "city_id"),
                @Index(name = "idx_address_street", columnList = "street_id")
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Address extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "postal_code_id", nullable = false)
    private PostalCode postalCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "street_id", nullable = false)
    private Street street;

    @Column(name = "house_number", nullable = false, length = 40)
    private String houseNumber;

    @Column(columnDefinition = "geography(Point,4326)")
    private Point location;
}

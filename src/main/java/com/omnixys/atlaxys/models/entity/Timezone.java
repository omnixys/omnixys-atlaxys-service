package com.omnixys.atlaxys.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "timezone")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Timezone extends BaseEntity {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "utc_offset", nullable = false, unique = true, length = 20)
    private String utcOffset;

    @ManyToMany(mappedBy = "timezones")
    private List<Country> countries = new ArrayList<>();
}

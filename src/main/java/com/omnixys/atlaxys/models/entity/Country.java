package com.omnixys.atlaxys.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "country")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Country extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 2)
    private String iso2;

    @Column(nullable = false, unique = true, length = 3)
    private String iso3;

    @Column(nullable = false)
    private String flagSvg;

    @Column(nullable = false)
    private String flagPng;

    @Column(nullable = false)
    private String nativeName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "continent_id")
    private Continent continent;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subregion_id")
    private Subregion subregion;

    @ManyToMany
    @JoinTable(
            name = "country_currency",
            joinColumns = @JoinColumn(name = "country_id"),
            inverseJoinColumns = @JoinColumn(name = "currency_id")
    )
    private List<Currency> currencies = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "country_language",
            joinColumns = @JoinColumn(name = "country_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private List<Language> languages = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "country_timezone",
            joinColumns = @JoinColumn(name = "country_id"),
            inverseJoinColumns = @JoinColumn(name = "timezone_id")
    )
    private List<Timezone> timezones = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "country_calling_code",
            joinColumns = @JoinColumn(name = "country_id"),
            inverseJoinColumns = @JoinColumn(name = "calling_code_id")
    )
    private List<CallingCode> callingCodes = new ArrayList<>();


    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
    private List<State> states = new ArrayList<>();
}



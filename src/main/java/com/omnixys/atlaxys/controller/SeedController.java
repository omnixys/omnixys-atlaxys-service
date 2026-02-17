package com.omnixys.atlaxys.controller;

import com.omnixys.atlaxys.services.CountrySeederService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/seed")
@RequiredArgsConstructor
public class SeedController {

    private final CountrySeederService seeder;

    @PostMapping("/countries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> seedCountries() {
        seeder.seedCountries();
        return ResponseEntity.ok("Countries seeded successfully");
    }
}


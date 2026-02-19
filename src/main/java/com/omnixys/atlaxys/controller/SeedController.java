package com.omnixys.atlaxys.controller;

import com.omnixys.atlaxys.services.CountrySeederService;
import com.omnixys.atlaxys.services.GermanyImportService;
import com.omnixys.atlaxys.services.GlobalPostalImportService;
import com.omnixys.atlaxys.services.StateSeederService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/admin/seed")
@RequiredArgsConstructor
public class SeedController {

    private final CountrySeederService countrySeederService;
    private final StateSeederService stateSeederService;
    private final GermanyImportService importService;
    private final GlobalPostalImportService globalPostalImportService;

    @PostMapping("/countries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> seedCountries() {
        countrySeederService.seedCountries();
        return ResponseEntity.ok("Countries seeded successfully");
    }

    @PostMapping("/states")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> seedStates() {
        stateSeederService.seedStates();
        return ResponseEntity.ok("States seeded successfully");
    }

    @PostMapping("/de")
    public ResponseEntity<String> importGermany() throws Exception {
        System.out.println(Files.exists(Path.of("data/DE.txt")));

        importService.importGermany(
                Path.of("data/DE.txt") // <- HIER PFAD PRÜFEN
        );

        return ResponseEntity.ok("Germany import finished");
    }

    @PostMapping("/postal-code")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> importPostal() throws Exception {

        globalPostalImportService.importFile(
                Path.of("data/allCountries.txt")
        );

        return ResponseEntity.ok("Postal import finished");
    }

}


package org.got.web.gotweb.init.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.got.web.gotweb.init.service.DataInitializationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur non sécurisé pour initialiser les données d'exemple
 * À utiliser uniquement en développement
 */
@Slf4j
@RestController
@RequestMapping("/api/public/init")
@RequiredArgsConstructor
public class DataInitializationController {

    private final DataInitializationService initializationService;

    @PostMapping("/data")
    public ResponseEntity<String> initializeData() {
        try {
            initializationService.initializeData();
            return ResponseEntity.ok("Données initialisées avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation des données", e);
            return ResponseEntity.internalServerError()
                    .body("Erreur lors de l'initialisation des données: " + e.getMessage());
        }
    }
}

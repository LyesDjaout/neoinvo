/* package com.neoinvo.invoice.service.impl;

import com.neoinvo.invoice.service.SiretValidatorService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class SiretValidatorServiceImpl implements SiretValidatorService {

    @Override
    public boolean isValidSiret(String siret) {
        if (siret == null || !siret.matches("\\d{14}")) {
            return false;
        }

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://entreprise.data.gouv.fr/api/sirene/v3/etablissements/" + siret))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Vérifier que la réponse contient bien des données d'établissement
            return response.statusCode() == 200 &&
                    response.body().contains("\"etablissement\"");

        } catch (IOException | InterruptedException e) {
            // Log l'erreur (à implémenter avec un vrai logger)
            System.err.println("Erreur lors de la validation SIRET: " + e.getMessage());
            return false;
        }
    }
}*/

package com.neoinvo.invoice.service.impl;

import com.neoinvo.invoice.service.SiretValidatorService;
import org.springframework.stereotype.Service;

@Service
public class SiretValidatorServiceImpl implements SiretValidatorService {

    @Override
    public boolean isValidSiret(String siret) {
        if (siret == null || !siret.matches("\\d{14}")) {
            return false;
        }
        // Validation mockée : accepte les SIRET commençant par '1' ou '2'
        return siret.startsWith("1") || siret.startsWith("2");
    }
}
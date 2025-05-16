package com.neoinvo.invoice.service.impl;

import com.neoinvo.invoice.service.SiretValidatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SiretValidatorServiceImpl implements SiretValidatorService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean isValidSiret(String siret) {
        try {
            String url = "https://entreprise.data.gouv.fr/api/sirene/v3/etablissements/" + siret;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // Si 200 OK et contient un établissement, c'est valide
            return response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().contains("\"etablissement\":");
        } catch (Exception e) {
            return false;
        }
    }
}

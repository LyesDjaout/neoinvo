package com.neoinvo.invoice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SiretValidatorService {

    public boolean isValidSiret(String siret) {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.insee.fr/entreprises/sirene/V3/siret/" + siret)
                .toUriString();

        try {
            RestTemplate restTemplate = new RestTemplate();
            var response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}
package com.globus.damiaadapter.service;

import com.globus.damiaadapter.client.DamiaApiClient;
import com.globus.damiaadapter.dto.external.CompanyFinancialScoringResponse;
import com.globus.damiaadapter.dto.external.CompanyRiskScoringResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@Slf4j
public class DamiaService {
    private final DamiaApiClient damiaApiClient;
    private final String apiKey;
    private final String userAgent;

    public DamiaService(DamiaApiClient damiaApiClient,
                        @Value("${damia.apitoken}") String apiKey,
                        @Value("${damia.useragent}") String userAgent) {
        this.damiaApiClient = damiaApiClient;
        this.apiKey = apiKey;
        this.userAgent = userAgent;
    }

    public Optional<CompanyFinancialScoringResponse> getCompanyCoefficients(String inn) {
        log.info("Requesting financial scoring for INN: {}", inn);

        try {
            Optional<CompanyFinancialScoringResponse> response = damiaApiClient
                    .getFinancialScoring(inn, apiKey, userAgent);

            if (response.get().getCompanies() == null
                                   || response.get().getCompanies().isEmpty()) {
                log.warn("Empty financial scoring data received for INN: {}", inn);
                return Optional.empty();
            }

            log.info("Successfully received financial scoring for INN: {}", inn);
            return response;

        } catch (FeignException e) {
            log.error("Feign client error while getting financial scoring for INN: {} - Status: {}, Message: {}",
                    inn, e.status(), e.contentUTF8(), e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error while processing financial scoring for INN: {}", inn, e);
            return Optional.empty();
        }
    }

    public Optional<CompanyRiskScoringResponse> getCompanyRiskScoring(String inn, String model) {
        log.info("Requesting risk scoring for INN: {}, Model: {}", inn, model);

        try {
            Optional<CompanyRiskScoringResponse> response = damiaApiClient
                    .getRiskScoring(inn, model, apiKey, userAgent);

            if (response.isEmpty() || response.get().getCompanies() == null
                    || response.get().getCompanies().isEmpty()) {
                log.warn("Empty risk scoring data received for INN: {}, Model: {}", inn, model);
                return Optional.empty();
            }

            log.info("Successfully received risk scoring for INN: {}, Model: {}", inn, model);
            return response;

        } catch (FeignException e) {
            log.error("Feign client error while getting risk scoring for INN: {}, Model: {} - Status: {}, Message: {}",
                    inn, model, e.status(), e.contentUTF8(), e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error while processing risk scoring for INN: {}, Model: {}", inn, model, e);
            return Optional.empty();
        }
    }
}
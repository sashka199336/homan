package com.globus.damiaadapter.client;

import com.globus.damiaadapter.dto.external.CompanyRiskScoringResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import com.globus.damiaadapter.dto.external.CompanyFinancialScoringResponse;
import java.util.Optional;

@FeignClient(name = "damia-api",
             url = "${damia.integration-api.scoring}")
public interface DamiaApiClient {
    @GetMapping("${damia.integration-api.endpoint-scoring}")
    Optional<CompanyFinancialScoringResponse> getFinancialScoring(
            @RequestParam("inn") String inn,
            @RequestParam("key") String apiKey,
            @RequestHeader("User-Agent") String userAgent
    );

    @GetMapping("${damia.integration-api.endpoint-risk}")
    Optional<CompanyRiskScoringResponse> getRiskScoring(
            @RequestParam("inn") String inn,
            @RequestParam("model") String model,
            @RequestParam("key") String apiKey,
            @RequestHeader("User-Agent") String userAgent
    );
}
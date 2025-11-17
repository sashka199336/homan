package com.globus.claim_service.dto.criteria;

import com.globus.claim_service.dto.ClaimFilter;
import com.globus.claim_service.model.Claim;
import com.globus.claim_service.model.enums.ClaimStatus;
import org.springframework.data.jpa.domain.Specification;
import java.util.UUID;

public interface ClaimSearchCriteria {
    static Specification<Claim> withFilter(ClaimFilter filter) {
        return Specification.allOf(ClaimSearchCriteria.byCustomerId(filter.getCustomerId())
                .and(ClaimSearchCriteria.byClaimStatus(filter.getClaimStatus())));
    }

    static Specification<Claim> byCustomerId(UUID customerId) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get(Claim.Fields.customerId), customerId);
        }


    static Specification<Claim> byClaimStatus(ClaimStatus claimStatus) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Claim.Fields.status), claimStatus);
        }
}

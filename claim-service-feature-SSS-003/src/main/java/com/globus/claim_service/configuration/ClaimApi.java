package com.globus.claim_service.configuration;

import com.globus.claim_service.dto.ClaimDto;
import com.globus.claim_service.dto.ClaimRequestParams;
import com.globus.claim_service.dto.ClaimsResponse;
import com.globus.claim_service.dto.credit.CreditClaimRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/claims")
@Tag(name = "Claim API", description = "Операции с заявками клиентов")
public interface ClaimApi {

    @Operation(
            summary = "Получить список заявок клиента",
            description = "Возвращает список заявок с указанием id клиента, типа и статуса заявки. "
                    + "Список выдается постранично. Все параметры необязательны.",
            tags = {"Get"})
    @ApiResponse(
            responseCode = "200",
            description = "Список заявок успешно получен",
            content = @Content(schema = @Schema(implementation = ClaimsResponse.class)))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<ClaimDto> getAllClaims(
            @Parameter(description = "ID клиента") @PathVariable UUID customerId,
            @Parameter(hidden = true) @Valid ClaimRequestParams params);


    @Operation(
            summary = "Получить заявку по номеру.",
            description = "Возвращает заявку с указанием id клиента, типа и статуса заявки.",
            tags = {"Get"})
    @ApiResponse(
            responseCode = "200",
            content = {@Content(schema = @Schema(
                    implementation = ClaimDto.class), mediaType = "application/json")})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ClaimDto getClaimById(@Parameter(description = "ID заявки") @PathVariable UUID claimId);

    @Operation(
            summary = "Заявка на кредит.",
            description = "Автоматически создается заявка на кредит и отправляется в кредитный сервис." +
                    "В ответ ожидается решение по заявке",
            tags = {"Post"})
    @ApiResponse(
            responseCode = "201")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void createCreditClaim(@Parameter(description = "В заголовке передается id юзера")
                           @RequestHeader("X-User-Id") UUID userId,
                           @Parameter(description = "В заявке указывается id клиента, тип заявки, сумма и срок кредита")
                           @RequestBody CreditClaimRequest creditClaimRequest);

    @Operation(
            summary = "Имитация подписания кредитного договора.",
            description = "В кредитный сервис отправляется номер заявки, по которой  клиент подписал кредитный договор." +
                    "В ответ от кредитного сервиса ожидается изменение статуса заявки на FINANCED",
            tags = {"Post"})
    @ApiResponse(
            responseCode = "200")
    @PostMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void acceptCredit(@Parameter(description = "Номер заявки, в рамках которой клиентом подписан кредитный договор")
                      @PathVariable UUID claimId);

    @Operation(
            summary = "Удалить заявку по идентификатору.",
            description = "Удаляет заявку по идентификатору",
            tags = {"Delete"})
    @ApiResponse(
            responseCode = "204")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteClaim(@Parameter(description = "Номер заявки на удаление") @PathVariable UUID claimId);
}

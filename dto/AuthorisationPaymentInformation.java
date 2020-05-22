package com.bangtechnologies.qrscanner.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Component
@Data
@NoArgsConstructor
public class AuthorisationPaymentInformation {

    @NotBlank
    private String authorizationId;

    @NotBlank
    private String merchantId;

    @NotBlank
    private String amount;

    @NotBlank
    private String currency;

    @NotBlank
    private String transactionTime;

    private String reference;

    private String description;

    private String invoiceId;
}

package com.bangtechnologies.qrscanner.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Component
@Data
@NoArgsConstructor
public class RetryPaymentInformation {

    @NotBlank
    private String cardNumber;

    @NotBlank
    private String expiryMonth;

    @NotBlank
    private String expiryYear;

    //@NotBlank
    private String CVV;

    @NotBlank
    private String cardHolderName;

    @NotBlank
    private String merchantId;

    @NotBlank
    private String amount;

    @NotBlank
    private String currency;

    @NotBlank
    private String cardType;

    @NotBlank
    private String transactionTime;

    @NotBlank
    private String invoiceId;

    private String description;

    @NotBlank
    private String replayId;
}

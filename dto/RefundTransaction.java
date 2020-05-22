package com.bangtechnologies.qrscanner.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class RefundTransaction {

    @NotBlank
    private int amount;

    @NotBlank
    private int merchantId;

    private String paymentId;

    private String reason;

    private String reference;

    private int invoiceId;
}

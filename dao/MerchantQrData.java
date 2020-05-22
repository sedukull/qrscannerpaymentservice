package com.bangtechnologies.qrscanner.dao;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;


@Data
@RequiredArgsConstructor
public class MerchantQrData {

    @Id
    private String id;

    @NotBlank
    private String merchantId;

    @NotBlank
    private String transactionTime;

    @NotBlank
    private String invoiceId;

    @Max(value = 100, message = "Description should not be more than 100 characters")
    private String description;

    private String QrData;

    private byte[] QrDataImage;
}

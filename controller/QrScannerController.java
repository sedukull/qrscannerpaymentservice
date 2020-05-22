package com.bangtechnologies.qrscanner.controller;

import com.bangtechnologies.qrscanner.dto.AuthorisationPaymentInformation;
import com.bangtechnologies.qrscanner.dto.CardTokenPaymentInformation;
import com.bangtechnologies.qrscanner.dto.PaymentInformation;
import com.bangtechnologies.qrscanner.dto.RefundTransaction;
import com.bangtechnologies.qrscanner.service.PaymentService;
import com.bangtechnologies.qrscanner.service.QrDataConverterService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/payments")
public class QrScannerController {

    private Logger log = LoggerFactory.getLogger(QrScannerController.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private QrDataConverterService qrDataConverterService;

    @Bean
    public RestTemplate rest(RestTemplateBuilder builder) {
        return builder.build();
    }


    /**
     * Returns the hello message response string with input name.
     * @return the string
     */
    @RequestMapping(
            value = "/hello/{input}",
            method = RequestMethod.GET)
    public @ResponseBody String makeHello(@PathVariable String input) {
        return "Hello " + input;
    }

    /**
     * Makes a payment with given card information.
     * @return
     */
    @RequestMapping(
            value = "/pay",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<Object> makePaymentWithCard(@Valid @RequestBody PaymentInformation paymentInformationInfo) throws Exception {

        Map output =  paymentService.payWithCard(paymentInformationInfo);
        HttpStatus httpStatus = HttpStatus.OK;

        if (!output.get("status").toString().toLowerCase().equals("approved")) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(output, httpStatus);
    }

    /**
     * Makes a payment with given authorization information.
     * @return
     */
    @RequestMapping(
            value = "/pay/authorize",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<Object> makePaymentWithAuthorization(@Valid @RequestBody AuthorisationPaymentInformation paymentInformationInfo) throws Exception {

        Map output =  paymentService.payWithAuthorization(paymentInformationInfo);
        HttpStatus httpStatus = HttpStatus.OK;

        if (!output.get("status").toString().toLowerCase().equals("approved")) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(output, httpStatus);
    }


    /**
     * Retry a payment.
     * @return
     */
    @RequestMapping(
            value = "/pay/retry",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<Object> retryPayment(@Valid @RequestBody AuthorisationPaymentInformation paymentInformationInfo) throws Exception {

        Map output =  paymentService.payWithAuthorization(paymentInformationInfo);
        HttpStatus httpStatus = HttpStatus.OK;

        if (!output.get("status").toString().toLowerCase().equals("approved")) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(output, httpStatus);
    }


    /**
     * Makes a payment with card token information.
     * @return
     */
    @RequestMapping(
            value = "/pay/token",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<Object> makePaymentWithCardToken(@Valid @RequestBody CardTokenPaymentInformation paymentInformationInfo) throws Exception {

        Map output =  paymentService.payWithCardToken(paymentInformationInfo);
        HttpStatus httpStatus = HttpStatus.OK;

        if (!output.get("status").toString().toLowerCase().equals("approved")) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(output, httpStatus);
    }


    /**
     * Makes a payment with given payment information.
     * @return
     */
    @RequestMapping(
            value = "/refund",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {"application/json", "application/xml"})
    public ResponseEntity<Object> refundPayment(@Valid @RequestBody RefundTransaction refundTransaction) throws Exception {

        Map output =  paymentService.refundPayment(refundTransaction);

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (output.get("status").toString().toLowerCase().equals("approved") &&
                output.get("refunded").equals(true)) {
            httpStatus = HttpStatus.OK;
        }

        if (!output.get("status").toString().toLowerCase().equals("approved")) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(output, httpStatus);
    }


    /**
     * Returns the hello message response string with input name.
     * @return the string
     */
    @RequestMapping(
            value = "/test/qrcode/{paymentInfo}",
            method = RequestMethod.POST,
            produces = {"application/json", "application/xml"}
            )
    public @ResponseBody ResponseEntity<Object> generateQRCode(@PathVariable String paymentInfo) throws Exception {
        String uuid =  qrDataConverterService.createQRCode(paymentInfo);
        return new ResponseEntity<>(uuid, HttpStatus.OK);
    }


    @RequestMapping(
            value = "/test/qrdata/{uuid}",
            method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public @ResponseBody ResponseEntity<Object> getQRCode(@PathVariable String uuid) throws Exception {
        return new ResponseEntity<>(qrDataConverterService.readQRCode(uuid), HttpStatus.OK);
    }


    @RequestMapping(
            value = "/test/qrimage/{paymentInfo}",
            method = RequestMethod.GET,
            produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody ResponseEntity<byte[]>  getQRCodeImage(@PathVariable String paymentInfo) throws Exception {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        byte[] bArray =  qrDataConverterService.createQRCodeImage(paymentInfo);
        return new ResponseEntity<>(bArray, headers, HttpStatus.CREATED);
    }
}

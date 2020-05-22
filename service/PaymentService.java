package com.bangtechnologies.qrscanner.service;

import com.bangtechnologies.qrscanner.dto.*;
import com.simplify.payments.domain.Payment;
import com.simplify.payments.domain.Refund;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.simplify.payments.PaymentsApi;
import com.simplify.payments.PaymentsMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class PaymentService {

    private Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Value("${payment.gateway.api_key}")
    private String apiKey;

    @Value("${payment.gateway.secret_key}")
    private String secretKey;

    private String generateReplayId() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 10;
        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }

    /**
     * pay with card details
     *
     * @param paymentInformation
     * @return
     * @throws Exception
     */
    public Map payWithCard(final PaymentInformation paymentInformation) throws Exception {

        PaymentsApi.PUBLIC_KEY = apiKey;
        PaymentsApi.PRIVATE_KEY = secretKey;

        Map output = new HashMap();
        String replayId = generateReplayId();

        Payment paymentTransaction = Payment.create(new PaymentsMap()
                .set("amount", paymentInformation.getAmount())
                .set("card.cvc", paymentInformation.getCVV())
                .set("card.expMonth", paymentInformation.getExpiryMonth())
                .set("card.expYear", paymentInformation.getExpiryYear())
                .set("card.number", paymentInformation.getCardNumber())
                .set("description", paymentInformation.getDescription())
                //.set("invoice", paymentInformation.getInvoiceId())
                .set("replayId", generateReplayId())
        );
        log.info("MerchantId: {}, InvoiceId: {} Status: {} ReplayId: {} Id: {}", paymentInformation.getMerchantId(),
                paymentInformation.getInvoiceId(), paymentTransaction.get("paymentStatus"), replayId, paymentTransaction.get("id"));

        output.put("status", paymentTransaction.get("paymentStatus"));
        return output;
    }


    /**
     * Pay with Authorization information.
     *
     * @param paymentInformation
     * @return
     * @throws Exception
     */
    public Map payWithAuthorization(final AuthorisationPaymentInformation paymentInformation) throws Exception {

        PaymentsApi.PUBLIC_KEY = apiKey;
        PaymentsApi.PRIVATE_KEY = secretKey;

        Map output = new HashMap();


        String replayId = generateReplayId();

        Payment payment = Payment.create(new PaymentsMap()
                .set("amount", paymentInformation.getAmount())
                .set("authorization", paymentInformation.getAuthorizationId())
                .set("currency", paymentInformation.getCurrency())
                .set("description", paymentInformation.getDescription())
                .set("reference", paymentInformation.getReference())
                .set("replayId", replayId)
        );
        log.info("MerchantId: {}, InvoiceId: {} Status: {}  Reference: {} ReplayId: {} Id: {}", paymentInformation.getMerchantId(),
                paymentInformation.getInvoiceId(), payment.get("paymentStatus"), paymentInformation.getReference(), payment.get("id"));

        output.put("status", payment.get("paymentStatus"));

        return output;
    }


    /**
     * Pay with card token information.
     *
     * @param paymentInformation
     * @return
     * @throws Exception
     */
    public Map payWithCardToken(final CardTokenPaymentInformation paymentInformation) throws Exception {
        PaymentsApi.PUBLIC_KEY = apiKey;
        PaymentsApi.PRIVATE_KEY = secretKey;

        Map output = new HashMap();

        Payment payment = Payment.create(new PaymentsMap()
                .set("amount", paymentInformation.getAmount())
                .set("currency", paymentInformation.getCurrency())
                .set("description", paymentInformation.getDescription())
                .set("reference", paymentInformation.getReference())
                .set("token", paymentInformation.getToken())
        );
        log.info("MerchantId: {}, InvoiceId: {} Status: {}  Reference: {} Id: {}", paymentInformation.getMerchantId(),
                paymentInformation.getInvoiceId(), payment.get("paymentStatus"), paymentInformation.getReference(), payment.get("id"));

        output.put("status", payment.get("paymentStatus"));
        return output;
    }

    /**
     * Retry payment with card details
     *
     * @param paymentInformation
     * @return
     * @throws Exception
     */
    public Map payWithCard(final RetryPaymentInformation paymentInformation) throws Exception {

        PaymentsApi.PUBLIC_KEY = apiKey;
        PaymentsApi.PRIVATE_KEY = secretKey;

        Map output = new HashMap();
        String replayId = paymentInformation.getReplayId();

        Payment paymentTransaction = Payment.create(new PaymentsMap()
                .set("amount", paymentInformation.getAmount())
                .set("card.cvc", paymentInformation.getCVV())
                .set("card.expMonth", paymentInformation.getExpiryMonth())
                .set("card.expYear", paymentInformation.getExpiryYear())
                .set("card.number", paymentInformation.getCardNumber())
                .set("description", paymentInformation.getDescription())
                //.set("invoice", paymentInformation.getInvoiceId())
                .set("replayId", paymentInformation.getReplayId())
        );
        log.info("MerchantId: {}, InvoiceId: {} Status: {} ReplayId: {} Id: {}", paymentInformation.getMerchantId(),
                paymentInformation.getInvoiceId(), paymentTransaction.get("paymentStatus"), replayId, paymentTransaction.get("id"));

        output.put("status", paymentTransaction.get("paymentStatus"));
        return output;
    }

    /**
     * Refund payments.
     */
    public Map refundPayment(final RefundTransaction refundTransaction) throws Exception {
        PaymentsApi.PUBLIC_KEY = apiKey;
        PaymentsApi.PRIVATE_KEY = secretKey;

        Map output = new HashMap();

        Refund refund = Refund.create(new PaymentsMap()
                .set("amount", refundTransaction.getAmount())
                .set("payment", refundTransaction.getPaymentId())
                .set("reason", refundTransaction.getReason())
                .set("reference", refundTransaction.getReference())
        );

        Map refundMap = (Map) refund.get("refunds");

        log.info("MerchantId: {}, InvoiceId: {} Status: {}  Reference: {} Id: {} Refunded: {} RefundedId: {}", refundTransaction.getMerchantId(),
                refundTransaction.getInvoiceId(), refund.get("paymentStatus"), refundTransaction.getReference(), refund.get("id"),
                refund.get("refunded"), refundMap.get("id"));

        output.put("status", refund.get("paymentStatus"));
        output.put("status", refund.get("refunded"));
        return output;
    }
}

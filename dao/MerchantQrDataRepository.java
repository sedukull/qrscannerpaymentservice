package com.bangtechnologies.qrscanner.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
@Repository
public interface MerchantQrDataRepository extends MongoRepository<MerchantQrData, String> {

    public List<MerchantQrData> findByMerchantId(String merchantId);
    public MerchantQrData findByInvoiceId (String lastName);
}

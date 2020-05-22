package com.bangtechnologies.qrscanner.service;

import com.bangtechnologies.qrscanner.dao.MerchantQrDataRepository;
import com.bangtechnologies.qrscanner.dao.MerchantQrData;
import com.bangtechnologies.qrscanner.dto.PaymentInformation;
import lombok.extern.slf4j.Slf4j;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class QrDataConverterService {

    @Value("${QrData.width}")
    private int qrCodewidth;

    @Value("${QrData.height}")
    private int qrCodeheight;

    @Value("${QrData.basepath}")
    private String qrCodeSavePath;

    @Autowired
    private MerchantQrDataRepository merchantQrDataRepository;

    private Logger log = LoggerFactory.getLogger(QrDataConverterService.class);

    public PaymentInformation toPaymentInformation(final String QrCodeData) {
        return new PaymentInformation();
    }

    public String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private String getUUID() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest salt = MessageDigest.getInstance("SHA-256");
        salt.update(UUID.randomUUID()
                .toString()
                .getBytes("UTF-8"));
        String digest = bytesToHex(salt.digest());
        return digest;
    }

    public String createQRCode(final String qrCodeData) throws Exception {
        String uuid = getUUID();
        String charset = "UTF-8";
        Map hintMap = new HashMap();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(qrCodeData.getBytes(charset), charset),
                BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
        Path path = FileSystems.getDefault().getPath(String.format("%s/%s.png", qrCodeSavePath,  uuid));
        log.info("{}, {}", path.getFileName(), path.toString());
        MatrixToImageWriter.writeToPath(matrix, "PNG", path);
        log.info("QR Code generated. UUID: {}", uuid);
        return uuid;
    }

    public String readQRCode(final String uuid)
            throws IOException, NotFoundException {
        Map hintMap = new HashMap();
        String filePath = String.format("%s/%s.png", qrCodeSavePath, uuid);
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                        ImageIO.read(new FileInputStream(filePath)))));
        Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap,
                hintMap);
        String qrCode = qrCodeResult.getText();
        log.info("UUID: {}, output: {}", uuid, qrCode);
        return qrCode;
    }

    public byte[] createQRCodeImage(final String qrCodeData) throws Exception {
        String uuid = getUUID();
        String charset = "UTF-8";
        Map hintMap = new HashMap();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(qrCodeData.getBytes(charset), charset),
                BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
        Path path = FileSystems.getDefault().getPath(String.format("%s/%s.png", qrCodeSavePath, uuid));
        MatrixToImageWriter.writeToPath(matrix, "png", path);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
        log.info("UUID: {}", uuid);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", bos );
        return bos.toByteArray();
    }
}

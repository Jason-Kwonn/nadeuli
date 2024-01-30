package kr.nadeuli.makesignature;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MakeSignature {
    public String makeSignature() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";					// 공백
        String newLine = "\n";  				// 줄바꿈
        String method = "GET";  				// HTTP 메서드
        String url = "/api/v2/channels?pageNo=1";	// 도메인을 제외한 "/" 아래 전체 url (쿼리스트링 포함)
        String accessKey = "A7YXXDFuySF3KfWwBjQg";		// access key id (from portal or Sub Account)
        String secretKey = "BoekwrkmB32dJJCRSNSYrdkguTVLFZs50Vkh17Fx\n";		// secret key (from portal or Sub Account)
        String timestamp = String.valueOf(System.currentTimeMillis());		// 현재 타임스탬프 (epoch, millisecond)

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);
        System.out.println("Timestamp: " + timestamp);
        System.out.println("encodeBase64String: " + timestamp);
        return encodeBase64String;
    }
}

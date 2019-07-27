package com.fih.aiovpoint.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fih.aiovpoint.pojo.AccessToken;
import com.fih.aiovpoint.pojo.AccountProfile;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
@Component
public class AccountService {

    @Autowired
    private RestTemplate restTemplate;
//    @Value("${account.api.access-token}")
//    private String accessTokenUrl;
//    @Value("${account.api.access-token.authorization}")
//    private String accessTokenAuthorization;
    @Value("${account.api.account-profile}")
    private String accountProfileUrl;

    @Value("${account.api.getAccessToken.Authorization}")
    private String getAccessTokenAuthorization;
    @Value("${account.api.getAccessToken.url}")
    private String getAccessTokenURL;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${auth.AccessKeyId}")
    private String AccessKeyId;
    @Value("${auth.AccessKeySecret}")
    private String AccessKeySecret;
    @Value("${auth.SignatureMethod}")
    private String SignatureMethod;
    @Value("${auth.Version}")
    private String Version;
    @Value("${auth.SignatureVersion}")
    private String SignatureVersion;
    @Value("${auth.SignatureNonce}")
    private String SignatureNonce;

    public AccountProfile getAccountProfile(String accessToken) {
        String Timestamp = Long.toString(new Date().getTime());
        String Signature = this._generateSignature(Timestamp);
        HttpHeaders headers = generateHeaders(Signature, Timestamp);
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        AccountProfile profile = null;
        try {
            String result = restTemplate.exchange(accountProfileUrl, HttpMethod.GET, httpEntity, String.class).getBody();
            if (!StringUtils.isEmpty(result)) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                profile = mapper.readValue(result, AccountProfile.class);
            }
        } catch (Exception e) {
            logger.error(String.format("getAccountProfile error: %s", e.getMessage()));
            return null;
        }
        return profile;
    }

    public String[] getSignature() {
        String Timestamp = Long.toString(new Date().getTime());
        String[] signatureAry = {
                Version,
                AccessKeyId,
                "HMAC-SHA1",
                Timestamp,
                SignatureVersion,
                SignatureNonce,
                ""
        };
        String Signature = this._generateSignature(Timestamp);
        signatureAry[6] = Signature;
        return signatureAry;
    }

    public AccessToken getTokenForTest(String signature, String time) throws Exception{
        HttpHeaders headers = generateHeaders(signature,time);
        headers.add("Authorization", getAccessTokenAuthorization);
        JSONObject json = new JSONObject();
        json.put("auth_type", "phone_password");
        json.put("username","86-19900000005");
        json.put("password","qwer1234");
        json.put("scope","account,display_name,phone");
        json.put("uuid","7e87a3e0-4a31-4e66-85f6-5a1e62a9d467");
        HttpEntity<String> httpEntity = new HttpEntity<>(json.toString(), headers);
        AccessToken token = null;
        try {
            String result = restTemplate.postForObject(getAccessTokenURL, httpEntity, String.class);
            if (!StringUtils.isEmpty(result)) {
                ObjectMapper mapper = new ObjectMapper();
                token = mapper.readValue(result, AccessToken.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    private String _generateSignature(String Timestamp) {
        final Base64.Encoder encoder = Base64.getEncoder();

        String[] signatureAry = {
                Version,
                AccessKeyId,
                "HMAC-SHA1",
                Timestamp,
                SignatureVersion,
                SignatureNonce
        };
        String signatureStr = String.join("\n", signatureAry);
        byte[] decodedKey = AccessKeySecret.getBytes();
        try {
            Mac mac = Mac.getInstance(SignatureMethod);
            SecretKeySpec keySpec = new SecretKeySpec(decodedKey, SignatureMethod);
            mac.init(keySpec);
            //byte[] dataBytes = signatureStr.getBytes("UTF-8");
            byte[] signatureBytes = mac.doFinal(signatureStr.getBytes());
            String signatureEncode = encoder.encodeToString(signatureBytes);
            logger.info("signatureAry:{}", signatureAry);
            logger.info("signatureEncode:{}", signatureEncode);
            return signatureEncode;
        } catch (Exception e) {
            logger.error(String.format("Signature encode error: %s", e.getMessage()));
            return null;
        }
    }

    public HttpHeaders generateHeaders(String signature, String time) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Content-Type","application/json");
        headers.add("Version", Version);
        headers.add("Signature",signature);
        headers.add("SignatureMethod", "HMAC-SHA1");
        headers.add("Timestamp", time);
        headers.add("SignatureVersion", SignatureVersion);
        headers.add("SignatureNonce", SignatureNonce);
        headers.add("AccessKeyId", AccessKeyId);
        return headers;
    }
}

package com.fih.aiovpoint.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

public class ApiHandlerInterceptor extends HandlerInterceptorAdapter {
    private static Logger logger = LoggerFactory.getLogger(ApiHandlerInterceptor.class);

    private String AccessKeySecret="7b710ff6d11fda1c386111a60e5e0702";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        logger.info(String.format("%s %s %s start thread id: %s", request.getRemoteAddr(), request.getMethod(), request.getRequestURI(), Thread.currentThread().getId()));
        String result = "";
        //check signature - start
        if (!this._checkSignatureIsLegal(
                request.getHeader("Version"), request.getHeader("AccessKeyId"),
                request.getHeader("Signature"), request.getHeader("SignatureMethod"),
                request.getHeader("Timestamp"), request.getHeader("SignatureVersion"),
                request.getHeader("SignatureNonce"),
                request.getRemoteAddr()
        )) {
            logger.info(String.format("%s Check signature fail: signature - %s", request.getRemoteAddr(), request.getHeader("Signature")));
            result = "Unauthorized";
            response.getOutputStream().write(result.getBytes());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        // check signature -end
        return true;
    }

    private boolean _checkSignatureIsLegal(String Version, String AccessKeyId, String Signature, String SignatureMethod,
                                           String Timestamp, String SignatureVersion, String SignatureNonce, String IPAddress) {

        if (!StringUtils.hasText(Version)) {
            return false;
        }
        if (!StringUtils.hasText(AccessKeyId)) {
            return false;
        }
        if (!StringUtils.hasText(Signature)) {
            return false;
        }
        if (!StringUtils.hasText(SignatureMethod)) {
            return false;
        }
        if (!StringUtils.hasText(Timestamp)) {
            return false;
        }
        if (!StringUtils.hasText(SignatureVersion)) {
            return false;
        }
        if (!StringUtils.hasText(SignatureNonce)) {
            return false;
        }

        try {
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
                Mac mac = Mac.getInstance("HmacSHA1");
                SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "HmacSHA1");
                mac.init(keySpec);
                //byte[] dataBytes = signatureStr.getBytes("UTF-8");
                byte[] signatureBytes = mac.doFinal(signatureStr.getBytes());
                String signatureEncode = encoder.encodeToString(signatureBytes);
                logger.info("signatureAry:{}", signatureAry);
                logger.info("signatureEncode:{}", signatureEncode);
                return signatureEncode.equals(Signature);
            } catch (Exception e) {
                logger.error(String.format("Signature encode error: %s", e.getMessage()));
                return false;
            }
        }
        catch(Exception e){
            logger.error(" ==== ApiHandlerInterceptor Error @_checkSignatureIsLegal:{}", e.getMessage());
            return false;
        }

    }

}

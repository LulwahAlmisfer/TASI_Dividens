package org.example.tasi_dividens.helpers;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public abstract class JWT {
    /**
     * Generates a JWT token as per Apple's specifications.
     *
     * @param APN_KEY
     * @param keyId   the key id, obtained from developer.apple.com
     * @param teamId  the team id, obtained from developer.apple.com
     * @return The resulting token, which will be valid for one hour
     * @throws InvalidKeySpecException  if the key is incorrect
     * @throws NoSuchAlgorithmException if the key algo failed to load
     * @throws InvalidKeyException      if the key is invalid
     * @throws SignatureException       if this signature object is not initialized properly.
     */
    public static String getApnJwtToken(String APN_KEY, String keyId, String teamId)
            throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        final int now = (int) (System.currentTimeMillis() / 1000);
        final String header = "{\"alg\":\"ES256\",\"kid\":\"" + keyId + "\"}";
        final String payload = "{\"iss\":\"" + teamId + "\",\"iat\":" + now + "}";

        final String part1 = Base64.getEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8))
                + "."
                + Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));

        return part1 + "." + ES256(APN_KEY, part1);
    }

    /**
     * Adopted from http://stackoverflow.com/a/20322894/2274894
     *
     * @param secret The secret
     * @param data   The data to be encoded
     * @return The encoded token
     * @throws InvalidKeySpecException  if the key is incorrect
     * @throws NoSuchAlgorithmException if the key algo failed to load
     * @throws InvalidKeyException      if the key is invalid
     * @throws SignatureException       if this signature object is not initialized properly.
     */
    private static String ES256(final String secret, final String data)
            throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {

        KeyFactory kf = KeyFactory.getInstance("EC");
        KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(secret.getBytes()));
        PrivateKey key = kf.generatePrivate(keySpec);

        final Signature sha256withECDSA = Signature.getInstance("SHA256withECDSA");
        sha256withECDSA.initSign(key);

        sha256withECDSA.update(data.getBytes(StandardCharsets.UTF_8));

        final byte[] signed = sha256withECDSA.sign();
        return Base64.getEncoder().encodeToString(signed);
    }
}

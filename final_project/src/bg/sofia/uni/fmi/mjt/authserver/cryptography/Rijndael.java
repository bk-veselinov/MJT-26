package bg.sofia.uni.fmi.mjt.authserver.cryptography;

import bg.sofia.uni.fmi.mjt.authserver.exception.CipherException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Rijndael implements Cryptography {

    private static final String ENCRYPTION_ALGORITHM = "AES"; // Advanced Encryption Standard

    private final SecretKey secretKey;

    public Rijndael(SecretKey secretKey) {
        if (secretKey == null) {
            throw new IllegalArgumentException("Secret key is null.");
        }
        this.secretKey = secretKey;
    }

    public String encrypt(String text) throws CipherException {
        if (text == null) {
            throw new IllegalArgumentException("The text is null.");
        }

        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] plaintextBytes = text.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedBytes = cipher.doFinal(plaintextBytes);

            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception e) {
            throw new CipherException("Problem while encrypting the data.", e);

        }
    }

    public String decrypt(String encryptedText) throws CipherException {
        if (encryptedText == null) {
            throw new IllegalArgumentException("Encrypted text is null.");
        }

        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new CipherException("Problem while decrypting the data.", e);

        }
    }
}
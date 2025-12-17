package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Rijndael implements SymmetricBlockCipher {

    private static final int BUFFER_SIZE = 1024;
    private static final String ENCRYPTION_ALGORITHM = "AES"; // //  Advanced Encryption Standard

    private final SecretKey secretKey;

    /**
     * Encrypts/decrypts data using AES (Rijndael) algorithm with the provided secret key.
     *
     * @param secretKey the encryption/decryption key
     * @throws IllegalArgumentException if secretKey is null
     */
    public Rijndael(SecretKey secretKey) {
        if (secretKey == null) {
            throw new IllegalArgumentException("Secret key is null");
        }
        this.secretKey = secretKey;
    }

    @Override
    public void encrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        try {
            encryptData(secretKey, inputStream, outputStream);
        } catch (Exception e) {
            throw new CipherException("Problem while encrypting the data", e);
        }
    }

    private void encryptData(SecretKey secretKey,
                             InputStream inputStream,
                             OutputStream outputStream) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        try (var cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
            transferData(inputStream, cipherOutputStream);
        }
    }

    @Override
    public void decrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        try {
            decryptData(secretKey, inputStream, outputStream);
        } catch (Exception e) {
            throw new CipherException("Problem while decrypting the data", e);
        }
    }

    private void decryptData(SecretKey secretKey,
                             InputStream inputStream,
                             OutputStream outputStream) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        try (OutputStream decryptedOutputStream = new CipherOutputStream(outputStream, cipher)) {
            transferData(inputStream, decryptedOutputStream);
        }
    }

    private void transferData(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }
}

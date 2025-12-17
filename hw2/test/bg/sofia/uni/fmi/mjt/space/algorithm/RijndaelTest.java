package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class RijndaelTest {

    private SecretKey secretKey;
    private Rijndael rijndael;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        this.secretKey = keyGenerator.generateKey();
        this.rijndael = new Rijndael(secretKey);
    }

    @Test
    void testConstructorWithNullSecretKey() {
        assertThrows(IllegalArgumentException.class, () -> new Rijndael(null));
    }

    @Test
    void testEncrypt() throws CipherException {
        String originalText = "Test content for encryption";
        InputStream inputStream = new ByteArrayInputStream(originalText.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        rijndael.encrypt(inputStream, outputStream);

        assertNotEquals(originalText, outputStream.toString());
    }

    @Test
    void testDecrypt() throws CipherException {
        String originalText = "Test content for decryption";
        byte[] encryptedData = encryptHelper(originalText);

        InputStream inputStream = new ByteArrayInputStream(encryptedData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        rijndael.decrypt(inputStream, outputStream);

        assertArrayEquals(originalText.getBytes(), outputStream.toByteArray());
    }

    private byte[] encryptHelper(String data) throws CipherException {
        InputStream in = new ByteArrayInputStream(data.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        rijndael.encrypt(in, out);
        return out.toByteArray();
    }

}
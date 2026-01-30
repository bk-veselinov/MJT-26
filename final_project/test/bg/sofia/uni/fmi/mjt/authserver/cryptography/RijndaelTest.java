package bg.sofia.uni.fmi.mjt.authserver.cryptography;

import bg.sofia.uni.fmi.mjt.authserver.exception.CipherException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RijndaelTest {

    private SecretKey validSecretKey;
    private Rijndael rijndael;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        validSecretKey = keyGenerator.generateKey();
        rijndael = new Rijndael(validSecretKey);
    }

    @Test
    void testConstructorWithNullSecretKeyThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Rijndael(null),
                "Constructor should throw IllegalArgumentException when secret key is null");
    }

    @Test
    void testEncryptWithNullPlaintextThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> rijndael.encrypt(null),
                "Encrypt should throw IllegalArgumentException when plaintext is null");
    }

    @Test
    void testDecryptWithNullEncryptedTextThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> rijndael.decrypt(null),
                "Decrypt should throw IllegalArgumentException when encrypted text is null");
    }

    @Test
    void testEncryptAndDecryptSimpleText() throws CipherException {
        String plaintext = "Hello, World!";

        String encrypted = rijndael.encrypt(plaintext);
        String decrypted = rijndael.decrypt(encrypted);

        assertEquals(plaintext, decrypted,
                "Decrypted text should match original plaintext");
    }

    @Test
    void testEncryptAndDecryptEmptyString() throws CipherException {
        String plaintext = "";

        String encrypted = rijndael.encrypt(plaintext);
        String decrypted = rijndael.decrypt(encrypted);

        assertEquals(plaintext, decrypted,
                "Decrypted empty string should match original");
    }

    @Test
    void testEncryptAndDecryptSpecialCharacters() throws CipherException {
        String plaintext = "!@#$%^&*()_+-={}[]|\\:;\"'<>,.?/~`";

        String encrypted = rijndael.encrypt(plaintext);
        String decrypted = rijndael.decrypt(encrypted);

        assertEquals(plaintext, decrypted,
                "Decrypted special characters should match original plaintext");
    }

    @Test
    void testEncryptProducesTheSameOutputForSameInput() throws CipherException {
        String plaintext = "Test message";

        String encrypted1 = rijndael.encrypt(plaintext);
        String encrypted2 = rijndael.encrypt(plaintext);

        assertEquals(encrypted1, encrypted2,
                "Same plaintext should produce same encrypted output with same key");
    }

    @Test
    void testEncryptProducesDifferentOutputWithDifferentKeys() throws Exception {
        String plaintext = "Test message";

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey anotherKey = keyGenerator.generateKey();
        Rijndael anotherRijndael = new Rijndael(anotherKey);

        String encrypted1 = rijndael.encrypt(plaintext);
        String encrypted2 = anotherRijndael.encrypt(plaintext);

        assertNotEquals(encrypted1, encrypted2,
                "Same plaintext with different keys should produce different encrypted output");
    }

    @Test
    void testEncryptedOutputIsDifferentFromPlaintext() throws CipherException {
        String plaintext = "Hello, World!";
        String encrypted = rijndael.encrypt(plaintext);

        assertNotEquals(plaintext, encrypted,
                "Encrypted output should be different from plaintext");
    }

    @Test
    void testEncryptAndDecryptNumericString() throws CipherException {
        String plaintext = "1234567890";

        String encrypted = rijndael.encrypt(plaintext);
        String decrypted = rijndael.decrypt(encrypted);

        assertEquals(plaintext, decrypted,
                "Decrypted numeric string should match original");
    }
}
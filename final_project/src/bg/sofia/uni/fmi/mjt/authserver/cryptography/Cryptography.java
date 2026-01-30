package bg.sofia.uni.fmi.mjt.authserver.cryptography;

public interface Cryptography {

    String encrypt(String data);

    String decrypt(String data);
}

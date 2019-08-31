package com.viloveul.packuman.security.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class CryptoManager {

    private static final Logger log = LoggerFactory.getLogger(CryptoManager.class);

    private static final String CIPHER = "RSA/ECB/PKCS1Padding";

    private RSAPrivateKey privateKey;

    private RSAPublicKey publicKey;

    @Autowired
    public void setup(Environment environment) throws Exception {
        System.out.println("Crypto setup");

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            // load private key file
            File privateKeyFile = new File(environment.getRequiredProperty("viloveul.packuman.crypto.privatekey"));
            if (!privateKeyFile.exists()) {
                log.warn("Private key not exists or not readable");
                System.out.println("Private key not exists or not readable");
            }
            FileInputStream privateKeyFileInput = new FileInputStream(privateKeyFile);
            DataInputStream privateKeyDataInput = new DataInputStream(privateKeyFileInput);

            byte[] privateKeyBytes = new byte[(int) privateKeyFile.length()];
            privateKeyDataInput.readFully(privateKeyBytes);
            privateKeyDataInput.close();

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);

            // load public key file
            File publicKeyFile = new File(environment.getRequiredProperty("viloveul.packuman.crypto.publickey"));
            if (!publicKeyFile.exists()) {
                log.warn("Public key not exists or not readable");
                System.out.println("Public key not exists or not readable");
            }
            FileInputStream publicKeyFileInput = new FileInputStream(publicKeyFile);
            DataInputStream publicKeyDataInput = new DataInputStream(publicKeyFileInput);
            byte[] publicKeyBytes = new byte[(int) publicKeyFile.length()];
            publicKeyDataInput.readFully(publicKeyBytes);
            publicKeyDataInput.close();

            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            this.publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);

        } catch (Exception e) {
            log.warn(e.getMessage());
            System.out.println(e.getMessage());
            throw new Exception("Public/Private key not exists or not readable");
        }
    }

    public String encrypt(String message) {
        try {
            Cipher c = Cipher.getInstance(CIPHER);
            c.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] cipherMessage = c.doFinal(message.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(cipherMessage);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            log.error(e.getMessage());
            System.out.println("Crypto encrypt");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String decrypt(String encryptedMessage) {

        try {
            byte[] encryptedMessageBytes = Base64.getDecoder().decode(encryptedMessage);

            Cipher c = Cipher.getInstance(CIPHER);
            c.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] plainMessage = c.doFinal(encryptedMessageBytes);

            return new String(plainMessage, StandardCharsets.UTF_8);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | IllegalArgumentException e) {
            log.error(e.getMessage());
            System.out.println("Crypto decrypt");
            System.out.println(e.getMessage());
            return null;
        }
    }
}



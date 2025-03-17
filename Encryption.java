package main;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

	public static byte[] generateSalt() {
        byte[] salt = new byte[16]; // 16 bytes = 128-bit salt
        new SecureRandom().nextBytes(salt); // SecureRandom for cryptographic security
        return salt;
    }

	// Generates a 256-bit AES key from a user entered password
	public static SecretKey getKeyFromPassword(String strToEncrypt, byte[] salt) {
        SecretKey secretKey = null;
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(strToEncrypt.toCharArray(), salt, 65536, 256);
			secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		}

		catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
       return secretKey;
    }

	/**
	 * @param password
	 * @param secretKey
	 * @return
	 */
	public static String encryption(String str, SecretKey secretKey) {
		StringBuilder encryptedStr = new StringBuilder();
		byte[] encryptedWithIv = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			// Generate a random 16-byte Initialization Vector
	        byte[] vector = new byte[16];
	        new SecureRandom().nextBytes(vector);
	        IvParameterSpec vectorSpec = new IvParameterSpec(vector);

	        // Initialize AES cipher in ENCRYPT mode
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey, vectorSpec);

	        // Encrypted string
	        byte[] encrypted = cipher.doFinal(str.getBytes("UTF-8"));

	        // Combine Initialization Vector + Encrypted data and encode it to Base64
	        encryptedWithIv = new byte[vector.length + encrypted.length];
	        System.arraycopy(vector, 0, encryptedWithIv, 0, vector.length);
	        System.arraycopy(encrypted, 0, encryptedWithIv, vector.length, encrypted.length);

	        encryptedStr.append(Base64.getEncoder().encodeToString(encryptedWithIv));
		}

		catch (Exception e) {
			e.printStackTrace();
		}

        return encryptedStr.toString();
	}

	public static void main(String[] args) {

		String text = "qwerty123";
		System.out.println("Original text:   " + text);
		byte[] salt = Encryption.generateSalt();
		SecretKey secretKey = Encryption.getKeyFromPassword(text, salt);
		String encryptedText = Encryption.encryption(text,secretKey);
		System.out.println("Encrypted text:  " + encryptedText);

		String decryptedText = Decryption.decryption(encryptedText,secretKey,salt);
		System.out.println("Decrypted text:  " + decryptedText);
	}
}

package main;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Decryption {

	public static String decryption(String strToDecrypt, SecretKey secretKey, byte[] salt) {
		try {
			// Decode Base64 Encrypted Data
			byte[] encryptedData = Base64.getDecoder().decode(strToDecrypt);

			// Extract Initialization Vector (first 16 bytes)
	        byte[] vector = Arrays.copyOfRange(encryptedData, 0, 16);
	        IvParameterSpec ivspec = new IvParameterSpec(vector);

	        // Extract actual encrypted data (skipping IV)
            byte[] cipherText = Arrays.copyOfRange(encryptedData, 16, encryptedData.length);

            // Initialize Cipher for Decryption
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);

            // Decrypt and return the plaintext password
            byte[] decryptedText = cipher.doFinal(cipherText);
            return new String(decryptedText, StandardCharsets.UTF_8);
		}

		catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	    return null;
	  }
}

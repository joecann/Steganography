package main;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Converter {
	
	public static String textToBinary(String text) {
		char [] charArray = text.toCharArray();
		StringBuilder binaryString = new StringBuilder();
		// Convert the string to binary using the Integer primitive type
		for(char c: charArray) {
			//replace add a 0 at the start of the binary sequence
			String binary = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
			binaryString.append(binary);
		}
		return binaryString.toString();
	}

	public static String bytesToBinary(byte[] bytes) {
	    StringBuilder binary = new StringBuilder();
	    for (byte b : bytes) {
	        binary.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
	    }
	    return binary.toString();
	}
	
	public static String binaryToText(String binary) {
		StringBuilder convertedBinary = new StringBuilder();

		for(int i = 0; i < binary.length(); i+=8) {

			if (i + 8 > binary.length()) break; // Stop if a complete byte cannot be extracted.

			int bitIndex = i+8;
			String eightBits = binary.substring(i,bitIndex); // extract 8 bits
			int asciiValue = Integer.parseInt(eightBits, 2); // Convert binary to integer
			char character = (char) asciiValue; // Convert integer to character

			convertedBinary.append(character);
		}
		return convertedBinary.toString();
	}
		
	public static byte[] binaryToBytes(String binary) {
	    int byteCount = binary.length() / 8;
	    byte[] bytes = new byte[byteCount];

	    for (int i = 0; i < byteCount; i++) {
	        bytes[i] = (byte) Integer.parseInt(binary.substring(i * 8, (i + 1) * 8), 2);
	    }

	    return bytes;
	}
	
	public static long generateSeed(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
			 // Convert first 8 bytes of hash into a long value
	        return Arrays.hashCode(Arrays.copyOfRange(hash, 0, 8));
		}

		catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
    }

}

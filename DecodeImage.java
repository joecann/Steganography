package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Base64;
import java.util.Random;
import javax.crypto.SecretKey;
import javax.imageio.ImageIO;

public class DecodeImage {
	
	String imageURL;
	public DecodeImage(String imageURL) {
		this.imageURL = imageURL;
	}

	/**
	 *
	 * @param imageURL
	 * @return str
	 * @throws Exception
	 */
	public String basicDecoding() throws Exception {
		BufferedImage image = ImageIO.read(new File(imageURL));
		StringBuilder binary = new StringBuilder();

		for(int x = 0; x < image.getWidth(); x++) {
			for(int y = 0; y < image.getHeight(); y++) {
				int pixel = image.getRGB(x, y);

				// Extract the LSB from each color channel
				int red = (pixel >> 16) & 0xFF;
				int green = (pixel >> 8) & 0xFF;
				int blue = pixel & 0xFF;

				// Append the least significant bit of each color channel
				binary.append(red & 0x01);
				binary.append(green & 0x01);
				binary.append(blue & 0x01);

				String str = Converter.binaryToText(binary.toString());

				if(str.endsWith("END"))
		            return str.substring(0, str.length() - 3);
			}
		}
		return null;
	}
	
	/**
	 * TODO WIll need to extract salt we can decode the encrypted string
	 * @param imageURL
	 * @return encrypted str
	 * @throws Exception
	 */
	public String advancedDecoding(String password) throws Exception {
		BufferedImage image = ImageIO.read(new File(imageURL));
		StringBuilder binary = new StringBuilder();
		byte [] salt = null;
				
		long SECRET_SEED = Converter.generateSeed(password);
		Random random = new Random(SECRET_SEED);
		
		String convertedSalt = "";
		// Retrieve and decrypt salt
		while(! convertedSalt.contains("KEY")) {
			int x = random.nextInt(image.getWidth());
			int y = random.nextInt(image.getHeight());								
			int pixel = image.getRGB(x, y);				
						
			// Extract the LSB from each color channel
			int red = (pixel >> 16) & 0xFF;
			int green = (pixel >> 8) & 0xFF;
			int blue = pixel & 0xFF;
	
			// Append the least significant bit of each color channel
			binary.append(red & 0x01);
			binary.append(green & 0x01);
			binary.append(blue & 0x01);
			
			convertedSalt = Converter.binaryToText(binary.toString());
		}	
		
		if(convertedSalt.startsWith("PIE") && convertedSalt.endsWith("KEY")) {
			// Retrieve and convert salt string to char		
			int start = convertedSalt.indexOf("PIE")+ 3; // Move past marker
			int end = convertedSalt.indexOf("KEY");
			if (end == -1 || start >= end) throw new RuntimeException("Invalid salt format");
			String extractedSalt = convertedSalt.substring(start,end);
			salt = Base64.getDecoder().decode(extractedSalt);
		}
				
		// Reset the binary String 
		binary.delete(0, binary.length());
			
		String encryptedStr = "";			
		while(! encryptedStr.contains("END")) {
			int x = random.nextInt(image.getWidth());
			int y = random.nextInt(image.getHeight());
			int pixel = image.getRGB(x, y);

			// Extract the LSB from each color channel
			int red = (pixel >> 16) & 0xFF;
			int green = (pixel >> 8) & 0xFF;
			int blue = pixel & 0xFF;
			
			// Append the least significant bit of each color channel
			binary.append(red & 0x01);
			binary.append(green & 0x01);
			binary.append(blue & 0x01);
			
			encryptedStr = Converter.binaryToText(binary.toString());			
		}
		
		if(encryptedStr.startsWith("DAS") && encryptedStr.endsWith("END")) {
			int start = encryptedStr.indexOf("DAS")+ 3; // Move past marker
			int end = encryptedStr.indexOf("END");
			if (end == -1 || start >= end) throw new RuntimeException("Invalid text format");
			encryptedStr = encryptedStr.substring(start,end);
		}	
		
		SecretKey secretKey = Encryption.getKeyFromPassword(password, salt);
		return Decryption.decryption(encryptedStr,secretKey,salt);
	}

	public static void main(String[] args) {
		try {
			String text = "qwerty123";
			String imageURL = "C:\\Users\\JoeCa\\OneDrive\\Pictures\\image.jpg";
			String fileURL = "C:\\Users\\JoeCa\\OneDrive\\Desktop\\encryptedEncoding.png";
			EncodeImage encode = new EncodeImage(imageURL);
			encode.advancedEncoding(text,fileURL,"zebra69");			
			
			DecodeImage decode = new DecodeImage("C:\\Users\\JoeCa\\OneDrive\\Desktop\\encryptedEncoding.png");
			System.out.println(decode.advancedDecoding("zebra69"));
		}
		catch (Exception e) {e.printStackTrace();}
	}

}

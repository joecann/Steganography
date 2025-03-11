package main;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Random;

public class DecodeImage {
	
	public String decode(String imageURL) throws Exception {
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
				
				String text = convertBinaryToText(binary.toString());
				
				if(text.endsWith("END"))
		            return text.substring(0, text.length() - 3);
			}
		}			
		return null;
	}
	
	public static String convertBinaryToText(String binary) {	
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
	
	public static void main(String[] args) {
		try {
			DecodeImage image = new DecodeImage();
			String output = image.decode("C:\\Users\\JoeCa\\OneDrive\\Desktop\\hidden2.png");
			System.out.println(output);
			
			String key = "password";
			Random ran = new Random(key.hashCode());
			
			System.out.println(ran);			
		} 
		catch (Exception e) {e.printStackTrace();}
	}

}

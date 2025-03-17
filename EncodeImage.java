package main;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import javax.crypto.SecretKey;
import javax.imageio.ImageIO;

public class EncodeImage {
	
	String imageURL;
	public EncodeImage(String imageURL) {
		this.imageURL = imageURL;
	}

	/**
	 * @param imageURL
	 * @param text
	 * @param fileURL
	 * @return encryptedEmbeddedImage
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public BufferedImage advancedEncoding(String text, String fileURL, String seedPassword) throws IOException, InterruptedException {
		BufferedImage image = ImageIO.read(new File(imageURL));
		Set<Point> usedPixels = new HashSet<>();
		
		// Create the salt
		byte[] salt = Encryption.generateSalt();
		String saltBinary = Converter.textToBinary("PIE") 
				+ Converter.bytesToBinary(Base64.getEncoder().encode(salt)) + Converter.textToBinary("KEY");
						
		// Encrypting the string data and convert to binary
		SecretKey secretKey = Encryption.getKeyFromPassword(seedPassword,salt);
		String encryptedText = "DAS" + Encryption.encryption(text,secretKey) + "END";
		String binaryText = Converter.textToBinary(encryptedText);	
		
		if(!enoughPixelsForStorage(image,binaryText.length() + saltBinary.length())) return null;
		
		 // Random placement of string using a secred seed
		long SECRET_SEED = Converter.generateSeed(seedPassword);
        Random random = new Random(SECRET_SEED);
        int x = 0, y = 0;
               
        // 1. Assign the salt 
	    int saltIndex = 0;	
	    while(saltIndex < saltBinary.length()) {
	    	x = random.nextInt(image.getWidth());
    	    y = random.nextInt(image.getHeight());
	    	
    	    if(! usedPixels.contains(new Point(x, y))) {
	    		        	    
        	    usedPixels.add(new Point(x, y));  // Track used pixels
            	
      			// A pixel in Java is represented as a 32-bit integer
    			int pixel = image.getRGB(x, y);
    				
    			// Extract each color and apply bitwise with 0xFF:
    			int red = (pixel >> 16) & 0xFF; // Red(First 8 bits and shift 16 bits)
    			int green = (pixel >> 8) & 0xFF; // Green(Next 8 bits and shift 8 bits)
    			int blue = pixel & 0xFF; // Blue(Last 8 bits and no shifting)
    	
    			// Modify each color channel's LSB
    			char currentBit = saltBinary.charAt(saltIndex);
    			red = setLeastSignificantBit(red,currentBit);
    			saltIndex++;   			
    	
    			if(saltIndex < saltBinary.length()) {
    				currentBit = saltBinary.charAt(saltIndex);
    				green = setLeastSignificantBit(green,currentBit);
    				saltIndex++;
    			}
    	
    			if(saltIndex < saltBinary.length()) {
    				currentBit = saltBinary.charAt(saltIndex);
    				blue = setLeastSignificantBit(blue,currentBit);
    				saltIndex++;
    			}
    	
    			// Reconstruct the pixel with modified color channels
    	        int newPixel = (red << 16) | (green << 8) | blue;
    	        image.setRGB(x, y, newPixel); // Set the modified pixel in the image
	    	} 	
	    }
        
    	// Encoding the string message
    	int StrIndex = 0;    	
    	while(StrIndex < binaryText.length()) {
    		x = random.nextInt(image.getWidth());
        	y = random.nextInt(image.getHeight());	
        	
        	if(!usedPixels.contains(new Point(x, y))) {
            	
            	usedPixels.add(new Point(x, y));  // Track used pixels
            	    		
    			int pixel = image.getRGB(x, y);
    			
    			// Extract each color and apply bitwise & 0xFF:
    			int red = (pixel >> 16) & 0xFF; 
    			int green = (pixel >> 8) & 0xFF;
    			int blue = pixel & 0xFF;

    			// Modify each color channel's LSB
    			char currentBit = binaryText.charAt(StrIndex);
    			red = setLeastSignificantBit(red,currentBit);
    			StrIndex++;

    			if(StrIndex < binaryText.length()) {
    				currentBit = binaryText.charAt(StrIndex);
    				green = setLeastSignificantBit(green,currentBit);
    				StrIndex++;
    			}

    			if(StrIndex < binaryText.length()) {
    				currentBit = binaryText.charAt(StrIndex);
    				blue = setLeastSignificantBit(blue,currentBit);
    				StrIndex++;
    			}

    			// Reconstruct the pixel with modified color channels
    	        int newPixel = (red << 16) | (green << 8) | blue;
    	        image.setRGB(x, y, newPixel); // Set the modified pixel in the image        		
        	}
        }  		
		saveImage(image,fileURL);
		return image;
	}
	
	/**
	 *
	 * @param imageURL
	 * @param text
	 * @param fileURL
	 * @return embedded Image
	 * @throws IOException
	 */
	public BufferedImage basicEncoding(String text, String fileURL) throws IOException {
		
		BufferedImage image = ImageIO.read(new File(imageURL));
		String str = text + "END";
		String binaryText = Converter.textToBinary(str);
		int binaryStrLength = binaryText.length();
		int pixelIndex = 0;

		if(!enoughPixelsForStorage(image,binaryStrLength)) return null;
		
		for(int x = 0; x < image.getWidth(); x++) {
			for(int y = 0; y < image.getHeight(); y++) {

				if(pixelIndex >= binaryStrLength) break;

				// A pixel in Java is represented as a 32-bit integer
				int pixel = image.getRGB(x, y);

				// Extract each color and apply bitwise with 0xFF:
				int red = (pixel >> 16) & 0xFF; // Red(First 8 bits and shift 16 bits)
				int green = (pixel >> 8) & 0xFF; // Green(Next 8 bits and shift 8 bits)
				int blue = pixel & 0xFF; // Blue(Last 8 bits and no shifting)

				// Modify each color channel's LSB
				char currentBit = binaryText.charAt(pixelIndex);
				red = setLeastSignificantBit(red,currentBit);
				pixelIndex++;

				if(pixelIndex < binaryStrLength) {
					currentBit = binaryText.charAt(pixelIndex);
					green = setLeastSignificantBit(green,currentBit);
					pixelIndex++;
				}

				if(pixelIndex < binaryStrLength) {
					currentBit = binaryText.charAt(pixelIndex);
					blue = setLeastSignificantBit(blue,currentBit);
					pixelIndex++;
				}

				// Reconstruct the pixel with modified color channels
		        int newPixel = (red << 16) | (green << 8) | blue;
		        image.setRGB(x, y, newPixel); // Set the modified pixel in the image
	     	}
		}
		saveImage(image,fileURL);
		return image;
	}
	
	private void saveImage(BufferedImage image, String outputPath) throws IOException {
		File outputFile = new File(outputPath);
	    ImageIO.write(image, "PNG", outputFile); // saves as a png. Can be any file type jpg etc
	}

	private boolean enoughPixelsForStorage(BufferedImage image, int length) {
		// Check if the image has enough pixels to store the binary data
        int totalPixels = image.getWidth() * image.getHeight();
        if (totalPixels * 3 < length) {
            System.out.println("Not enough pixels in the image to store the data!");
            return false;
        }
        return true;
	}
	
	private int setLeastSignificantBit(int colorChannel, char bit) {
		// Clear the LSB using bitwise AND with 0xFE
	    colorChannel = colorChannel & 0xFE;
	    // Set the LSB based on the bit value ('0' or '1')
	    if (bit == '1') colorChannel = colorChannel | 0x01;

	    return colorChannel;
	}

}

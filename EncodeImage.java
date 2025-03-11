package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EncodeImage {
	
	public BufferedImage encode(String imageURL, String text, String fileURL) throws IOException {
		BufferedImage image = ImageIO.read(new File(imageURL));
		
		String str = text + "END";
		String binaryText = convertTextToBinary(str);
		int binaryStrLength = binaryText.length();
		int pixelIndex = 0;
				
		// Check if the image has enough pixels to store the binary data
        int totalPixels = image.getWidth() * image.getHeight();
        if (totalPixels * 3 < binaryStrLength) {
            System.out.println("Not enough pixels in the image to store the data!");
            return null;
        }
		
		for(int x = 0; x < image.getWidth(); x++) {
			for(int y = 0; y < image.getHeight(); y++) {
				
				if(pixelIndex >= binaryStrLength) break;
				
				// A pixel in Java is represented as a 32-bit integer in the format
				int pixel = image.getRGB(x, y);
				
				// Extract each color and apply bitwise with 0xFF: 
				// Red(First 8 bits and shift 16 bits),
				// Green(Next 8 bits and shift 8 bits)
				// Blue(Last 8 bits and no shifting)
								
				int red = (pixel >> 16) & 0xFF;
				int green = (pixel >> 8) & 0xFF;
				int blue = pixel & 0xFF;
								
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
		        // Set the modified pixel in the image
		        image.setRGB(x, y, newPixel);
			}
		}
		saveImage(image,fileURL);
		return image;
	}
		
	private void saveImage(BufferedImage image, String outputPath) throws IOException {
		File outputFile = new File(outputPath);
	    ImageIO.write(image, "PNG", outputFile); // saves as a png. Can be any file type jpg etc
	}
	
	public static String convertTextToBinary(String text) {
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
	
	private int setLeastSignificantBit(int colorChannel, char bit) {
		// Clear the LSB using bitwise AND with 0xFE
	    colorChannel = colorChannel & 0xFE;
	    // Set the LSB based on the bit value ('0' or '1')
	    if (bit == '1') colorChannel = colorChannel | 0x01;
	    
	    return colorChannel;
	}
	
	public static void main(String[] args) {
		EncodeImage image = new EncodeImage();
		try {
			String text = "password";
			String imageURL = "C:\\Users\\JoeCa\\OneDrive\\Pictures\\image.jpg";
			String fileURL = "C:\\Users\\JoeCa\\OneDrive\\Desktop\\hidden2.png";
			image.encode(imageURL,text,fileURL);
		} 
		
		catch (IOException e) { e.printStackTrace(); }
		
		
		
	}

}

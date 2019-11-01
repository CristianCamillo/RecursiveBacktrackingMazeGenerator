package p1;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Bitmap
{
	protected final int width;
	protected final int height;
	protected final byte[] comps; // the bitmap is divided into pixels, every pixel has 4 components: alpha, blue, green and red
	
	public Bitmap(int width, int height)
	{
		this.width = width;
		this.height = height;		
		comps = new byte[width * height * 4];
	}
	
	public Bitmap(String fileName) throws IOException
	{
		byte[] components = null;

		BufferedImage image = ImageIO.read(new File(fileName));

		width = image.getWidth();
		height = image.getHeight();

		int imgPixels[] = new int[width * height];
		image.getRGB(0, 0, width, height, imgPixels, 0, width);
		components = new byte[width * height * 4];

		for(int i = 0; i < width * height; i++)
		{
			int pixel = imgPixels[i];
	
			components[i * 4]     = (byte)(pixel >> 24); // A
			components[i * 4 + 1] = (byte)(pixel      ); // B
			components[i * 4 + 2] = (byte)(pixel >> 8 ); // G
			components[i * 4 + 3] = (byte)(pixel >> 16); // R
		}

		this.comps = components;
	}
	
	public int getWidth() 				{ return width; }	
	public int getHeight() 				{ return height; }
	public byte getComponent(int index) { return comps[index]; }
	
	public void clear(byte shade)
	{
		Arrays.fill(comps, shade);
	}
	
	protected void drawPixel(int x, int y, byte a, byte r, byte g, byte b) // TODO: actually implement alpha (trasparency)
	{		
		int index = (x + y * width) * 4;	
			
	//	float opacity = (a&0xff) / 255f;
		
		comps[index + 1] = b;//(byte)((comps[index + 1]&0xff) + (b&0xff - comps[index + 1]&0xff) * opacity);
		comps[index + 2] = g;//(byte)((comps[index + 1]&0xff) + (b&0xff - comps[index + 1]&0xff) * opacity);
		comps[index + 3] = r;//(byte)((comps[index + 1]&0xff) + (b&0xff - comps[index + 1]&0xff) * opacity);
		
		comps[index] = a;
	}
	
	public void copyToByteArray(byte[] dest)
	{
		int end = width * height;
		
		for(int i = 0; i < end; i++)
		{
			int index1 = i * 3;
			int index2 = i * 4;
			
			dest[index1] 	 = comps[index2 + 1];
			dest[index1 + 1] = comps[index2 + 2];
			dest[index1 + 2] = comps[index2 + 3];
			
		}
	}
}
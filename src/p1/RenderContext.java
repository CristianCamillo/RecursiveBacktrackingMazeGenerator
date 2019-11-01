package p1;

public class RenderContext extends Bitmap
{	
	private byte a = (byte)0xff;
	private byte r = (byte)0xff;
	private byte g = (byte)0xff;
	private byte b = (byte)0xff;
	
	private byte bgR = (byte)0x00;
	private byte bgG = (byte)0x00;
	private byte bgB = (byte)0x00;
	
	private int fontSize = 20;
	
	public RenderContext(int width, int height)
	{
		super(width, height);
	}
	
	/*********************************************************************/
	/* Getters                                                           */
	/*********************************************************************/
	
	public byte[] getColor()			{ return new byte[] {a, r, g, b}; }
	public byte[] getBackgroundColor()  { return new byte[] {bgR, bgG, bgB}; }
	public int getFontSize()			{ return fontSize; }
	
	/*********************************************************************/
	/* Setters                                                           */
	/*********************************************************************/
	
	public void setFontSize(int fontSize) { this.fontSize = fontSize; }
	
	public void setColor(byte a, byte r, byte g, byte b)
	{
		this.a = a;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public void setBackgroundColor(byte r, byte g, byte b)
	{
		bgR = r;
		bgG = g;
		bgB = b;
	}
		
	/*********************************************************************/
	/* Draw routings                                                     */
	/*********************************************************************/
	
	public void fillBackground()
	{		
		int end = width * height;

		for(int i = 0; i < end; i++)
		{
			int index = i * 4;
			
			comps[index] = (byte)0xff;
			comps[index + 3] = bgR;
			comps[index + 2] = bgG;
			comps[index + 1] = bgB;
		}
	}
	
	public void copyPixel(Bitmap src, int srcX, int srcY, int destX, int destY, float lightAmt)
	{
		int srcIndex = (srcX + srcY * src.getWidth()) * 4;
		int destIndex = (destX + destY * width) * 4;
			
		if(srcIndex >= src.getWidth() * src.getHeight() * 4)
			return;

		comps[destIndex] 	 = (byte)((src.getComponent(srcIndex) 	  & 0xff) * lightAmt);
		comps[destIndex + 1] = (byte)((src.getComponent(srcIndex + 1) & 0xff) * lightAmt);
		comps[destIndex + 2] = (byte)((src.getComponent(srcIndex + 2) & 0xff) * lightAmt);
		comps[destIndex + 3] = (byte)((src.getComponent(srcIndex + 3) & 0xff) * lightAmt);
	}
	
	public final void fillRectangle(int xStart, int yStart, int width, int height)
	{
		if(width < 0)
		{
			xStart = xStart + width;
			width = - width;
		}
		
		if(width < 0)
		{
			yStart = yStart + height;
			height = - height;
		}
		
		for(int y = yStart; y < yStart + height; y++)
			for(int x = xStart; x < xStart + width; x++)
				drawPixel(x, y, a, r, g, b);
	}
	
	public final void fillNumber(int xStart, int yStart, float number) // TODO: add number after comma
	{			
		if(number == 0)
		{
			fillDigit(xStart, yStart, 0);
			return;
		}
		
		int offset = 0;
		
		if(number < 0)
		{
			number = - number;
			fillMinus(xStart, yStart);
			offset = fontSize + fontSize / 5;
		}
		
		int iNumber = (int)number;

		int nIDigit = 0;
		
		if(iNumber == 0)
			nIDigit++;
		else
			while(iNumber > 0)
			{
				iNumber /= 10;
				nIDigit++;
			}
		
		double print = number / Math.pow(10, nIDigit - 1);
		
		while(number != (int)number && nIDigit > -2)
		{
			fillDigit(xStart + offset, yStart, ((int)print) % 10);
			offset += fontSize + fontSize / 5;
			print *= 10;
			number *= 10;
			nIDigit--;
			
			if(nIDigit == 0)
			{	
				fillComma(xStart + offset, yStart);
				offset += fontSize + fontSize / 5;				
			}
		}
	}
	
	public final void fillDigit(int xStart, int yStart, int i)
	{
		if(i < 0 || i > 9)
			return;
		
		switch(i)
		{
			case 0: fill0(xStart, yStart); return;
			case 1: fill1(xStart, yStart); return;
			case 2: fill2(xStart, yStart); return;
			case 3: fill3(xStart, yStart); return;
			case 4: fill4(xStart, yStart); return;
			case 5: fill5(xStart, yStart); return;
			case 6: fill6(xStart, yStart); return;
			case 7: fill7(xStart, yStart); return;
			case 8: fill8(xStart, yStart); return;
			case 9: fill9(xStart, yStart);
		}
	}
	
	public final void fill0(int xStart, int yStart)
	{
		fillRectangle(xStart, 					 yStart, 					fontSize / 5, 	  fontSize);
		fillRectangle(xStart + fontSize / 5 * 4, yStart, 					fontSize / 5, 	  fontSize);
		
		fillRectangle(xStart + fontSize / 5, 	 yStart, 					fontSize / 5 * 3, fontSize / 5);
		fillRectangle(xStart + fontSize / 5, 	 yStart + fontSize / 5 * 4, fontSize / 5 * 3, fontSize / 5);
	}
	
	public final void fill1(int xStart, int yStart)
	{
		fillRectangle(xStart + fontSize / 5 * 2, yStart, 					fontSize / 5, 	  fontSize);
	}
	
	public final void fill2(int xStart, int yStart)
	{
		fillRectangle(xStart, 					 yStart, 					fontSize, 		  fontSize / 5);
		fillRectangle(xStart, 					 yStart + fontSize / 5 * 2, fontSize, 		  fontSize / 5);
		fillRectangle(xStart, 					 yStart + fontSize / 5 * 4, fontSize, 		  fontSize / 5);
		
		fillRectangle(xStart + fontSize / 5 * 4, yStart + fontSize / 5, 	fontSize / 5, 	  fontSize / 5);
		fillRectangle(xStart, 					 yStart + fontSize / 5 * 3, fontSize / 5, 	  fontSize / 5);
	}
	
	public final void fill3(int xStart, int yStart)
	{
		fillRectangle(xStart, 					 yStart, 					fontSize / 5 * 4, fontSize / 5);
		fillRectangle(xStart, 					 yStart + fontSize / 5 * 2, fontSize / 5 * 4, fontSize / 5);
		fillRectangle(xStart, 					 yStart + fontSize / 5 * 4, fontSize / 5 * 4, fontSize / 5);	
		
		fillRectangle(xStart + fontSize / 5 * 4, yStart, 					fontSize / 5, 	  fontSize);
	}
	
	public final void fill4(int xStart, int yStart)
	{
		fillRectangle(xStart, 					 yStart, 					fontSize / 5, 	  fontSize / 5 * 3);
		fillRectangle(xStart + fontSize / 5, 	 yStart + fontSize / 5 * 2, fontSize / 5 * 3, fontSize / 5);
		fillRectangle(xStart + fontSize / 5 * 4, yStart, 					fontSize / 5, 	  fontSize);
	}
	
	public final void fill5(int xStart, int yStart)
	{
		fillRectangle(xStart, 					 yStart, 					fontSize, 		  fontSize / 5);
		fillRectangle(xStart, 					 yStart + fontSize / 5 * 2, fontSize, 		  fontSize / 5);
		fillRectangle(xStart, 					 yStart + fontSize / 5 * 4, fontSize, 		  fontSize / 5);
		
		fillRectangle(xStart + fontSize / 5 * 4, yStart + fontSize / 5 * 3, fontSize / 5, 	  fontSize / 5);
		fillRectangle(xStart, 					 yStart + fontSize / 5, 	fontSize / 5, 	  fontSize / 5);
	}
	
	public final void fill6(int xStart, int yStart)
	{
		fillRectangle(xStart, 					 yStart, 					fontSize, 		  fontSize / 5);
		fillRectangle(xStart, 					 yStart + fontSize / 5 * 2, fontSize, 		  fontSize / 5);
		fillRectangle(xStart, 					 yStart + fontSize / 5 * 4, fontSize, 		  fontSize / 5);
		
		fillRectangle(xStart, 					 yStart + fontSize / 5, 	fontSize / 5,	  fontSize / 5);
		fillRectangle(xStart, 					 yStart + fontSize / 5 * 3, fontSize / 5, 	  fontSize / 5);
		fillRectangle(xStart + fontSize / 5 * 4, yStart + fontSize / 5 * 3, fontSize / 5, 	  fontSize / 5);
	}
	
	public final void fill7(int xStart, int yStart)
	{
		fillRectangle(xStart, 					 yStart, 					fontSize / 5 * 4, fontSize / 5);
		fillRectangle(xStart + fontSize / 5 * 4, yStart, 					fontSize / 5, 	  fontSize);
	}
	
	public final void fill8(int xStart, int yStart)
	{
		fill0(xStart, 							 yStart);
		
		fillRectangle(xStart + fontSize / 5, 	 yStart + fontSize / 5 * 2, fontSize / 5 * 3, fontSize / 5);
	}
	
	public final void fill9(int xStart, int yStart)
	{
		fillRectangle(xStart, 					 yStart, 					fontSize, 		  fontSize / 5);
		fillRectangle(xStart, 					 yStart + fontSize / 5 * 2, fontSize, 		  fontSize / 5);
		fillRectangle(xStart, 					 yStart + fontSize / 5 * 4, fontSize, 		  fontSize / 5);
		
		fillRectangle(xStart, 					 yStart + fontSize / 5, 	fontSize / 5, 	  fontSize / 5);
		fillRectangle(xStart + fontSize / 5 * 4, yStart + fontSize / 5, 	fontSize / 5, 	  fontSize / 5);
		fillRectangle(xStart + fontSize / 5 * 4, yStart + fontSize / 5 * 3, fontSize / 5, 	  fontSize / 5);
	}
	
	public final void fillComma(int xStart, int yStart)
	{
		fillRectangle(xStart + fontSize / 5 * 2, yStart + fontSize / 5 * 4, fontSize / 5, 	  fontSize / 5);
	}
	
	public final void fillMinus(int xStart, int yStart)
	{
		fillRectangle(xStart, 					 yStart + fontSize / 5 * 2,  fontSize, 		  fontSize / 5);
	}
}
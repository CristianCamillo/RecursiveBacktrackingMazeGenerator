package p1;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JFrame;

public class SimpleEngineBuffered implements KeyListener, MouseListener
{
	// Cristian Camillo, 2019-10-28
	// Copy-paste this class together with "Bitmap" and "RenderContext"
	
	/*********************************************************************/
	/* Settings                                                          */
	/*********************************************************************/
	
	private int width;
	private int height;
	private int pixelSize;
	private String title;
	private int FPSCap;
	private boolean showFPS;
	private boolean fullscreen;
	
	/*********************************************************************/
	/* Graphics                                                          */
	/*********************************************************************/
	
	private JFrame frame;	
	private Canvas canvas;
	
	private BufferStrategy bs;
	private Graphics gr;
	
	private RenderContext screen;
	private BufferedImage displayImage;
	private byte[]		  displayComponents;
	
	/*********************************************************************/
	/* Game Loop variables                                               */
	/*********************************************************************/
	
	private boolean running = true;
	
	private long frameDuration = 0;
	private int syncDelay = 1000000;
	
	private long elapsedTime = 1000000000;
	
	private float FPS = 0;
	private long FPSUpdate = 0;
	
	/*********************************************************************/
	/* Keyboard and Mouse Inputs                                         */
	/*********************************************************************/
	
	private boolean[] key = new boolean[1024];
	private boolean[] mouse = new boolean[MouseInfo.getNumberOfButtons()];
	
	private boolean[] keyToggle = new boolean[key.length];
	private boolean[] mouseToggle = new boolean[mouse.length];
	
	private KeyEvent lastKeyPressed = null;
	private MouseEvent lastMouseInput = null;
	
	/*********************************************************************/
	/* Constants                                                         */
	/*********************************************************************/
	
	private final static int SECOND = 1000000000;
	private final static int FPS_UPDATE_PERIOD = 500000000;
	
	/*********************************************************************/
	/* Constructor                                                       */
	/*********************************************************************/
	
	public SimpleEngineBuffered(int width, int height, int pixelSize, String title, int FPSCap, boolean showFPS, boolean fullscreen)
	{		
		this.width = width;
		this.height = height;
		this.pixelSize = pixelSize;
		setTitle(title);
		setFPSCap(FPSCap);
		setShowFPS(showFPS);
		this.fullscreen = fullscreen;

		setSize(width, height, fullscreen);
	}
	
	public void start()
	{
		while(running)
		{
			long start = System.nanoTime();
			
			update();
			render();						
			
			if(this.showFPS)
			{
				int fontSize = screen.getFontSize();
				
				screen.setColor((byte)0xff, (byte)0x00, (byte)0x00, (byte)0x00);
				screen.fillRectangle(fontSize / 10 * 9, fontSize / 10 * 9, (fontSize + fontSize / 5) * 5, fontSize + fontSize / 5);
				
				screen.setColor((byte)0xff, (byte)0x00, (byte)0xff, (byte)0x00);
				screen.fillNumber(fontSize, fontSize, getFPS());
			}			
			
			swapBuffers();
							
			if(this.FPSCap > 0)
			{
				long timeLeft = frameDuration - (System.nanoTime() - start) - syncDelay;
				
				if(timeLeft > 0)
					try{
						Thread.sleep(timeLeft / 1000000, (int)(timeLeft % 1000000));
					}catch(InterruptedException e) { System.err.println(e); System.exit(1); }
				
				while(frameDuration > System.nanoTime() - start);
			}
			
			elapsedTime = System.nanoTime() - start;		
		}
	
		frame.dispose();
	}
	
	/*********************************************************************/
	/* Misc                                                              */
	/*********************************************************************/
	
	public void update(){}
	public void render(){}
	
	public void swapBuffers()
	{	
		screen.copyToByteArray(displayComponents);
		gr.drawImage(displayImage, 0, 0, width, height, 0, 0, screen.getWidth(), screen.getHeight(), null);
		bs.show();
	}
	
	public final void stop()
	{
		running = false;
	}
	
	private boolean toggle(int code, boolean[] baseArray, boolean[] toggleArray)
	{
		if(baseArray[code])
		{
			if(!toggleArray[code])
			{
				synchronized(SimpleEngineBuffered.class)
				{
					toggleArray[code] = true;
				}
				
				return true;
			}
		}
		else
			synchronized(SimpleEngineBuffered.class)
			{
				toggleArray[code] = false;
			}
			
		
		return false;
	}
	
	/*********************************************************************/
	/* Getters                                                           */
	/*********************************************************************/
	
	public final int getWidth() 		 		{ return width; }	
	public final int getHeight() 	 	 		{ return height; }
	public final int getPixelSize()      		{ return pixelSize; }
	public final String getTitle() 		 		{ return title; }
	public final int getFPSCap() 		 		{ return FPSCap; }	
	public final boolean getShowFPS() 	 		{ return showFPS; }
	public final boolean getFullscreen() 		{ return fullscreen; }
	public final boolean key(int code) 		   	{ return key[code]; }	
	public final boolean mouse(int code) 	   	{ return mouse[code]; }
	public final boolean keyToggle(int code)   	{ return toggle(code, key, keyToggle); }	
	public final boolean mouseToggle(int code) 	{ return toggle(code, mouse, mouseToggle); }
	public final KeyEvent lastKeyPressed() 	   	{ return lastKeyPressed; }
	public final MouseEvent lastMouseInput()   	{ return lastMouseInput; }
	public final int getMouseX() 				{ return (int)(MouseInfo.getPointerInfo().getLocation().getX() - canvas.getLocationOnScreen().getX()); }
	public final int getMouseY() 				{ return (int)(MouseInfo.getPointerInfo().getLocation().getY() - canvas.getLocationOnScreen().getY()); }
	public final float getElapsedTime() 		{ return elapsedTime / 1000000000f; }
	public final RenderContext getScreen() 		{ return screen; }
	
	public final float getFPS()
	{
		long now = System.nanoTime();
		if(now >= FPSUpdate + FPS_UPDATE_PERIOD)
		{
			FPSUpdate = now;
			FPS = (SECOND * 1f / elapsedTime);
		}
		
		return FPS;
	}	
	
	/*********************************************************************/
	/* Setters                                                           */
	/*********************************************************************/
	
	public final void setShowFPS(boolean showFPS) { this.showFPS = showFPS; }
	
	public final void setSize(int width, int height, boolean fullscreen)
	{	
		if(frame != null)
		{
			if(this.fullscreen && fullscreen || !fullscreen && this.width == width && this.height == height)
				return;
			else
				frame.dispose();
		}		
		
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		
		if(fullscreen)
		{
			this.width = screenWidth;
			this.height = screenHeight;
		}
		else
		{			
			if(width < 1 || height < 1 || width > screenWidth || height > screenHeight)
				throw new IllegalArgumentException("Resolution " + width + "x" + height + " not compatible with the screen resolution.");
			
			this.width = width;
			this.height = height;
		}
		
		this.fullscreen = fullscreen;
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle(title);
		frame.setResizable(false);	
		
		canvas = new Canvas();			
		canvas.setSize(getWidth(), getHeight());
		
		frame.add(canvas);
		
		if(fullscreen)
		{
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			frame.setUndecorated(true);
		}
		else
		{	
			frame.pack();
			frame.setLocationRelativeTo(null);
		}
		
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		frame.setAlwaysOnTop(false);
		
		canvas.createBufferStrategy(1);
		
		bs = canvas.getBufferStrategy();
		gr = bs.getDrawGraphics();
		
		screen = new RenderContext(getWidth() / pixelSize, getHeight() / pixelSize);
		displayImage = new BufferedImage(getWidth() / pixelSize, getHeight() / pixelSize, BufferedImage.TYPE_3BYTE_BGR);
		displayComponents = ((DataBufferByte)displayImage.getRaster().getDataBuffer()).getData();
		
		displayImage.setAccelerationPriority(1);
		
		frame.addKeyListener(this);
		frame.addMouseListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
	}
	
	public final void setPixeSize(int pixelSize)
	{
		if(pixelSize < 1)
			return;
		
		this.pixelSize = pixelSize;	
		setSize(width, height, fullscreen);
	}
	
	public final void setTitle(String title)
	{
		if(title == null)
			title = "";
		
		this.title = title;
		if(frame != null)
			frame.setTitle(title);
	}	
	
	public final void setFPSCap(int FPSCap)
	{
		if(FPSCap < 0)
			return;
		
		this.FPSCap = FPSCap;
		if(this.FPSCap > 0)
			frameDuration = (long)(1f / this.FPSCap * SECOND);
	}
	
	/*********************************************************************/
	/* Events                                                            */
	/*********************************************************************/

	public final void keyPressed(KeyEvent e)
	{
		lastKeyPressed = e;
		key[e.getKeyCode()] = true;
	}

	public final void keyReleased(KeyEvent e)
	{
		key[e.getKeyCode()] = false;
	}
	
	public void mousePressed(MouseEvent e)
	{
		lastMouseInput = e;
		mouse[e.getButton()] = true;
	}

	public void mouseReleased(MouseEvent e)
	{
		mouse[e.getButton()] = false;
	}
	
	public final void keyTyped(KeyEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}	
}
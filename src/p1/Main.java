package p1;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Main
{
	private int width = 1000;
	private int height = 1000;
	
	private int mWidth = 10;
	private int mHeight = 10;
	
	private byte[] maze = new byte[mWidth * mHeight];	
	private short[] currentCell = new short[2];
	private int visited = 1;
	
	private ArrayList<short[]> stack = new ArrayList<short[]>();
	
	private final int tileSize;
	private final int tile10th;
	private final int tile5th;
	
	private long lastGeneration = 0;
	
	private final static long GENERATE_NEW = 10000000l;
	
	public Main()
	{
		SimpleEngineBuffered seb = new SimpleEngineBuffered(width, height, 1, "Recursive backtracking Maze Generator", 60, false, false)
		{
			public void update()
			{
				if(key(KeyEvent.VK_ESCAPE))
					stop();
				
				long now = System.nanoTime();
				
				if(now >= lastGeneration + GENERATE_NEW)
					lastGeneration = now;
				else
					return;
				
				if(visited < mWidth * mHeight)
				{
					short x = currentCell[0];
					short y = currentCell[1];
					
					boolean[] notVis = new boolean[4];
					
					if(x > 0 		  && maze[x-1 + y    * mWidth] == 0) // left
						notVis[0] = true;
					if(y > 0  		  && maze[x + (y-1)  * mWidth] == 0) // up
						notVis[1] = true;
					if(x < mWidth - 1  && maze[x+1 + y 	 * mWidth] == 0) // right
						notVis[2] = true;
					if(y < mHeight - 1 && maze[x + (y+1) * mWidth] == 0) // down
						notVis[3] = true;
					
					if(notVis[0] || notVis[1] || notVis[2] || notVis[3])
					{
						byte c = 0;
						for(byte i = 0; i < 4; i++)
							if(notVis[i])
								c++;
						
						if(c > 1)
							stack.add(new short[] {currentCell[0], currentCell[1]});
						
						byte next;						
						while(!notVis[(next = random((byte)4))]);
						
						switch(next)
						{
							case 0:
								currentCell[0]--;
								maze[currentCell[0] + currentCell[1] * mWidth] = 3;
								break;
							case 1:
								currentCell[1]--;
								maze[currentCell[0] + currentCell[1] * mWidth] = 4;
								break;
							case 2:
								currentCell[0]++;
								maze[currentCell[0] + currentCell[1] * mWidth] = 1;
								break;
							case 3:
								currentCell[1]++;
								maze[currentCell[0] + currentCell[1] * mWidth] = 2;
						}	

						visited++;
					}	
					else						
						currentCell = stack.remove(stack.size() - 1);
				}
				else
					currentCell[0] = -1;
			}
			
			public void render()
			{
				RenderContext screen = getScreen();
				screen.fillBackground();
				
				screen.setColor((byte)0xff, (byte)0x22, (byte)0x22, (byte)0x22);
				screen.fillRectangle(0, 0, width, height);						
				
				screen.setColor((byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff);
				for(short y = 0; y < mHeight; y++)
					for(short x = 0; x < mWidth; x++)
					{		
						if(maze[x + y * mWidth] == 0)
							continue;
						
						screen.fillRectangle(x * tileSize + tile10th, y * tileSize + tile10th, tileSize - tile5th, tileSize - tile5th);

						if(x > 0 && maze[x-1 + y     * mWidth] == 3)
							screen.fillRectangle(x * tileSize - tile10th, y * tileSize + tile10th, tile5th, tile5th * 4);
						
						if(y > 0 && maze[x   + (y-1) * mWidth] == 4)
							screen.fillRectangle(x * tileSize + tile10th, y * tileSize - tile10th, tile5th * 4, tile5th);
						
						if(x < mWidth - 1 && maze[x+1 + y     * mWidth] == 1)
							screen.fillRectangle((x+1) * tileSize - tile10th, y * tileSize + tile10th, tile5th, tile5th * 4);
						
						if(y < mHeight - 1 && maze[x   + (y+1) * mWidth] == 2)
							screen.fillRectangle(x * tileSize + tile10th, (y+1) * tileSize - tile10th, tile5th * 4, tile5th);
					}
				
				if(currentCell[0] != -1)
				{
					screen.setColor((byte)0xff, (byte)0x00, (byte)0xff, (byte)0x00);
					screen.fillRectangle(currentCell[0] * tileSize, currentCell[1] * tileSize, tileSize, tileSize);				
				}
			}
			
		};
		
		currentCell[0] = (short)(mWidth / 2);
		currentCell[1] = (short)(mHeight / 2);
		
		maze[currentCell[0] + currentCell[1] * mWidth] = -1;
		
		if(mWidth > mHeight)
			tileSize = 1000 / mWidth;
		else
			tileSize = 1000 / mHeight;
		
		tile5th  = tileSize / 5;
		tile10th = tileSize / 10;
		
		seb.start();
	}
	
	public byte random(byte n)
	{
		return (byte)(Math.random() * n);
	}
}

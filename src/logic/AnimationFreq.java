/**
 * @author Fabian Schaefer
 *
 */

package logic;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class AnimationFreq extends Animation {
/**
 * Klassenname wurde geaendert von Frequenz zu AnimationFreq - Grund: Somit ist der Bezeichner eindeutig
 */
	// ersetzt das Attribut String color, da weniger code und einfacheres Handle
	Color colors[] = {Color.BLUE, Color.RED, Color.GREEN}; 
	//Koordinaten auf der Sinuskurve
	int[] x;
	int[] y;
	BufferedImage img;
	double xPos;
	
	
	public AnimationFreq (int [] freq, JPanel pnl)
	{
		super(pnl);
		super.setFreq(freq);
		y = new int[360]; 
		x = new int[360];
		//Initialisierung - checkSize muss im Konstruktor initialisiert sein !!! (wegen resizing)
		setCheckResize(0);
		init();
	}

	protected void sin (boolean set)
	{	
		Color[] c = new Color[3];
		if(set == false)
		{
			c[0] = Color.GRAY;
			c[1] = Color.GRAY;
			c[2] = Color.GRAY;
		}
		else
		{
			c[0] = colors[0];
			c[1] = colors[1];
			c[2] = colors[2];
		}
		Graphics2D animationPnlBuffer;
		animationPnlBuffer = (Graphics2D) img.createGraphics();
		int nextPos = 0;
		int i,j;
		int q = height/5;
		while(nextPos < width)
		{
			for( j = 0; j < 3; j++)
			{
				for( i = 0; i < y.length; i++ )
				{
					y[i]=(int) (q*2.5-(int)Math.round(Math.sin(Math.toRadians(i)+xPos)*super.getFreq()[j]));
					x[i]=i;
					if(j == 0)y[i]-=q;
					else if (j==1) y[i]+=0;
					else y[i]+=q;
				}
				animationPnlBuffer.setColor(c[j]);
				animationPnlBuffer.setStroke(new BasicStroke(5.0f));
				animationPnlBuffer.drawPolyline(x, y, x.length);
			}
			animationPnlBuffer.translate((int)x[x.length-1],0);
			nextPos += (int)x[x.length-1];
		}
	}

	
	
	
	//Vererbte Methoden
	
	@Override
	public void init() {//TODO Session session
		// TODO Auto-generated method stub
		// Initialisierung der Werte
		width = pnl.getSize().width; // muss �ber Dimension gemacht werden, da Punkte und Pixel nicht vergleichbar w�ren
		height = pnl.getSize().height;
		animationPnl = (Graphics2D) pnl.getGraphics();
		img = animationPnl.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.BITMASK);
		xPos = 0;
		setCheckResize(getCheckResize() + 1);
		if(getCheckResize()%2 == 0)
		{
			tempo = 0;
		}
		else
		{
			tempo = 30;
		}
		super.start();
	}

	@Override
	public void run() {
	Thread thisThread = Thread.currentThread();
	while (animation == thisThread) 
		{
			synchronized(thisThread)
			{	while(pause)
				{  
					try 
					{
		                  thisThread.wait();
		            } 
					catch (Exception e) 
					{
		                  e.printStackTrace();
		            }
				}
			}
			xPos += 0.1f;
			sin(true);
			animationPnl.drawImage(img, null, 0, 0);
			sin(false);

			try 
			{
				Thread.sleep (tempo);	
			}	
			catch (InterruptedException e) 
			{
				//nichts
			}				
        }
	}
	
	@Override
	public void pause (boolean state) {
		if(state == false) pause = false;
		else 	
		{
			pause = true;
		}
	}

	@Override
	public boolean finish (boolean state) {
		if(state == false)
		{
			return false;
		}
		else 	
		{
			//Animation wird �bermalt
			Rectangle2D rectangle = new Rectangle2D.Double(0, 0, width, height);
			animationPnl.setColor(Color.GRAY);
			animationPnl.fill(rectangle);
			animation = null;
			return true;
		}
		
	}
	
	@Override
	public void setHandle(Graphics2D animationpnl) {
		// TODO Auto-generated method stub
		
	}
	
	//getter & setter
	public Color [] getColor ()
	{
		return colors;
	}
	
	public void setColor (Color[] color)
	{
		this.colors = color;
	}
	
}

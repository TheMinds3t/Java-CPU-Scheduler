package cs405.scheduler.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class QueuePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public QueuePanel() {
		super();
		repaint();
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D graphics = (Graphics2D)g;
		graphics.setColor(Color.black);
		int x = getX()+this.getParent().getX();
		int y = getY()+this.getParent().getY();
		int width = getWidth();
		int height = getHeight();
		int rectW = width/10, rectH = height/10;
		;
		System.out.println("X="+x+",Y="+y+",W="+width+",H="+height);
		graphics.drawRect(x, y, rectW, rectH);
		graphics.drawRect(x+width-rectW, y+height-rectH, rectW, rectH);
		super.paintComponent(g);
	}

}

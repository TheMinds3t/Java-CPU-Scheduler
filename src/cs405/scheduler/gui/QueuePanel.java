package cs405.scheduler.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class QueuePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private String cpuTask = null, ioTask = null;
	private ArrayList<String> cpuTasks = new ArrayList<String>();
	private ArrayList<String> ioTasks = new ArrayList<String>();

	/**
	 * Create the panel.
	 */
	public QueuePanel() {
		super();
		repaint();
		ioTasks.clear();
		cpuTasks.clear();
		cpuTasks.add("P1");
//		cpuTasks.add("P2");
//		cpuTasks.add("P2");
		ioTasks.add("P3");
		ioTasks.add("P4");
		ioTask = null;
		cpuTask = null;
		//ioTask = "P5";
		cpuTask = "P6";
	}
	
	//Populates the panel with the queue imagery
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D graphics = (Graphics2D)g;
		
		//define constants
		int x = getX();
		int y = getY();
		int width = getWidth();
		int height = getHeight();
		int rectW = width/8, rectH = height/3;
		int margin = 5;
		
		int cpuX = x+margin;
		int cpuY = y+margin;
		int ioX = x+width-rectW-margin;
		int ioY = y+height-(int)(rectH*1.5)+margin;
		Font f = graphics.getFont();
		
		//offset to fix graphic bug
		g.translate(0, -height/8+margin);
		
		//distinguish two queue regions
		graphics.setColor(Color.gray.brighter());
		graphics.fillRect(cpuX-margin,cpuY-margin,width,height/2);
		
		//IO/CPU boxes
		graphics.setColor(Color.black);
		graphics.drawString("Ready queue",cpuX,height/2+margin);
		graphics.drawString("Processing queue",ioX-20,height+margin);
		graphics.fillRect(cpuX, cpuY, rectW, rectH);
		graphics.fillRect(ioX, ioY, rectW, rectH);
		
		//line to connect cpu tasks
		graphics.drawLine(cpuX, cpuY+rectH/2, cpuX+(int)(cpuTasks.size()*rectW*1.25)+rectW/2, cpuY+rectH/2);
		//line to connect io tasks
		graphics.drawLine(ioX, ioY+rectH/2, ioX-(int)(ioTasks.size()*rectW*1.25)+rectW/2, ioY+rectH/2);
		drawCornerArrow(graphics, cpuX-rectW/3,cpuY+height/2,rectW,rectH);
		graphics.rotate(Math.toRadians(180), ioX, ioY);
		drawCornerArrow(graphics, ioX-rectW/3*4,ioY+rectH/3,rectW,rectH);
		graphics.rotate(Math.toRadians(180), ioX, ioY);

		//Labels
		graphics.setColor(Color.white);
		drawCentered(graphics, "CPU 1", cpuX + margin, cpuY+margin+f.getSize2D());
		
		String cTask = cpuTask == null ? "Idle" : cpuTask;
		graphics.setColor(Color.green);
		if(!cTask.equals("Idle"))
		{
			graphics.setColor(Color.darkGray);
			graphics.fillOval(cpuX+rectW/4, cpuY+margin*2+rectH/4, rectW/2, rectH/2);
			graphics.setColor(Color.white);
		}
		
		drawCentered(graphics, cTask, cpuX + rectW/2-f.getSize2D(), cpuY+rectH/3+f.getSize2D()*2);
			
		drawCentered(graphics, "I/O 1", ioX + margin, ioY + margin + f.getSize2D());

		String iTask = ioTask == null ? "Idle" : ioTask;

		graphics.setColor(Color.green);
		if(!iTask.equals("Idle"))
		{
			graphics.setColor(Color.darkGray);
			graphics.fillOval(ioX+rectW/4, ioY+margin*2+rectH/4, rectW/2, rectH/2);
			graphics.setColor(Color.white);
		}
		
		drawCentered(graphics, iTask, ioX + rectW/2-f.getSize2D(), ioY+rectH/3+f.getSize2D()*2);

		rectH -= f.getSize()*2;
		
		for(int i = 0; i < cpuTasks.size(); ++i)
		{
			String task = cpuTasks.get(i);
			graphics.setColor(Color.darkGray);
			graphics.fillOval(cpuX+(int)(rectW*1.25*(i+1)), cpuY+f.getSize(), rectW-f.getSize()*2, rectH);
			graphics.setColor(Color.white);
			drawCentered(graphics, task, cpuX+(int)(rectW*1.25*(i+1))+rectW/2-f.getSize()/2*3,cpuY+f.getSize()+rectH/2);
		}

		for(int i = 0; i < ioTasks.size(); ++i)
		{
			String task = ioTasks.get(i);
			graphics.setColor(Color.darkGray);
			graphics.fillOval(ioX-(int)(rectW*1.25*(i+1)), ioY+f.getSize(), rectW-f.getSize()*2, rectH);
			graphics.setColor(Color.white);
			drawCentered(graphics, task, ioX-(int)(rectW*1.25*(i+1))+rectW/2-f.getSize()/2*3,ioY+f.getSize()+rectH/2);
		}
	}
	
	/**
	 * draws an arrow with a 90 degree pivot at the specified coordinates with the specified width.
	 * @param g graphics object to draw to.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param w the width of the full arrow.
	 * @param h the height of the full arrow.
	 */
	private void drawCornerArrow(Graphics2D g, int x, int y, int w, int h)
	{
		g.setColor(Color.black);
		g.drawLine(x+w/2, y+h/10, x+w/2, y+h/10*9);
		g.drawLine(x+w/2, y+h/10*9, x+w, y+h/10*9);
		g.drawLine(x+w,y+h/10*9,x+w-w/10,y+h/10*9-h/10);
		g.drawLine(x+w,y+h/10*9,x+w-w/10,y+h/10*9+h/10);
	}
	
	/**
	 * Draws the given string centered on the specified coordinates.
	 * @param g the graphics object to use for drawing
	 * @param str the string to draw
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	private void drawCentered(Graphics2D g, String str, float x, float y)
	{
		g.drawString(str,x,y);
	}
	
	/**
	 * Sets the queued CPU tasks to render next to the CPU block.
	 * @param tasks the list of tasks, in order, for the CPU.
	 */
	public void setQueuedCPUTasks(ArrayList<String> tasks)
	{
		cpuTasks = tasks;
	}

	/**
	 * Sets the queued IO tasks to render next to the IO block.
	 * @param tasks the list of tasks, in order, for the IO.
	 */
	public void setQueuedIOTasks(ArrayList<String> tasks)
	{
		ioTasks = tasks;
	}
	
	/**
	 * Sets the current CPU task, rendered inside the CPU block
	 * @param task the current task for the CPU to process
	 */
	public void setCurrentCPUTask(String task)
	{
		cpuTask = task;
	}
	
	/**
	 * Sets the current IO task, rendered inside the IO block
	 * @param task the current task for the IO to process
	 */
	public void setCurrentIOTask(String task)
	{
		ioTask = task;
	}
}

package cs405.scheduler.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import cs405.process.Burst;

public class QueuePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private String cpuTask = null, ioTask = null;
	private ArrayList<String> cpuTasks = new ArrayList<String>();
	private ArrayList<String> ioTasks = new ArrayList<String>();
	private final CPUFrame frame;
	
	/**
	 * Create the panel.
	 */
	public QueuePanel(CPUFrame frame) {
		super();
		this.frame = frame;
	}
	
	//Populates the panel with the queue imagery
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D graphics = (Graphics2D)g;

		ioTasks.clear();
		cpuTasks.clear();
		cpuTasks.add("P1");
		cpuTasks.add("P2");
//		cpuTasks.add("P2");
		ioTasks.add("P3");
//		ioTasks.add("P4");
		ioTask = null;
		cpuTask = null;
		ioTask = "P5";
		cpuTask = "P6";

		
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
		float height_scale = height/230.0f;
		g.translate(0, (int)(-height/8*height_scale+margin));
		
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

		//Block Labels
		graphics.setColor(Color.white);
		drawCentered(graphics, "CPU 1", cpuX + rectW/2, cpuY+margin*2+f.getSize2D());
		
		//Current cpu task
		String cTask = cpuTask == null ? "Idle" : cpuTask;
		graphics.setColor(Color.green);
		if(!cTask.equals("Idle"))
		{
			graphics.setColor(Color.darkGray);
			graphics.fillOval(cpuX+rectW/4-4, cpuY+margin+rectH/4, rectW/2+8, rectH/2+8);
			graphics.setColor(Color.white);
		}
		
		drawCentered(graphics, cTask, cpuX + rectW/2, cpuY+margin*2+rectH/2+4);			

		//Block Labels
		graphics.setColor(Color.white);
		drawCentered(graphics, "I/O 1", ioX + rectW/2, ioY + margin*2 + f.getSize2D());
		//Current IO task
		String iTask = ioTask == null ? "Idle" : ioTask;

		graphics.setColor(Color.green);
		if(!iTask.equals("Idle"))
		{
			graphics.setColor(Color.darkGray);
			graphics.fillOval(ioX+rectW/4-4, ioY+margin+rectH/4, rectW/2+8, rectH/2+8);
			graphics.setColor(Color.white);
		}
		
		drawCentered(graphics, iTask, ioX + rectW/2, ioY+margin*2+rectH/2+4);
		int radius = (rectW+rectH)/2;

		
		for(int i = 0; i < cpuTasks.size(); ++i)
		{
			String task = cpuTasks.get(i);
			graphics.setColor(Color.darkGray);
			graphics.fillOval(cpuX+(int)(rectW*1.25)+(int)(radius*1.25*(i)), cpuY+f.getSize(), radius, radius);
			graphics.setColor(Color.white);
			drawCentered(graphics, task, cpuX+(int)(rectW*1.25)+(int)(radius*1.25*(i))+radius/2,cpuY+(int)(f.getSize()*1.5)+radius/2);
		}

		for(int i = 0; i < ioTasks.size(); ++i)
		{
			String task = ioTasks.get(i);
			graphics.setColor(Color.darkGray);
			graphics.fillOval(ioX-(int)(rectW*1.25)-(int)(radius*1.25*(i)), ioY+f.getSize(), radius, radius);
			graphics.setColor(Color.white);
			drawCentered(graphics, task, ioX-(int)(rectW*1.25)-(int)(radius*1.25*(i))+radius/2,ioY+(int)(f.getSize()*1.5)+radius/2);
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
	private void drawCentered(Graphics g, String text, float x, float y) {
	    // Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(g.getFont());
	    // Determine the X coordinate for the text
	    x -= metrics.stringWidth(text) / 2;
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    y -= metrics.getAscent()/2;
	    // Draw the String
	    g.drawString(text, (int)x, (int)y);
	    //Source:
	    //https://stackoverflow.com/questions/27706197/how-can-i-center-graphics-drawstring-in-java
	}
	
	/**
	 * Sets the queued CPU tasks to render next to the CPU block.
	 * @param tasks the list of tasks, in order, for the CPU.
	 */
	public void setQueuedCPUTasks(ArrayList<String> tasks)
	{
		cpuTasks = tasks;
		frame.repaintComponents(GuiComponent.QUEUE);
	}

	/**
	 * Sets the queued IO tasks to render next to the IO block.
	 * @param tasks the list of tasks, in order, for the IO.
	 */
	public void setQueuedIOTasks(ArrayList<String> tasks)
	{
		ioTasks = tasks;
		frame.repaintComponents(GuiComponent.QUEUE);
	}
	
	/**
	 * Sets the current CPU task, rendered inside the CPU block
	 * @param task the current task for the CPU to process
	 */
	public void setCurrentCPUTask(String task)
	{
		cpuTask = task;
		frame.repaintComponents(GuiComponent.QUEUE);
	}
	
	/**
	 * Sets the current IO task, rendered inside the IO block
	 * @param task the current task for the IO to process
	 */
	public void setCurrentIOTask(String task)
	{
		ioTask = task;
		frame.repaintComponents(GuiComponent.QUEUE);
	}

	/**
	 * Moves the CPU queue's head to the current CPU task, and moves the first queued item into the CPU block.
	 * 
	 * @return the process string that just exited the CPU.
	 */
	public String popCPUTask()
	{
		String ret = cpuTask;
		cpuTask = cpuTasks.remove(0);
		frame.repaintComponents(GuiComponent.QUEUE);
		return ret;
	}
	
	/**
	 * Moves the IO queue's head to the current IO task, and moves the first queued item into the IO block.
	 * 
	 * @return the process string that just exited the IO.
	 */
	public String popIOTask()
	{
		String ret = ioTask;
		ioTask = ioTasks.remove(0);
		frame.repaintComponents(GuiComponent.QUEUE);
		return ret;
	}
	
	/**
	 * Sets the current specified task, rendered inside the specified block
	 * @param burst the side to set the current task for
	 * @param task the current task for the specified to process
	 */
	public void setCurrentTask(Burst side, String task)
	{
		if(side == Burst.CPU)
			cpuTask = task;
		else
			ioTask = task;			
		
		frame.repaintComponents(GuiComponent.QUEUE);
	}
	
	/**
	 * Sets the queued specified tasks to render next to the specified block.
	 * @param burst the side to set the list of tasks for 
	 * @param tasks the list of tasks, in order, for the specified block.
	 */
	public void setQueuedTasks(Burst side, ArrayList<String> tasks)
	{
		if(side == Burst.CPU)
			cpuTasks = tasks;
		else
			ioTasks = tasks;
		
		frame.repaintComponents(GuiComponent.QUEUE);
	}

	
	/**
	 * Moves the specified queue's head to the current specified task, and moves the first queued item into the specified block.
	 * 
	 * @param side the queue to pop from
	 * @return the process string that just exited the IO.
	 */
	public String popTask(Burst side)
	{
		String ret = side == Burst.CPU ? cpuTask : ioTask;
		
		if(side == Burst.CPU)
			cpuTask = ioTasks.remove(0);			
		else
			ioTask = ioTasks.remove(0);

		frame.repaintComponents(GuiComponent.QUEUE);
		return ret;
	}
}

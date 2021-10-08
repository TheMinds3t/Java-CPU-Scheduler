package cs405.scheduler;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import cs405.process.Process;
import cs405.process.State;
import cs405.scheduler.gui.CPUFrame;

public class Dispatcher { // tells the scheduler when it needs to work
	private ArrayList<Process> allProcesses;
	private LinkedList<Process> IOqueue;
	private CPUFrame gui;
	private SynchronizedCounter counter;
	
	Dispatcher(){
		this.gui = new CPUFrame(this);
		this.counter = new SynchronizedCounter();
		this.IOqueue = new LinkedList<Process>();
	}
	
	public void startGui() {
		this.gui.setVisible(true);
	}
	
	/**
	 * Helper function to allow processes to add to process log
	 * @param message - the message to add to the log
	 * @param color - the color of the message
	 */
	public void addToProcessLog(String message, Color color) {
		this.gui.addToProcessLog(message, color);
	}
	
	public void popIO(Process proc) {
		if (proc.equals(IOqueue.peek())) { // double check we are attempting to remove the head
			IOqueue.pop();
			Process next = IOqueue.peek();
			if(next != null) { // tell the next process it's at the head
				next.setIO();
				System.out.println(next.toString());
				gui.getQueuePanel().setCurrentIOTask(Integer.toString(next.getId()));
			} else {
				System.out.println("none");
				gui.getQueuePanel().setCurrentIOTask(null);
			}
			
			// update queue in gui
			ArrayList<String> queue = new ArrayList<String>();
			for (int i = 1; i < IOqueue.size(); i++) {
				queue.add(Integer.toString(IOqueue.get(i).getId()));
			}
			this.gui.getQueuePanel().setQueuedIOTasks(queue);
			
			this.gui.addToProcessLog("Process " + proc.getId() + ": Finished IO and moved back to ready queue at " + counter.getCount(), Color.BLUE);
		}
	}
	
	/**
	 * Add a process to the IO Queue
	 * The process should be the object calling this
	 * @param proc - the process to add to the queue
	 */
	public void pushIO(Process proc) {
		this.IOqueue.add(proc);
		if (IOqueue.peek().equals(proc)) { // if process is now head of queue
			proc.setIO();
			this.gui.getQueuePanel().setCurrentIOTask(Integer.toString(proc.getId()));
		}
		ArrayList<String> queue = new ArrayList<String>();
		for (int i = 1; i < IOqueue.size(); i++) {
			queue.add(Integer.toString(IOqueue.get(i).getId()));
		}
		this.gui.getQueuePanel().setQueuedIOTasks(queue);
		this.gui.addToProcessLog("Process " + proc.getId() + ": Entered IO Queue at " + counter.getCount(), Color.ORANGE);
	}

	public void loadFromFile(File file) {
		new Thread(()->{
			Scanner fileinput;
			try {
				fileinput = new Scanner(file);
			} catch (FileNotFoundException e) {
				System.err.println("Error: File not found");
				return;
			}
			int index = 0;
			this.allProcesses = new ArrayList<Process>();
			while (fileinput.hasNext()) {
				String line = fileinput.nextLine();
				String[] params = line.split("\\s+"); // split line on whitespace
				ArrayList<Integer> cpu = new ArrayList<Integer>(); 
				ArrayList<Integer> io = new ArrayList<Integer>(); 
				for (int i = 3; i < params.length; i++) { // params 0-2 are not bursts
					if (i % 2 == 1) { // every odd i is a cpu burst
						cpu.add(Integer.parseInt(params[i]));
					} else { // every even i is an io burst
						io.add(Integer.parseInt(params[i]));
					}
				}
				Process proc = new Process(index, params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]), cpu, io, counter, this);
				this.allProcesses.add(proc);
				index++;
			}
			fileinput.close();

			this.publishProcesses();
			
			// TESTING: put process 0 in the IO queue and step until it's done
			pushIO(allProcesses.get(0));
			for (int i = 0; i < 24; i++) {
				counter.tickUp();
				// For some reason this does all the sleeping and then updates the gui
				// instead of updating the gui between every sleep
				// note: the data does appear to update correctly, it just isn't shown until the end
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.publishProcesses();
			}			
		}).start();
	}
	
	private void publishProcesses() {
		Object[][] arr = new Object[this.allProcesses.size()][10];
		for (int i = 0; i < this.allProcesses.size(); i++) {
			arr[i] = this.allProcesses.get(i).getInformation();
		}
		this.gui.setTableData(arr);
	}
	
}

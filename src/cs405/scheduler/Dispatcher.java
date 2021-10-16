package cs405.scheduler;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import cs405.process.Process;
import cs405.process.State;
import cs405.scheduler.gui.CPUFrame;

/**
 * Middle layer between the scheduler and the GUI
 * @author Emma Rector and Alissa Teigland
 */
public class Dispatcher { // tells the scheduler when it needs to work
	
	public SynchronizedCounter counter; // GUI needs access to this sometimes
	
	private ArrayList<Process> allProcesses;
	private LinkedList<Process> ioQueue;
	private Process currentProcess; // process currently running on the CPU
	private LinkedList<Process> cpuQueue;

	private CPUFrame gui;
	private Scheduler scheduler;
	
	private boolean started;
	public void setCounter(SynchronizedCounter counter) {
		this.counter = counter;
	}

	public void setAllProcesses(ArrayList<Process> allProcesses) {
		this.allProcesses = allProcesses;
	}

	public void setIoQueue(LinkedList<Process> ioQueue) {
		this.ioQueue = ioQueue;
	}

	public void setCurrentProcess(Process currentProcess) {
		this.currentProcess = currentProcess;
	}

	public void setCpuQueue(LinkedList<Process> cpuQueue) {
		this.cpuQueue = cpuQueue;
	}

	public void setGui(CPUFrame gui) {
		this.gui = gui;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public void setTimeOnCpu(int timeOnCpu) {
		this.timeOnCpu = timeOnCpu;
	}

	public void setTimeUtilized(int timeUtilized) {
		this.timeUtilized = timeUtilized;
	}

	public int timeOnCpu;
	public int timeUtilized; 

	Dispatcher() {
		gui = new CPUFrame(this);
		counter = new SynchronizedCounter();
		allProcesses = new ArrayList<Process>();
		ioQueue = new LinkedList<Process>();
		cpuQueue = new LinkedList<Process>();
		scheduler = new Scheduler();
		started = false;
		timeOnCpu = 0;
		timeUtilized = 0;
	}

	public void startGui() {
		gui.setVisible(true);
	}


	/**
	 * Starts automatically ticking the counter at the rate set by the gui
	 * Runs when the start/stop button is clicked
	 */
	public void toggleStartStop() {
		if (allProcesses.size() == 0) {
			return;
		}
		started = !started;
		if (started) {
			addToProcessLog("STARTED", Color.BLACK);
			
			int algorithm = gui.getSelectedAlgorithm();
			String algMessage = "Algorithm used: ";
			switch (algorithm) {
				case 0:
					algMessage += "FCFS";
					break;
				case 1:
					algMessage += "Priority";
					break;
				case 2:
					algMessage += "SJF";
					break;
				case 3:
					algMessage += "RR, quantum = " + gui.getQValue();
					break;
			}
			addToProcessLog(algMessage, Color.MAGENTA);
			
			new Thread(() -> {
				while (started) {
					tickUp();
					// if all processes are terminated, end loop, prompt save
					if (allProcesses.stream().filter(p -> p.getState() == State.TERMINATED).count() == allProcesses.size()) {
						started = false;
						addToProcessLog("STOPPED", Color.BLACK);
					}
					try {
						Thread.sleep(1000 / gui.getSelectedFrameRate());
					}
					catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		} else {
			addToProcessLog("STOPPED", Color.BLACK);
		}
	}
	
	/**
	 * Updates the CPU queue and tells the gui
	 * Includes round robin handling
	 */
	private void updateCPU() {
		if (gui.getSelectedAlgorithm() == 3 && timeOnCpu == gui.getQValue()) { // if RR and quantum time elapsed
			if (currentProcess != null && currentProcess.getState() == State.RUNNING) {
				currentProcess.preempt();
			}
			currentProcess = null;
		}
		Process p = scheduleProcesses(); // sort processes
		if (p == null) {
			gui.getQueuePanel().setCurrentCPUTask(null);
			currentProcess = null;
		} else if (currentProcess == null || currentProcess.getId() != p.getId()) {
			if (currentProcess != null && currentProcess.getState() == State.RUNNING) {
				currentProcess.preempt();
			}
			gui.getQueuePanel().setCurrentCPUTask(Integer.toString(p.getId()));
			currentProcess = p;
			timeOnCpu = 1;
			p.setCPU();
		} else { // process stays on CPU
			timeOnCpu++;
		}
		

		// update cpu queue
		ArrayList<String> queue = new ArrayList<String>();
		for (int i = 1; i < cpuQueue.size(); i++) {
			queue.add(Integer.toString(cpuQueue.get(i).getId()));
		}
		gui.getQueuePanel().setQueuedCPUTasks(queue);
	}

	public void tickUp() {
		// if everything is done, don't keep going
		if (allProcesses.stream().filter(p -> p.getState() == State.TERMINATED).count() == allProcesses.size()) {
			addToProcessLog("No processes to run", Color.RED);
			return;
		}
		
		counter.tickUp(); // increase system time
		updateCPU();
		if (currentProcess != null) {
			timeUtilized++;
		}
		publishProcesses(); // refills process table
		gui.setSystemData(counter.getCount(), getThroughput(), getTurnaround(), getWait(), getUtilization()); // sets system statistics

		if (allProcesses.stream().filter(p -> p.getState() == State.NEW).count() == allProcesses.size()) {
			addToProcessLog("No processes arrived by time " + counter.getCount(), Color.ORANGE);
		}
	}

	private double getThroughput() {
		if (counter.getCount() == 0) {
			return 0;
		} else {
			int finished = 0;
			for (int i = 0; i < allProcesses.size(); i++) {
				if (allProcesses.get(i).getState() == State.TERMINATED) {
					finished++;
				}
			}
			
			BigDecimal bd = BigDecimal.valueOf(finished / (double) (counter.getCount()));
		    bd = bd.setScale(3, RoundingMode.HALF_UP);

			return bd.doubleValue(); // return average throughput time
		}
	}

	private double getTurnaround() {
		int count = 0;
		int totalTime = 0;
		for (int i = 0; i < allProcesses.size(); i++) {
			if (allProcesses.get(i).getState() == State.TERMINATED) {
				count++;
				totalTime += allProcesses.get(i).getTurnaround();
			}
		}
		if (count == 0) { // if no processes are finished, display 0
			return 0;
		}
		
		BigDecimal bd = BigDecimal.valueOf(totalTime / (double) (count));
	    bd = bd.setScale(3, RoundingMode.HALF_UP);

		return bd.doubleValue(); // return average turnaround time
	}

	private double getWait() {
		int totalTime = 0;
		for (int i = 0; i < allProcesses.size(); i++) {
			totalTime += allProcesses.get(i).getWait();
		}
		if (allProcesses.size() == 0) { // if no processes are loaded, display 0
			return 0;
		}
		
		BigDecimal bd = BigDecimal.valueOf(totalTime / (double) (allProcesses.size()));
	    bd = bd.setScale(3, RoundingMode.HALF_UP);

		return bd.doubleValue(); // return average wait time
	}
	
	private String getUtilization() {
		BigDecimal bd = BigDecimal.valueOf((double)timeUtilized / counter.getCount() * 100);
	    bd = bd.setScale(3, RoundingMode.HALF_UP);
	    
		return bd.doubleValue() + "%";
	}

	/**
	 * Helper function to allow processes to add to process log
	 * 
	 * @param message - the message to add to the log
	 * @param color   - the color of the message
	 */
	public void addToProcessLog(String message, Color color) {
		gui.addToProcessLog(message, color);
	}

	/**
	 * Remove a process from the IO Only ever removes the head of the queue
	 * 
	 * @param proc - the process to remove
	 */
	public void popIO(Process proc) {
		if (proc.equals(ioQueue.peek())) { // double check we are attempting to remove the head
			ioQueue.pop();
			Process next = ioQueue.peek();
			if (next != null) { // tell the next process it's at the head
				next.setIO();
				gui.getQueuePanel().setCurrentIOTask(Integer.toString(next.getId()));
			} else {
				gui.getQueuePanel().setCurrentIOTask(null);
			}

			// update queue in gui
			ArrayList<String> queue = new ArrayList<String>();
			for (int i = 1; i < ioQueue.size(); i++) {
				queue.add(Integer.toString(ioQueue.get(i).getId()));
			}
			gui.getQueuePanel().setQueuedIOTasks(queue);

			gui.addToProcessLog(
					"Process " + proc.getId() + ": Finished IO and moved back to ready queue at " + counter.getCount(),
					Color.BLUE);
		}
	}

	/**
	 * Add a process to the IO Queue The process should be the object calling this
	 * 
	 * @param proc - the process to add to the queue
	 */
	public void pushIO(Process proc) {
		ioQueue.add(proc);
		if (ioQueue.peek().equals(proc)) { // if process is now head of queue
			proc.setIO();
			gui.getQueuePanel().setCurrentIOTask(Integer.toString(proc.getId()));
		}
		ArrayList<String> queue = new ArrayList<String>();
		for (int i = 1; i < ioQueue.size(); i++) {
			queue.add(Integer.toString(ioQueue.get(i).getId()));
		}
		gui.getQueuePanel().setQueuedIOTasks(queue);
		gui.addToProcessLog("Process " + proc.getId() + ": Entered IO Queue at " + counter.getCount(), Color.ORANGE);
	}

	/**
	 * Parses an input file and loads it into the system
	 * 
	 * @param file - the input file
	 */
	public void loadFromFile(File file) {
		Scanner fileinput;
		try {
			fileinput = new Scanner(file);
		}
		catch (FileNotFoundException e) {
			System.err.println("Error: File not found");
			return;
		}
		int index = 0;
		allProcesses = new ArrayList<Process>();
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
			Process proc = new Process(index, params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]), cpu,
					io, counter, this);
			allProcesses.add(proc);
			index++;
		}
		fileinput.close();

		publishProcesses();
	}
	
	private LinkedList<Process> makeCpuQueue() {
		LinkedList<Process> queue = new LinkedList<Process>();
		for (int i = 0; i < allProcesses.size(); i++) {
			if (allProcesses.get(i).getState() == State.READY || allProcesses.get(i).getState() == State.RUNNING) {
				queue.add(allProcesses.get(i));
			}
		}
		return queue;
	}

	/**
	 * Gets scheduling method from GUI and has Scheduler arrange processes
	 */
	private Process scheduleProcesses() {		
		int algorithm = gui.getSelectedAlgorithm();
		cpuQueue = makeCpuQueue();

		if (cpuQueue.size() == 0) {
			return null;
		}
		switch (algorithm) {
			case 0:
				return scheduler.FCFS(cpuQueue);
			case 1:
				return scheduler.PS(cpuQueue);
			case 2:
				return scheduler.SJF(cpuQueue);
			case 3:
				//return scheduler.RR(cpuQueue, gui.getQValue());
				return scheduler.FCFS(cpuQueue);
			default:
				throw new IllegalArgumentException("No matching method for input " + algorithm);
		}

	}

	/**
	 * Sets the GUI table with the list of all processes
	 */
	private void publishProcesses() {
		Object[][] arr = new Object[allProcesses.size()][10];
		for (int i = 0; i < allProcesses.size(); i++) {
			arr[i] = allProcesses.get(i).getInformation();
		}
		gui.setTableData(arr);
	}

}

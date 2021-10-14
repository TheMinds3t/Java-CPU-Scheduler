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
	private Scheduler scheduler;
	private SynchronizedCounter counter;
	private boolean started;

	Dispatcher() {
		gui = new CPUFrame(this);
		counter = new SynchronizedCounter();
		IOqueue = new LinkedList<Process>();
		scheduler = new Scheduler();
		started = false;
	}

	public void startGui() {
		gui.setVisible(true);
	}

	public void toggleStart(int FPS) {
		started = !started;
		if (started) {
			addToProcessLog("STARTED", Color.BLACK);
			new Thread(() -> {
				while (started) {
					tickUp();
					try {
						Thread.sleep(1000 / FPS);
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

	public void tickUp() {
		counter.tickUp(); // increase system time
		scheduleProcesses();
		publishProcesses();
		gui.setSystemData(counter.getCount(), getThroughput(), getTurnaround(), getWait());
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
			return finished / (double) (counter.getCount());
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
		return totalTime / (double) (count); // return average turnaround time
	}

	private double getWait() {
		int totalTime = 0;
		for (int i = 0; i < allProcesses.size(); i++) {
			totalTime += allProcesses.get(i).getWait();
		}
		if (allProcesses.size() == 0) { // if no processes are loaded, display 0
			return 0;
		}
		return totalTime / (double) (allProcesses.size()); // return average wait time
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
		if (proc.equals(IOqueue.peek())) { // double check we are attempting to remove the head
			IOqueue.pop();
			Process next = IOqueue.peek();
			if (next != null) { // tell the next process it's at the head
				next.setIO();
				gui.getQueuePanel().setCurrentIOTask(Integer.toString(next.getId()));
			} else {
				gui.getQueuePanel().setCurrentIOTask(null);
			}

			// update queue in gui
			ArrayList<String> queue = new ArrayList<String>();
			for (int i = 1; i < IOqueue.size(); i++) {
				queue.add(Integer.toString(IOqueue.get(i).getId()));
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
		IOqueue.add(proc);
		if (IOqueue.peek().equals(proc)) { // if process is now head of queue
			proc.setIO();
			gui.getQueuePanel().setCurrentIOTask(Integer.toString(proc.getId()));
		}
		ArrayList<String> queue = new ArrayList<String>();
		for (int i = 1; i < IOqueue.size(); i++) {
			queue.add(Integer.toString(IOqueue.get(i).getId()));
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

	/**
	 * Gets scheduling method from GUI and has Scheduler arrange processes
	 */
	private void scheduleProcesses() {
		for (Process process : allProcesses) {
			System.out.println(process.getId());
		}
		int algorithm = gui.getSelectedAlgorithm();
		switch (algorithm) {
		case 0:
			scheduler.FCFS(allProcesses);
			break;
		case 1:
			scheduler.PS(allProcesses);
			break;
		case 2:
			scheduler.SJF(allProcesses);
			break;
		case 3:
			scheduler.RR(allProcesses, gui.getQValue());
			break;
		default:
			throw new IllegalArgumentException("No matching method for input " + algorithm);
		}
		for (Process process : allProcesses) {
			System.out.println(process.getId());
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

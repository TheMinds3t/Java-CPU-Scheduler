package cs405.process;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import cs405.scheduler.Dispatcher;
import cs405.scheduler.SynchronizedCounter;

public class Process {
	// data given in constructor
	private int pid; // process id
	private int arrivalTime; // the system time the process should arrive
	private String name; // the name of the process
	private int priority; // the priority level of the process
	private List<Integer> CPUbursts; // the list of CPU bursts
	private List<Integer> IObursts; // the list of IO bursts
	private SynchronizedCounter systemTime; // system unit time
	private Dispatcher dispatcher; // the dispatcher

	// data calculated or set
	private State processState; // the current process state
	private Integer startTime; // the system time the process is first executed by the CPU, Integer to allow null
	private Integer finishTime; // the system time the process terminates
	private int CPUwait; // the total time waiting in the CPU queue (time READY)
	private int IOwait; // the total time waiting in the IO queue (time WAITING)
	private int turnaroundTime; // the total execution time of a process (finishTime - arrivalTime)
	private Burst currentBurstList; // which list is currently being worked on
	private int currentBurstIndex; // how many IO bursts have been completed
	private int burstCompletion; // how much of the burst has been completed
	private boolean isCurrentIO; // is the process currently the front of the IO queue

	
	public Process(int id, String name, int arrivalTime, int priority, List<Integer> CPUbursts, List<Integer> IObursts, SynchronizedCounter counter, Dispatcher dispatcher) {
		// passed to constructor
		this.pid = id;
		this.name = name;
		this.arrivalTime = arrivalTime;
		this.priority = priority;
		this.CPUbursts = CPUbursts;
		this.IObursts = IObursts;
		this.systemTime = counter;
		this.dispatcher = dispatcher;
		
		// increment the counter whenever the systemTime is incremented
		this.systemTime.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				incrementCounter();
			}
		});
		
		// set
		this.startTime = null;
		this.finishTime = null;
		this.processState = State.NEW;
		this.CPUwait = 0;
		this.IOwait = 0;
		this.currentBurstList = Burst.CPU;
		this.currentBurstIndex = 0;
		this.isCurrentIO = false;
		
		// TESTING:
		this.currentBurstList = Burst.IO;
		this.processState = State.WAITING;
	}
	
	/**
	 * gets the processes id
	 * @return the processes id
	 */
	public int getId() {
		return this.pid;
	}
	
	/**
	 * gets the processes priority
	 * @return the processes priority
	 */
	public int getPriority() {
		return this.priority;
	}
	
	/**
	 * gets the processes arrival time
	 * @return the processes arrival time
	 */
	public int getArrivalTime() {
		return this.arrivalTime;
	}
	
	/**
	 * gets the next cpu burst, or how much is left of the current burst if it's RUNNING
	 * @return the processes cpu burst
	 */
	public int getNextCPUBurst() {
		if (this.processState == State.RUNNING) { // currently working on CPU
			return this.CPUbursts.get(this.currentBurstIndex) - this.burstCompletion;
		} else if (this.processState == State.WAITING) { // after working on CPU
			return this.CPUbursts.get(this.currentBurstIndex + 1);
		} else { // before working on CPU
			return this.CPUbursts.get(this.currentBurstIndex);
		}
	}
	
	/**
	 * Gets the process information for the gui table
	 * @return an array with all process information
	 */
	public Object[] getInformation() {
		// {ID (int), Arrival (int), Priority (int), CPU Bursts (String), I/O Bursts (String), Start Time (int), End Time (int), Wait Time (int), Wait I/O Time (int), Status (String)}

		Object[] arr = new Object[10];
		arr[0] = this.pid;
		arr[1] = this.arrivalTime;
		arr[2] = this.priority;
		arr[3] = getBurstInfo(this.CPUbursts);
		arr[4] = getBurstInfo(this.IObursts);
		arr[5] = this.startTime;
		arr[6] = this.finishTime;
		arr[7] = this.CPUwait;
		arr[8] = this.IOwait;
		arr[9] = this.processState;
		
		return arr;
	}
	
	private String getBurstInfo(List<Integer> list) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (currentBurstIndex > i) { // already did burst
				sb.append("0/" + list.get(i));
			} else if (currentBurstIndex == i) {
				sb.append((list.get(i) - burstCompletion) + "/" + list.get(i));
			} else {
				sb.append(list.get(i) + "/" + list.get(i));
			}
			sb.append(" ");
		}
		return sb.toString();
	}
	/**
	 * For testing purposes.
	 * Prints the data from the process
	 */
	public String toString() {
		Object[] arr = this.getInformation();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length - 1; i++) {
			if (arr[i] != null) {
				sb.append(arr[i].toString());
			} else {
				sb.append(" ");
			}
			sb.append(", ");
		}
		if (arr[arr.length - 1] != null) {
			sb.append(arr[arr.length - 1].toString());
		} else {
			sb.append(" ");
		}
		return sb.toString();
	}
	
	/**
	 * Sets the process from RUNNING to READY and prints a message to the log
	 */
	public void preempt() {
		setState(State.READY);
		// TODO: print to process log a preempt message
	}
	
	/**
	 * Sets the process from READY to RUNNING and prints a message to the log
	 * Note: Process will not work on the CPU burst until the next tick up
	 */
	public void setCPU() {
		setState(State.RUNNING);
		// TODO: print to process log a message
	}
	
	/**
	 * Tells the process it is at the head of the IO queue so it can mark progress
	 * Note: Process will not work on the IO burst until the next tick up
	 */
	public void setIO() {
		this.isCurrentIO = true;
	}
	
	/**
	 * sets a new process state and information that changes on state change
	 * @param newState - the State the process is switched to
	 */
	public void setState(State newState) {
		this.isCurrentIO = false;
		this.processState = newState;

		if (newState == State.TERMINATED) {
			// TODO: tell process log process has terminated, print turnaround + wait times
			this.finishTime = this.systemTime.getCount();
			this.turnaroundTime = this.finishTime - this.arrivalTime;
		} else if (newState == State.RUNNING && this.currentBurstIndex == 0) { // first CPU
			this.startTime = this.systemTime.getCount();
		} else if (newState == State.WAITING) { // add to IO queue
			dispatcher.pushIO(this);
		}
	}
	
	/**
	 * Process handles having spent a tick WAITING 
	 * WAITING refers to waiting for IO and includes being at the front of the IO queue
	 */
	private void waiting() {
		this.IOwait++;
		if (this.isCurrentIO) { 
			// Head of IO queue, so got response from IO
			this.burstCompletion++;
			if (this.burstCompletion == this.IObursts.get(currentBurstIndex)) {
				// finished IO, go back to CPU
				this.currentBurstList = Burst.CPU;
				this.burstCompletion = 0;
				this.currentBurstIndex++; 
				this.dispatcher.popIO(this);
				setState(State.READY);
			}
		}
	}
	
	/**
	 * Process handles having spent a tick READY 
	 * READY refers to waiting for CPU but not at the front of the CPU queue (see running)
	 */
	private void ready() {
		this.CPUwait++;
	}
	
	/**
	 * Process handles having spent a tick RUNNING 
	 * RUNNING refers to being actively on the CPU
	 */
	private void running() {
		this.burstCompletion++;
		if (this.burstCompletion == this.CPUbursts.get(this.currentBurstIndex)) {
			// finished CPU burst, move onto next IO burst or terminate
			if (this.currentBurstIndex < IObursts.size()) {
				// there is an IO burst, switch to IO
				this.currentBurstList = Burst.IO;
				this.burstCompletion = 0;
				setState(State.WAITING);
			} else {
				// there is no more IO, last CPU burst just finished
				setState(State.TERMINATED);
			}
		}
	}
	
	/**
	 * increments the processes system time by one.
	 * handles increasing process wait counts and burst progress
	 */
	private void incrementCounter() {
		// based on previous state, update counters
		if (this.processState == State.WAITING) { // previously waiting for IO
			this.waiting();
		} else if (this.processState == State.READY) { // previously waiting for CPU
			this.ready();
		} else if (this.processState == State.RUNNING) { // previously running on CPU
			this.running();
		} else if (this.processState == State.NEW) {
			// set process to ready if at arrival time
			if (this.arrivalTime == this.systemTime.getCount()) { // system time is now at arrival time
				setState(State.READY);
				this.dispatcher.addToProcessLog("Process " + this.pid + " arrived at time " + this.systemTime, Color.GREEN);
			}
		}
	}

}

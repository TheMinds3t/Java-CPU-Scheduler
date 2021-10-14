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
	private int currentBurstIndex; // how many IO bursts have been completed
	private int burstCompletion; // how much of the burst has been completed
	private boolean isCurrentIO; // is the process currently the front of the IO queue

	
	public Process(int id, String name, int arrivalTime, int priority, List<Integer> CPUbursts, List<Integer> IObursts, SynchronizedCounter counter, Dispatcher dispatcher) {
		// passed to constructor
		pid = id;
		this.name = name;
		this.arrivalTime = arrivalTime;
		this.priority = priority;
		this.CPUbursts = CPUbursts;
		this.IObursts = IObursts;
		systemTime = counter;
		this.dispatcher = dispatcher;
		
		// increment the counter whenever the systemTime is incremented
		systemTime.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				incrementCounter();
			}
		});
		
		// set data
		startTime = null;
		finishTime = null;
		processState = State.NEW;
		CPUwait = 0;
		IOwait = 0;
		currentBurstIndex = 0;
		isCurrentIO = false;
		turnaroundTime = 0;
	}
	
	/**
	 * gets the processes id
	 * @return the processes id
	 */
	public int getId() {
		return pid;
	}
	
	/**
	 * gets the processes priority
	 * @return the processes priority
	 */
	public int getPriority() {
		return priority;
	}
	
	/**
	 * gets the processes arrival time
	 * @return the processes arrival time
	 */
	public int getArrivalTime() {
		return arrivalTime;
	}
	
	/**
	 * gets the processes state
	 * @return the processes state
	 */
	public State getState() {
		return processState;
	}
	
	public int getTurnaround() {
		return turnaroundTime;
	}
	
	public int getWait() {
		return CPUwait;
	}
	
	/**
	 * gets the next cpu burst, or how much is left of the current burst if it's RUNNING
	 * @return the processes cpu burst
	 */
	public int getNextCPUBurst() {
		if (processState == State.RUNNING) { // currently working on CPU
			return CPUbursts.get(currentBurstIndex) - burstCompletion;
		} else if (processState == State.WAITING) { // after working on CPU
			return CPUbursts.get(currentBurstIndex + 1);
		} else { // before working on CPU
			return CPUbursts.get(currentBurstIndex);
		}
	}
	
	/**
	 * Gets the process information for the gui table
	 * @return an array with all process information
	 */
	public Object[] getInformation() {
		// {ID (int), Arrival (int), Priority (int), CPU Bursts (String), I/O Bursts (String), Start Time (int), End Time (int), Wait Time (int), Wait I/O Time (int), Status (String)}

		Object[] arr = new Object[10];
		arr[0] = pid;
		arr[1] = arrivalTime;
		arr[2] = priority;
		arr[3] = getBurstInfo(CPUbursts);
		arr[4] = getBurstInfo(IObursts);
		arr[5] = startTime;
		arr[6] = finishTime;
		arr[7] = CPUwait;
		arr[8] = IOwait;
		arr[9] = processState;
		
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
		Object[] arr = getInformation();
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
		isCurrentIO = true;
	}
	
	/**
	 * sets a new process state and information that changes on state change
	 * @param newState - the State the process is switched to
	 */
	public void setState(State newState) {
		isCurrentIO = false;
		processState = newState;

		if (newState == State.TERMINATED) {
			// TODO: tell process log process has terminated, print turnaround + wait times
			finishTime = systemTime.getCount();
			turnaroundTime = finishTime - arrivalTime;
		} else if (newState == State.RUNNING && currentBurstIndex == 0) { // first CPU
			startTime = systemTime.getCount();
		} else if (newState == State.WAITING) { // add to IO queue
			dispatcher.pushIO(this);
		}
	}
	
	/**
	 * Process handles having spent a tick WAITING 
	 * WAITING refers to waiting for IO and includes being at the front of the IO queue
	 */
	private void waiting() {
		IOwait++;
		if (isCurrentIO) { 
			// Head of IO queue, so got response from IO
			burstCompletion++;
			if (burstCompletion == IObursts.get(currentBurstIndex)) {
				// finished IO, go back to CPU
				burstCompletion = 0;
				currentBurstIndex++; 
				dispatcher.popIO(this);
				setState(State.READY);
			}
		}
	}
	
	/**
	 * Process handles having spent a tick READY 
	 * READY refers to waiting for CPU but not at the front of the CPU queue (see running)
	 */
	private void ready() {
		CPUwait++;
	}
	
	/**
	 * Process handles having spent a tick RUNNING 
	 * RUNNING refers to being actively on the CPU
	 */
	private void running() {
		burstCompletion++;
		if (burstCompletion == CPUbursts.get(currentBurstIndex)) {
			// finished CPU burst, move onto next IO burst or terminate
			if (currentBurstIndex < IObursts.size()) {
				// there is an IO burst, switch to IO
				burstCompletion = 0;
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
		if (processState == State.WAITING) { // previously waiting for IO
			waiting();
		} else if (processState == State.READY) { // previously waiting for CPU
			ready();
		} else if (processState == State.RUNNING) { // previously running on CPU
			running();
		} else if (processState == State.NEW) {
			// set process to ready if at arrival time
			if (arrivalTime == systemTime.getCount()) { // system time is now at arrival time
				setState(State.READY);
				dispatcher.addToProcessLog("Process " + pid + " arrived at time " + systemTime.getCount(), Color.GREEN);
			}
		}
	}

}

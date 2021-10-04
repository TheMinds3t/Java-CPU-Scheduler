package cs405.process;

import java.util.List;

public class Process {
	// data given in constructor
	private int pid; // process id
	private int arrivalTime; // the system time the process should arrive
	private String name; // the name of the process
	private int priority; // the priority level of the process
	private List<Integer> CPUbursts; // the list of CPU bursts
	private List<Integer> IObursts; // the list of IO bursts
	
	// data calculated or set
	private int MyCounter; // system unit time
	private State processState; // the current process state
	private Integer startTime; // the system time the process is first executed by the CPU, Integer to allow null
	private Integer finishTime; // the system time the process terminates
	private int CPUwait; // the total time waiting in the CPU queue (time READY)
	private int IOwait; // the total time waiting in the IO queue (time WAITING)
	private int turnaroundTime; // the total execution time of a process (finishTime - arrivalTime)
	private Burst currentBurstList; // which list is currently being worked on
	private int currentBurstIndex; // which element of the list is currently being worked on
	private int burstCompletion; // how much of the burst has been completed
	private boolean isCurrentIO; // is the process currently the front of the IO queue

	
	public Process(int id, String name, int arrivalTime, int priority, List<Integer> CPUbursts, List<Integer> IObursts) {
		this.pid = id;
		this.name = name;
		this.arrivalTime = arrivalTime;
		this.priority = priority;
		this.CPUbursts = CPUbursts;
		this.IObursts = IObursts;
		
		this.startTime = null;
		this.finishTime = null;
		this.MyCounter = 0;
		this.processState = State.NEW;
		this.CPUwait = 0;
		this.IOwait = 0;
		this.currentBurstList = Burst.CPU;
		this.currentBurstIndex = 0;
		this.isCurrentIO = false;
	}
	
	/**
	 * gets the processes priority
	 * @return the processes priority
	 */
	public int getPriority() {
		return this.priority;
	}
	
	/**
	 * TODO
	 * gets the list of process information
	 * @return a string with all process information
	 */
	public Object[] getInformation() {
		// {ID (int), Arrival (int), Priority (int), CPU Bursts (String), I/O Bursts (String), Start Time (int), End Time (int), Wait Time (int), Wait I/O Time (int), Status (String)}

		Object[] arr = new Object[10];
		arr[0] = this.pid;
		arr[1] = this.arrivalTime;
		arr[2] = this.priority;
		arr[3] = this.CPUbursts.toString();
		arr[4] = this.IObursts.toString();
		arr[5] = this.startTime;
		arr[6] = this.finishTime;
		arr[7] = this.CPUwait;
		arr[8] = this.IOwait;
		arr[9] = this.processState;
		
		return arr;
	}
	
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
	 * sets a new process state and information that changes on state change
	 * @param newState - the State the process is switched to
	 */
	public void setState(State newState) {
		if (newState == State.TERMINATED) {
			this.finishTime = this.MyCounter;
			this.turnaroundTime = this.finishTime - this.arrivalTime;
		} else if (newState == State.RUNNING && this.currentBurstIndex == 0) { // first CPU
			this.startTime = this.MyCounter;
		}
		
		this.processState = newState;
		this.isCurrentIO = false;
	}
	
	/**
	 *  tells the process it is currently the head of the IO queue so it can mark progress
	 */
	public void setCurrentIO() {
		this.isCurrentIO = true;
	}
	
	/**
	 * increments the processes system time by one.
	 * handles increasing process wait counts and burst progress
	 */
	public void incrementCounter() {
		this.MyCounter++; // update system time
		// based on previous state, update counters
		if (this.processState == State.WAITING) { // previously waiting for IO
			this.IOwait++;
			if (this.isCurrentIO) { // previously did IO
				this.burstCompletion++;
				if (this.burstCompletion == this.IObursts.get(currentBurstIndex)) {
					// finished IO, go back to CPU
					this.currentBurstList = Burst.CPU;
					this.burstCompletion = 0;
					setState(State.READY);
				}
			}
		} else if (this.processState == State.READY) { // previously waiting for CPU
			this.CPUwait++;
		} else if (this.processState == State.RUNNING) { // previously running on CPU
			this.burstCompletion++;
			if (this.burstCompletion == this.CPUbursts.get(this.currentBurstIndex)) {
				// finished CPU burst i, move onto IO burst i or terminate
				if (IObursts.size() >= this.currentBurstIndex) {
					// there is an IO burst, prepare for IO
					this.currentBurstList = Burst.IO;
					this.burstCompletion = 0;
					setState(State.WAITING);
				} else {
					// there is no more IO, last CPU burst just finished
					setState(State.TERMINATED);
				}
			}
		}
		// set process to ready if at arrival time
		if (this.arrivalTime == this.MyCounter) { // system time is now at arrival time
			setState(State.READY);
		}
	}
}

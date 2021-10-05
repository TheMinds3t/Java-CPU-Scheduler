package cs405.scheduler;

import cs405.process.Process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Scheduler {

	private List<Process> processes;
	private SynchronizedCounter syCount;
	private int roundRobinIndex = -1;

	public Scheduler(SynchronizedCounter counter) {
		this.processes = new ArrayList<>();
		this.syCount = counter;
	}

	public void addProcess(Process p) {
		processes.add(p);
	}

	public Process FCFS() {
		// First come, first served	- ordered by arrival time
		processes.sort(Comparator.comparing(Process::getArrivalTime));
		return processes.get(0);
	}

	public Process SJF() {
		// Shortest job first - ordered by smallest next CPU burst
		processes.sort(Comparator.comparing(Process::getNextCPUBurst));
		return processes.get(0);
	}

	public Process RR(int quantum) {
		// Going through each process in turn
		
		// Quantum checking handled by dispatcher?
		if (roundRobinIndex > processes.size() - 1) { // no longer a valid index
			roundRobinIndex = -1;
		}
		
		roundRobinIndex++;		
		return processes.get(roundRobinIndex);
	}

	public Process PS() {
		// priority scheduling
		// Processes of equal priority will be ordered as FCFS
		// Assumes low value = high priority
		processes.sort(Comparator.comparing(Process::getPriority));
		return processes.get(0);
	}

}

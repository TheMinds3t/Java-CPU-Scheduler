package cs405.scheduler;

import cs405.process.Process;
import java.util.Comparator;
import java.util.List;

public class Scheduler {
	
	// Does various sorting of lists - dispatcher will handle passing a process to CPU

	public Scheduler() {}

	public Process FCFS(List<Process> processes) {
		// First come, first served	- ordered by arrival time
		processes.sort(Comparator.comparing(Process::getArrivalTime));
		return processes.get(0);
	}

	public Process SJF(List<Process> processes) {
		// Shortest job first - ordered by smallest next CPU burst
		processes.sort(Comparator.comparing(Process::getNextCPUBurst));
		return processes.get(0);
	}

	public Process RR(List<Process> processes) {
		return FCFS(processes); // the dispatcher handles the quantum time
	}

	public Process PS(List<Process> processes) {
		// priority scheduling
		// Processes of equal priority will be ordered as FCFS
		// Assumes low value = high priority
		processes.sort(Comparator.comparing(Process::getPriority)
								.thenComparing(Process::getArrivalTime));
		return processes.get(0);
	}

}

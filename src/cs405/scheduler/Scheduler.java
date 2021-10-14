package cs405.scheduler;

import cs405.process.Process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Scheduler {
	
	// Does various sorting of lists - dispatcher will handle passing a process to CPU

	private int roundRobinIndex = -1;

	public Scheduler() {}

	public Process FCFS(List<Process> processes) {
		// First come, first served	- ordered by arrival time
		processes.sort(Comparator.comparing(Process::getArrivalTime));
		return processes.get(0);
	}

	public Process SJF(List<Process> processes) {
		System.out.println(processes.hashCode());
		// Shortest job first - ordered by smallest next CPU burst
		processes.sort(Comparator.comparing(Process::getNextCPUBurst));
		System.out.println(processes.hashCode());
		return processes.get(0);
	}

	public Process RR(List<Process> processes, int quantum) {
		// Going through each process in turn
		// Order of processes is FCFS
		
		// Quantum checking handled by dispatcher?
		if (roundRobinIndex > processes.size() - 1) { // no longer a valid index
			roundRobinIndex = -1;
		}
		
		roundRobinIndex++;
		processes.sort(Comparator.comparing(Process::getArrivalTime));
		return processes.get(roundRobinIndex);
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

package cs405.process;

public enum State {
	NEW, 		// The process is just created, it is not READY yet
	READY, 		// The process is ready to be executed by the CPU but is waiting in queue
	RUNNING, 	// The process is currently being executed by the CPU
	WAITING, 	// The process is in the I/O queue waiting for the I/O device
	TERMINATED 	// The process has finished it's tasks
}
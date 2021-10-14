package cs405.scheduler;

public class SynchronizedCounter {
	
	// Wrapper around an int that is used in multiple places
	// More useful when we get to threading?
	
	int counter;
	
	public SynchronizedCounter() {
		counter = 0;
	}
	
	public synchronized int getCount() {
		return counter;
	}
	
	public synchronized int tickUp() {
		counter++;
		return counter;
	}

}

package cs405.scheduler;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * A counter to keep track of system time
 * Allows for listeners
 * @author Emma Rector and Alissa Teigland
 */
public class SynchronizedCounter {
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private int counter;
	
	public SynchronizedCounter() {
		counter = 0;
	}
	
	public synchronized void reset() {
		counter = 0;
	}
	
	public synchronized int getCount() {
		return counter;
	}
	
	public synchronized int tickUp() {
		counter++;
		this.pcs.firePropertyChange("tick", counter - 1, counter);
		return counter;
	}
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

}

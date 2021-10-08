package cs405.scheduler;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SynchronizedCounter {
	
	// Wrapper around an int that is used in multiple places
	// More useful when we get to threading?
	
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private int counter;
	
	public SynchronizedCounter() {
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

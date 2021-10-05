package cs405.devices;

import cs405.scheduler.SynchronizedCounter;

public abstract class AbstractDevice {
	
	private SynchronizedCounter syncCount;
	private int busyTimeCount;
	private boolean isBusy;	
	
	public AbstractDevice(SynchronizedCounter sc) {
		this.syncCount = sc;
	}
	
	public boolean getBusyStatus() {
		return this.isBusy;
	}
	
	public void onTick() {
		if (this.isBusy) {
			busyTimeCount++;			
		}
	}
	
	
	public void startProcess(int duration) {
		this.isBusy = true;
	}
	
	public void finish() {
		this.isBusy = false;
	}
	
	public double calculateUtilization() {
		return busyTimeCount / (double) syncCount.getCount();
	}

}

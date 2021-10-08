package cs405.scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import cs405.process.Process;
import cs405.scheduler.gui.CPUFrame;

public class Dispatcher { // tells the scheduler when it needs to work
	private ArrayList<Process> allProcesses;
	private CPUFrame gui;
	private SynchronizedCounter counter;
	
	Dispatcher(){
		this.gui = new CPUFrame(this);
		this.counter = new SynchronizedCounter();
	}
	
	public void startGui() {
		this.gui.setVisible(true);
	}
	
	public void loadFromFile(File file) {
		Scanner fileinput;
		try {
			fileinput = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.err.println("Error: File not found");
			return;
		}
		int index = 0;
		this.allProcesses = new ArrayList<Process>();
		while (fileinput.hasNext()) {
			String line = fileinput.nextLine();
			String[] params = line.split("\\s+"); // split line on whitespace
			ArrayList<Integer> cpu = new ArrayList<Integer>(); 
			ArrayList<Integer> io = new ArrayList<Integer>(); 
			for (int i = 3; i < params.length; i++) { // params 0-2 are not bursts
				if (i % 2 == 1) { // every odd i is a cpu burst
					cpu.add(Integer.parseInt(params[i]));
				} else { // every even i is an io burst
					io.add(Integer.parseInt(params[i]));
				}
			}
			Process proc = new Process(index, params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]), cpu, io, counter);
			this.allProcesses.add(proc);
			index++;
		}
		fileinput.close();
		
		this.publishProcesses();
	}
	
	private void publishProcesses() {
		Object[][] arr = new Object[this.allProcesses.size()][10];
		for (int i = 0; i < this.allProcesses.size(); i++) {
			arr[i] = this.allProcesses.get(i).getInformation();
		}
		this.gui.setTableData(arr);
	}
}

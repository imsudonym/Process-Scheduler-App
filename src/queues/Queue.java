package queues;

import java.util.ArrayList;

import gui.GanttChart;
import process.CPUBoundProcess;
import process.IOBoundProcess;
import scheduler.Scheduler;

public abstract class Queue {
	protected static long prevTime = 0;
	protected static long prevTimeQuantum;
	protected static CPUBoundProcess prevProcess;
	
	protected boolean suspended = false;
	
	protected void simulationDone(ArrayList<CPUBoundProcess> processList){
		ArrayList<CPUBoundProcess> temp = processList;
		int count =  temp.size();
		
		double avgResponse = 0;
		double avgWait = 0;
		double avgTurnaround = 0;
		
		for(int i = 0; i < count; i++) {
			temp.get(i).setWaitTimePreemptive();
			
			System.out.print("[p" + temp.get(i).getId() + "]: ");
			System.out.println("timesPreempted = " + temp.get(i).timePreempted.size() + " timesResumed = " + temp.get(i).timeResumed.size() 
					+ " waitTime: " + temp.get(i).getWaitTime() + " responseTime: " + temp.get(i).getResponseTime() + " turnAround: " + temp.get(i).getTurnaroundTime());
			
			avgResponse += temp.get(i).getResponseTime();
			avgWait += temp.get(i).getWaitTime();
			avgTurnaround += temp.get(i).getTurnaroundTime();
			
			int c = temp.get(i).getTimesPreempted();			
			for(int j = 0; j < c; j++) {
				System.out.print(temp.get(i).timePreempted.get(j) + "-");
				System.out.print(temp.get(i).timeResumed.get(j) + "|");
			}
			System.out.println();
		}
		
		avgResponse = avgResponse/count;
		avgWait = avgWait/count;
		avgTurnaround = avgTurnaround/count;
		
		System.out.println("avgResponse = " + avgResponse + " avgWait = " + avgWait + " avgTurnaround = " + avgTurnaround);
		
		GanttChart.simulationDone(this);
	}
	
	protected void insertToReadyQueue(IOBoundProcess process) {
		System.out.println("Insert p" + process.getId() + " with new arrival time = " + process.getArrivalTime());
		Scheduler.enqueue(process);
	}

}


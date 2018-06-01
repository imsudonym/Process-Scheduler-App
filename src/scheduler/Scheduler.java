package scheduler;
import java.util.ArrayList;

import constants.SchedulingAlgorithm;
import gui.GanttChart;
import process.CPUBoundProcess;
import process.IOBoundProcess;
import queues.FCFSQueue;
import queues.NonPQueue;
import queues.PQueue;
import queues.RRQueue;
import queues.SJFQueue;
import queues.SRTFQueue;

public class Scheduler {
	private static int itr = 0;
	public static long clockTime = 0;	
	private static boolean running = false;
	
	public static Object[] queues;
	private static int numOfQueues = 0;
		
	public static ArrayList<CPUBoundProcess> processes = new ArrayList<CPUBoundProcess>();
	
	public void initProcesses(int numOfQueues, ArrayList<CPUBoundProcess> processes){
		Scheduler.numOfQueues = numOfQueues;
		Scheduler.queues = new Object[numOfQueues];
		
		Scheduler.processes = processes;
		
		sortByArrivalTime();
		if(queues[0] instanceof PQueue){
			preSortSameArrivalTime();
		}				
	}
	
	private static void sortByArrivalTime() {
		/*System.out.println("Before sort by arrival...");
		//printContents(processes);
		System.out.println("Sorting by Arrival Time");*/
		int psize = processes.size();
		for(int i = 0; i < psize; i++){
			for(int j = i; j < psize; j++){
				if(processes.get(i).getArrivalTime() > processes.get(j).getArrivalTime()){
					CPUBoundProcess temp = processes.get(i);
					processes.set(i, processes.get(j));
					processes.set(j, temp); 
				}
			}			
		}
		//printContents(processes);
	}

	private static void preSortSameArrivalTime() {
		int psize = processes.size();
		for(int i = 0; i < psize; i++){
			for(int j = i; j < psize; j++){
				if(processes.get(i).getArrivalTime() == processes.get(j).getArrivalTime() && processes.get(i).getPriority() > processes.get(j).getPriority()){
					CPUBoundProcess temp = processes.get(i);
					processes.set(i, processes.get(j));
					processes.set(j, temp);
				}
			}			
		}
		//printContents(processes);
	}

	private static void printContents(ArrayList<CPUBoundProcess> processes2) {
		System.out.print("[");
		for(int i = 0; i < processes2.size(); i++){
			System.out.print("p" + processes2.get(i).getId());
		}
		System.out.println("]");		
	}
	
	public static void enqueue(IOBoundProcess process) {
		processes.add(process);
		sortByArrivalTime();
	}

	public void simulate(){
		running  = true;		
		clock.start();
	}
	
	public static void stop(){
		clock.interrupt();		
		clockTime = 0;
	}
	
	public void generateQueues(int[] algorithms, int[] quantums){
		System.out.println("Generating multilevel queues...");
		for(int i = 0; i < numOfQueues; i++){	
			if(algorithms[i] == SchedulingAlgorithm.FCFS){
				queues[i] = new FCFSQueue(i);
				((FCFSQueue) queues[i]).startThread();
				if(i == 0) ((FCFSQueue) queues[i]).startExecution();
			}else if (algorithms[i] == SchedulingAlgorithm.RR){
				queues[i] = new RRQueue(i, quantums[i]);			
				((RRQueue) queues[i]).startThread();
				if(i == 0) ((RRQueue) queues[i]).startExecution();
			}else if (algorithms[i] == SchedulingAlgorithm.SJF){
				queues[i] = new SJFQueue(i);
				((SJFQueue) queues[i]).setNumberOFProcesses(processes.size());
				((SJFQueue) queues[i]).startThread();
			}else if (algorithms[i] == SchedulingAlgorithm.NP_PRIO){
				queues[i] = new NonPQueue(i);
				((NonPQueue) queues[i]).setNumberOFProcesses(processes.size());
				((NonPQueue) queues[i]).startThread();
			}else if (algorithms[i] == SchedulingAlgorithm.PRIO){
				queues[i] = new PQueue(i);
				((PQueue) queues[i]).setNumberOFProcesses(processes.size());
				((PQueue) queues[i]).startThread();
			}else if (algorithms[i] == SchedulingAlgorithm.SRTF){
				queues[i] = new SRTFQueue(i);
				((SRTFQueue) queues[i]).setNumberOFProcesses(processes.size());
				((SRTFQueue) queues[i]).startThread();
			}
		}	
		
		for(int i = 0; i < numOfQueues; i++) {
		
			if(queues[i] instanceof FCFSQueue){
				if(i == numOfQueues-1) { 
					if(numOfQueues != 1)
						((FCFSQueue) queues[i]).setPrevQueue(queues[i-1]);
					else
						((FCFSQueue) queues[i]).setPrevQueue(null);
					((FCFSQueue) queues[i]).setNextQueue(null);
				}else if(i == 0) {
					((FCFSQueue) queues[i]).setPrevQueue(null);
					((FCFSQueue) queues[i]).setNextQueue(queues[i+1]);
				}else {
					((RRQueue) queues[i]).setPrevQueue(queues[i-1]);
					((RRQueue) queues[i]).setNextQueue(queues[i+1]);
				}
			}else if (queues[i] instanceof RRQueue){
				if(i == numOfQueues-1) { 
					if(numOfQueues != 1)
						((RRQueue) queues[i]).setPrevQueue(queues[i-1]);
					else
						((RRQueue) queues[i]).setPrevQueue(null);
					((RRQueue) queues[i]).setNextQueue(null);
				}else if(i == 0) {
					((RRQueue) queues[i]).setPrevQueue(null);
					((RRQueue) queues[i]).setNextQueue(queues[i+1]);
				}else {
					((RRQueue) queues[i]).setPrevQueue(queues[i-1]);
					((RRQueue) queues[i]).setNextQueue(queues[i+1]);
				}
			}else if (queues[i] instanceof SJFQueue){
				if(i == numOfQueues-1) { 
					if(numOfQueues != 1)
						((SJFQueue) queues[i]).setPrevQueue(queues[i-1]);
					else
						((SJFQueue) queues[i]).setPrevQueue(null);
					((SJFQueue) queues[i]).setNextQueue(null);
				}else if(i == 0) {
					((SJFQueue) queues[i]).setPrevQueue(null);
					((SJFQueue) queues[i]).setNextQueue(queues[i+1]);
				}else {
					((SJFQueue) queues[i]).setPrevQueue(queues[i-1]);
					((SJFQueue) queues[i]).setNextQueue(queues[i+1]);
				}
			}else if (queues[i] instanceof NonPQueue){
				if(i == numOfQueues-1) { 
					if(numOfQueues != 1)
						((NonPQueue) queues[i]).setPrevQueue(queues[i-1]);
					else
						((NonPQueue) queues[i]).setPrevQueue(null);
					((NonPQueue) queues[i]).setNextQueue(null);
				}else if(i == 0) {
					((NonPQueue) queues[i]).setPrevQueue(null);
					((NonPQueue) queues[i]).setNextQueue(queues[i+1]);
				}else {
					((NonPQueue) queues[i]).setPrevQueue(queues[i-1]);
					((NonPQueue) queues[i]).setNextQueue(queues[i+1]);
				}
			}else if (queues[i] instanceof PQueue){
				if(i == numOfQueues-1) { 
					if(numOfQueues != 1)
						((PQueue) queues[i]).setPrevQueue(queues[i-1]);
					else
						((PQueue) queues[i]).setPrevQueue(null);
					((PQueue) queues[i]).setNextQueue(null);
				}else if(i == 0) {
					((PQueue) queues[i]).setPrevQueue(null);
					((PQueue) queues[i]).setNextQueue(queues[i+1]);
				}else {
					((PQueue) queues[i]).setPrevQueue(queues[i-1]);
					((PQueue) queues[i]).setNextQueue(queues[i+1]);
				}
			}else if (queues[i] instanceof SRTFQueue){
				if(i == numOfQueues-1) { 
					if(numOfQueues != 1)
						((SRTFQueue) queues[i]).setPrevQueue(queues[i-1]);
					else
						((SRTFQueue) queues[i]).setPrevQueue(null);
					((SRTFQueue) queues[i]).setNextQueue(null);
				}else if(i == 0) {
					((SRTFQueue) queues[i]).setPrevQueue(null);
					((SRTFQueue) queues[i]).setNextQueue(queues[i+1]);
				}else {
					((SRTFQueue) queues[i]).setPrevQueue(queues[i-1]);
					((SRTFQueue) queues[i]).setNextQueue(queues[i+1]);
				}
			}
		}
	}			
	
	private static void insertOnQueue(CPUBoundProcess newProcess){				
		//timeArrive = System.currentTimeMillis();	
		
		if(queues[0] instanceof FCFSQueue){
			((FCFSQueue) queues[0]).enqueue(newProcess);		
		}else if(queues[0] instanceof RRQueue){
			((RRQueue) queues[0]).enqueue(newProcess);
		}else if(queues[0] instanceof SJFQueue){
			((SJFQueue) queues[0]).enqueue(newProcess);
		}else if(queues[0] instanceof NonPQueue){
			((NonPQueue) queues[0]).enqueue(newProcess);
		}else if(queues[0] instanceof PQueue){
			((PQueue) queues[0]).enqueue(newProcess);
		}else if(queues[0] instanceof SRTFQueue){
			((SRTFQueue) queues[0]).enqueue(newProcess);
		}

		int burstTime = newProcess.getBurstNeeded();
		int arrivalTime = newProcess.getArrivalTime();
		int priority = newProcess.getPriority();
		
		GanttChart.addNewArrivedProcess(newProcess.getId(), arrivalTime, burstTime, priority);
	}		
	
	static Thread clock = new Thread(){
		public void run(){
			System.out.println("running: " + running);
			while(running){				
								
				while(processes.size() > 0){								
					if(processes.get(0).getArrivalTime() == clockTime){						
						System.out.println("Clock time: " + clockTime + " insert p" + processes.get(0).getId());
						insertOnQueue(processes.get(0));
						processes.remove(0);
						itr++;
					}else if(processes.get(0).getArrivalTime() > clockTime){						
						break;
					}
				}
								
				clockTime++;
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}
	};
		
	public void restart() {
		itr = 0;
		clockTime = 0;
		running = true;		
	}
						
	public static int getMaxLevelOfQueues() {
		return numOfQueues-1;
	}
}

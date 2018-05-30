package scheduler;
import constants.SchedulingAlgorithm;
import gui.GanttChart;
import process.CPUBoundProcess;
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
	public static CPUBoundProcess[] processes;
	private static int numOfQueues = 0;
		
	public void initProcesses(int numOfQueues, CPUBoundProcess[] processes){
		Scheduler.numOfQueues = numOfQueues;
		Scheduler.queues = new Object[numOfQueues];
		
		Scheduler.processes = processes;
		sortByArrivalTime();
		if(queues[0] instanceof PQueue){
			preSortSameArrivalTime();
		}				
	}
	
	private static void sortByArrivalTime() {
		for(int i = 0; i < processes.length; i++){
			for(int j = i; j < processes.length; j++){
				if(processes[i].getArrivalTime() > processes[j].getArrivalTime()){
					CPUBoundProcess temp = processes[i];
					processes[i] = processes[j];
					processes[j] = temp; 
				}
			}			
		}
		printContents(processes);
	}

	private static void preSortSameArrivalTime() {
		for(int i = 0; i < processes.length; i++){
			for(int j = i; j < processes.length; j++){
				if(processes[i].getArrivalTime() == processes[j].getArrivalTime() && processes[i].getPriority() > processes[j].getPriority()){
					//System.out.println("Swapping p" + processes[i].getId() + " and p" + processes[j].getId());
					CPUBoundProcess temp = processes[i];
					processes[i] = processes[j];
					processes[j] = temp; 
				}
			}			
		}
		printContents(processes);
	}

	private static void printContents(CPUBoundProcess[] processes2) {
		System.out.print("[");
		for(int i = 0; i < processes2.length; i++){
			System.out.print("p" + processes2[i].getId());
		}
		System.out.println("]");		
	}

	public void simulate(){
		running  = true;		
		clock.start();
	}
	
	public static void stop(){
		clock.interrupt();		
		clockTime = 0;
	}
	
	public void generateQueues(int algorithm, int quantum){
		System.out.println("Generating single level queue...");
		for(int i = 0; i < numOfQueues; i++){	
			if(algorithm == SchedulingAlgorithm.FCFS){
				queues[0] = new FCFSQueue(0);
				((FCFSQueue) queues[0]).setNumberOFProcesses(processes.length);
			}else if (algorithm == SchedulingAlgorithm.RR){
				queues[0] = new RRQueue(0, quantum);
				((RRQueue) queues[0]).setNumberOFProcesses(processes.length);
			}else if (algorithm == SchedulingAlgorithm.SJF){
				queues[0] = new SJFQueue(0);
				((SJFQueue) queues[0]).setNumberOFProcesses(processes.length);
			}else if (algorithm == SchedulingAlgorithm.NP_PRIO){
				queues[0] = new NonPQueue(0);
				((NonPQueue) queues[0]).setNumberOFProcesses(processes.length);
			}else if (algorithm == SchedulingAlgorithm.PRIO){
				queues[0] = new PQueue(0);
				((PQueue) queues[0]).setNumberOFProcesses(processes.length);
			}else if (algorithm == SchedulingAlgorithm.SRTF){
				queues[0] = new SRTFQueue(0);
				((SRTFQueue) queues[0]).setNumberOFProcesses(processes.length);
			}
			
		}							
	}	
	
	public void generateQueues(int[] algorithms, int[] quantums){
		System.out.println("Generating multilevel queues...");
		for(int i = 0; i < numOfQueues; i++){	
			if(algorithms[i] == SchedulingAlgorithm.FCFS){
				queues[i] = new FCFSQueue(i);
				((FCFSQueue) queues[i]).setNumberOFProcesses(processes.length);
				((FCFSQueue) queues[i]).startThread();
				//if(i == 0) ((FCFSQueue) queues[i]).startExecution();
			}else if (algorithms[i] == SchedulingAlgorithm.RR){
				queues[i] = new RRQueue(i, quantums[i]);
				((RRQueue) queues[i]).setNumberOFProcesses(processes.length);
				((RRQueue) queues[i]).startThread();
				if(i == 0) ((RRQueue) queues[i]).startExecution();
			}else if (algorithms[i] == SchedulingAlgorithm.SJF){
				queues[i] = new SJFQueue(i);
				((SJFQueue) queues[i]).setNumberOFProcesses(processes.length);
				((SJFQueue) queues[i]).startThread();
			}else if (algorithms[i] == SchedulingAlgorithm.NP_PRIO){
				queues[i] = new NonPQueue(i);
				((NonPQueue) queues[i]).setNumberOFProcesses(processes.length);
				((NonPQueue) queues[i]).startThread();
			}else if (algorithms[i] == SchedulingAlgorithm.PRIO){
				queues[i] = new PQueue(i);
				((PQueue) queues[i]).setNumberOFProcesses(processes.length);
				((PQueue) queues[i]).startThread();
			}else if (algorithms[i] == SchedulingAlgorithm.SRTF){
				queues[i] = new SRTFQueue(i);
				((SRTFQueue) queues[i]).setNumberOFProcesses(processes.length);
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
								
				for(int i = itr; i < processes.length; i++){								
					if(processes[i].getArrivalTime() == clockTime){						
						System.out.println("Clock time: " + clockTime + " insert p" + processes[i].getId());
						insertOnQueue(processes[i]);
						itr++;
					}else if(processes[i].getArrivalTime() > clockTime){						
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

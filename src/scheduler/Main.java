package scheduler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import constants.SchedulingAlgorithm;
import gui.GanttChart;
import process.CPUBoundProcess;
import process.IOBoundProcess;
import queues.FCFSQueue;
import queues.NonPQueue;
import queues.PQueue;
import queues.Queue;
import queues.RoundRobin;
import queues.SJFQueue;
import queues.SRTFQueue;

public class Main {
	public static Queue[] queues;
	public static int numOfQueues = 0;		
	public static ArrayList<CPUBoundProcess> processes = new ArrayList<CPUBoundProcess>();
	public static int lastArrivalTime;
	
	public void initProcesses(int numOfQueues, ArrayList<CPUBoundProcess> processes){
		Main.numOfQueues = numOfQueues;
		Main.queues = new Queue[numOfQueues];		
		Main.processes = processes;		
		initLastArrivalTime();
		sortByArrivalTime();
		if(queues[0] instanceof PQueue){
			prioritySortSameArrivalTime();
		}				
		preSortSameArrivalTimeByType();
	}
	
	/**
	 * IO-bound processes executes first over CPU-bound processes
	 * with the same arrival time
	 * 
	 * */
	private static void preSortSameArrivalTimeByType() {
		int psize = processes.size();
		for(int i = 0; i < psize; i++){
			for(int j = i; j < psize; j++){
				if(processes.get(i).getArrivalTime() == processes.get(j).getArrivalTime() && processes.get(j) instanceof IOBoundProcess && !(processes.get(i) instanceof IOBoundProcess)){
					CPUBoundProcess temp = processes.get(i);
					processes.set(i, processes.get(j));
					processes.set(j, temp);
				}
			}			
		}
		System.out.print("After by type sorting: ");
		printContents(processes);
	}

	/**
	 * Sorts processes by arrival time
	 * 
	 * */
	private static void sortByArrivalTime() {
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
		System.out.print("After arrival time sorting: ");
		printContents(processes);
	}
	
	/**
	 * Sorts the processes with same arrival time according to their 
	 * priorities
	 * */
	private static void prioritySortSameArrivalTime() {
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
		
		System.out.print("After priority sorting: ");
		printContents(processes);
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
	
	public void generateQueues(int[] algorithms, int[] quantums){
		System.out.println("[Main:] Generating multilevel queues...");
		System.out.println("[Main:] numOfQueues: " + numOfQueues);	
		
		for(int i = 0; i < numOfQueues; i++){			
			if(algorithms[i] == SchedulingAlgorithm.FCFS){
				queues[i] = new FCFSQueue(i);
			}else if (algorithms[i] == SchedulingAlgorithm.RR){
				queues[i] = new RoundRobin(i, quantums[i]);
			}else if (algorithms[i] == SchedulingAlgorithm.SJF){
				queues[i] = new SJFQueue(i);
			}else if (algorithms[i] == SchedulingAlgorithm.NP_PRIO){
				queues[i] = new NonPQueue(i);
			}else if (algorithms[i] == SchedulingAlgorithm.PRIO){
				queues[i] = new PQueue(i);
			}else if (algorithms[i] == SchedulingAlgorithm.SRTF){
				queues[i] = new SRTFQueue(i);
			}
		}	
		
		for(int i = 0; i < numOfQueues; i++) {
		
			if(i == numOfQueues-1) { 
				if(numOfQueues != 1) {
					System.out.println("[Main:] Level = " + i + " previous = " + queues[i-1]);				
					queues[i].setPrevQueue(queues[i-1]);
				}else {
					System.out.println("[Main:] Level = " + i + " previous = null");
					queues[i].setPrevQueue(null);
				}
				queues[i].setNextQueue(null);
				System.out.println("[Main:] Level = " + i + " next = null");
			}else if(i == 0) {
				System.out.println("[Main:] Level = " + i + " previous = null");
				System.out.println("[Main:] Level = " + i + " next = " + queues[i+1]);
				queues[i].setPrevQueue(null);
				queues[i].setNextQueue(queues[i+1]);
				
			}else {
				System.out.println("[Main:] Level = " + i + " previous = " + queues[i-1]);
				System.out.println("[Main:] Level = " + i + " next = " + queues[i+1]);
				queues[i].setPrevQueue(queues[i-1]);
				queues[i].setNextQueue(queues[i+1]);
			}			
		}
		
		queues[0].startExecution();
	}			
						
	public static int getMaxLevelOfQueues() {
		return numOfQueues-1;
	}

	public static CPUBoundProcess getNextProcess() {
		if(processes.size() == 0) return null;
		
		CPUBoundProcess nextProcess =  processes.remove(0);		
		return nextProcess;
	}

	public static int getNextArrivalTime() {
		if(processes.size() == 0) return -1;
		return processes.get(0).getArrivalTime();
	}

	private static void initLastArrivalTime() {
		lastArrivalTime = 0;
		for(int i = 0; i < processes.size(); i++) {
			if(processes.get(i).getArrivalTime() >= lastArrivalTime) {
				lastArrivalTime = processes.get(i).getArrivalTime();
			}
		}		
	}
	
	public static int getLastArrivalTime() {
		return lastArrivalTime;
	}
}

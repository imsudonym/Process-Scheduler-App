package queues;

import java.util.ArrayList;

import constants.QueueType;
import datastructure.PseudoArray;
import gui.GanttChart;
import process.CPUBoundProcess;
import process.IOBoundProcess;
import scheduler.Main;

public abstract class Queue {
	public static ArrayList<CPUBoundProcess> processList = new ArrayList<CPUBoundProcess>();
	protected PseudoArray array = new PseudoArray(1000);
	protected GanttChart ganttChart = new GanttChart();
	
	protected int totalBurstTime = 0;
	protected static int prevTime = 0;
	public static int prevTimeQuantum;
	public static int clockTime = 0;
	
	protected int counter = 1;
	protected int queueStartTime = -1;
	protected int timeNow;
	protected int clockTimeEnd;
	protected boolean incFlag = false;	
	
	protected Queue prevQueue;
	protected Queue nextQueue;
	
	protected static CPUBoundProcess currProcess;
	protected static CPUBoundProcess prevProcess;
	protected boolean running = false;
	public static boolean threadStopped = false;
	protected boolean queuePreempted = false;
	
	protected int quantum = 0;
	protected int level = -1;
	protected long timeStart;
	protected long timeEnd;
	
	public byte allProcessesDone = 1;
	public byte prevQueueDone = 1;
	
	public int queueType;
	
	public void startThread() {
		run();
	}
		
	public void stopThread() {
		if(threadStopped) return;
		if(peekHead() == null && getSize() == 0 && isHigherQueueDone()) {
			if(level == Main.getMaxLevelOfQueues() || isLowerQueuesDone()) {											
				threadStopped = true;
				//System.out.println("[Queue] Level = " + level + " stopping simulation");
				clockTimeEnd = clockTime;
				simulationDone();
			}else {				
				startLowerLevelQueues();
			}
		}
	}
	
	private boolean isLowerQueuesDone() {
		if(nextQueue == null) return true;		
		Queue currNextQueue = nextQueue;
		
		while(true) {
			if(currNextQueue == null) break;		
			if(currNextQueue.getSize() > 0) return false;
			currNextQueue = currNextQueue.nextQueue;
		}
		return true;
	}

	public abstract void run();
	
	public Queue(int level) {
		this.level = level;
	}
	
	public void enqueue(CPUBoundProcess newProcess, int qType){		
		array.add(newProcess);		
		
		/*System.out.println("[Queue] Level: " + level + " P" + newProcess.getId() + " enqueued!");
		System.out.print("[Queue] Level: " + level + " ");
		array.printContents();*/
		
		if(qType == QueueType.SJF) sortSJF();
		if(qType == QueueType.SRTF) sortSRTF(); 
		if(qType == QueueType.PQ) sortPQ();
		if(qType == QueueType.NPQ) sortNPQ();		
		if(qType == QueueType.RR) {			

			if(nextQueue != null || prevQueue != null) {			
				totalBurstTime += quantum;
			}else {
				if(!processList.contains(newProcess)) {
					totalBurstTime += newProcess.getBurstNeeded();
					processList.add(newProcess);
				}				
			}
			stopLowerLevelQueues();
		}else {
			totalBurstTime += newProcess.getBurstTime();
		}

		if(!processList.contains(newProcess)) {			
			processList.add(newProcess);
		}
	}

	protected void sortByBound() {
		array.givePriorityToIoBounds();
	}

	private void sortNPQ() {
		array.sortNPQ();
	}
	
	private void sortNPQFromFirst() {
		array.sortNPQFromFirst();
	}
	
	private void sortPQ() {
		array.sortPQ();
	}

	private void sortSRTF() {
		array.sortSRTF();
	}

	public void sortSJF(){
		array.sortSJF();
	}
	
	public void sortSJFFromFirst(){
		array.sortSJFFromFirst();
	}
	
	protected CPUBoundProcess dequeue(){					
		CPUBoundProcess process = array.remove();
		//array.printContents();
		int burstExecuted = process.getEndTime()-process.getStartTime();			
		process.setPrevBurstPreempted(process.getBurstTime());
		displayExecutingInUI(burstExecuted, process.getEndTime(), process.getId());
		return process;
	}
	
	protected CPUBoundProcess dequeueSecondProcess() {
		CPUBoundProcess process = array.removeSecond();
		if(process != null) {
			int burstExecuted = process.getEndTime()-process.getStartTime();
			process.setPrevBurstPreempted(process.getBurstTime());
			displayExecutingInUI(burstExecuted, process.getEndTime(), process.getId());
		}		
		return process;
	}
	
	protected void retain(int qType) {
		enqueue(dequeue(), qType);
	}
	
	protected void retainSecondProcess(int qType) {
		CPUBoundProcess p = dequeueSecondProcess();
		//System.out.println("[Q] Level: " + level + " retaining P" + p.getId());
		enqueue(p, qType);		
	}
	
	protected void insertToReadyQueue(IOBoundProcess process) {
		Main.enqueue(process);
	}
	
	protected void preemptCurrProcess(int timeNow) {
		currProcess.setPreempted();
		currProcess.setTimePreempted(timeNow);
		currProcess.setEndTime(timeNow);
		currProcess.preemptedFlag = true;
		prevProcess = currProcess;
	}
	
	public void startExecution() {		
		if(prevQueue != null && !isHigherQueueDone()) return;

		if(clockTime <= Main.getFirstArrivalTime()) {
			for(int i = clockTime; i <= Main.getNextArrivalTime(); i++) {
				if(Main.getNextArrivalTime() == i){
					while(Main.getNextArrivalTime() == i) {
						Main.queues[0].getNextProcess();						
					}
					
					//System.out.println("[Q] starting queue level: " + level);					
					Main.queues[0].startThread();
				}
				clockTime++;
			}
		}else {
			
			//System.out.println("[Queue] starting queue level: " + level);			
			startThread();
		}
	}
	
	public void stopExecution() {
		stopLowerLevelQueues();			
		if(currProcess != null && hasExecuted(currProcess)) { 				
			currProcess.setPrevBurstPreempted(currProcess.getBurstTime());
		}
	}
	
	protected void startLowerLevelQueues() {
		if(nextQueue == null) return;
		
		//System.out.println("[Queue] Level " + level + " starting lower level queues");
		
		if(nextQueue.queueType == QueueType.SJF) {
			nextQueue.sortSJFFromFirst();
		}else if(nextQueue.queueType == QueueType.NPQ) {
			nextQueue.sortNPQFromFirst();
		}
		
		nextQueue.startExecution();
	}
	
	protected void stopLowerLevelQueues() {
		//System.out.println("[Queue] Level: " + level + " stopping lower level queues");
		if(nextQueue == null) return;			
		Queue currNextQueue = nextQueue;
		
		while(true) {
			if(currNextQueue == null) break;
			currNextQueue.counter = 1;
			currNextQueue = currNextQueue.nextQueue;
		}		
	}
	
	protected void displayExecutingInUI(int burstExecuted, int timeNow, int id) {
		GanttChart.addExecutingProcess((byte)level, id, burstExecuted, timeNow);
	}
		
	protected void displayArrivedInUI(int processId, int arrivalTime, int burstTime, int priority) {		
		GanttChart.addNewArrivedProcess(processId, arrivalTime, burstTime, priority);
	}
	
	protected boolean hasExecuted(CPUBoundProcess currProcess) {
		if(currProcess.getPrevBurstPreempted()-currProcess.getBurstTime() > 0)
			return true;
		return false;
	}
	
	protected boolean isHigherQueueDone() {
		if(prevQueue == null) return true;		
		Queue currPrevQueue = prevQueue;
		
		while(true) {
			if(currPrevQueue == null) break;		
			if(currPrevQueue.getSize() > 0) return false;
			currPrevQueue = currPrevQueue.prevQueue;
		}
		return true;
	}

	protected void shiftIoBoundsToFront() {
		array.givePriorityToIoBounds();
	}
	
	public CPUBoundProcess peekHead(){
		if(array.getHead() == null) {
			return null;
		}else {
			return array.getHead().getValue();
		}
	}
	
	public int getSize(){
		return array.getSize();
	}

	public void setPrevQueue(Queue prevQueue) {
		this.prevQueue = prevQueue;
		if(prevQueue == null) {
			prevQueueDone = 1;
		}	
	}
	
	public void setNextQueue(Queue nextQueue){
		this.nextQueue = nextQueue;
	}
	
	public Object getNextQueue(){
		return nextQueue;
	}
	
	public Object getPrevQueue() {
		return prevQueue;
	}
	
	protected void simulationDone(){
		clockTime = -1;
		ArrayList<CPUBoundProcess> temp = processList;
		int count =  temp.size();
		double avgResponse = 0;
		double avgWait = 0;
		double avgTurnaround = 0;
		for(int i = 0; i < count; i++) {
			if(temp.get(i) == null) continue;
			temp.get(i).setWaitTimePreemptive();
			
			/*System.out.print("[p" + temp.get(i).getId() + "]: ");
			System.out.println("timesPreempted = " + temp.get(i).timePreempted.size() + " timesResumed = " + temp.get(i).timeResumed.size() 
					+ " waitTime: " + temp.get(i).getWaitTime() + " responseTime: " + temp.get(i).getResponseTime() + " turnAround: " + temp.get(i).getTurnaroundTime());*/
			addTimesInformation(temp.get(i).getId(), temp.get(i).getResponseTime(), temp.get(i).getWaitTime(), temp.get(i).getTurnaroundTime());			
			avgResponse += temp.get(i).getResponseTime();
			avgWait += temp.get(i).getWaitTime();
			avgTurnaround += temp.get(i).getTurnaroundTime();			
		}
		
		avgResponse = avgResponse/count;
		avgWait = avgWait/count;
		avgTurnaround = avgTurnaround/count;
		
		System.out.println("[Queue] avgResponse = " + avgResponse + " avgWait = " + avgWait + " avgTurnaround = " + avgTurnaround);
		addAverageTime(avgResponse, avgWait, avgTurnaround);
		
		ganttChart.simulationDone(this);
	}
	
	private void addAverageTime(double avgResponse, double avgWait, double avgTurnaround) {
		GanttChart.addTimeAverages(avgResponse, avgWait, avgTurnaround);
	}
	
	private void addTimesInformation(int processId, long responseTime, long waitTime, long turnaroundTime) {
		GanttChart.addTimesInformation(processId, responseTime, waitTime, turnaroundTime);
	}
	
	protected int getNextArrivalTime() {
		int nextArrivalTime = Main.getNextArrivalTime();
		return nextArrivalTime;
	}

	protected void getNextProcess() {
		CPUBoundProcess nextProcess = Main.getNextProcess();
		//System.out.println("[Queue:] Inserting process P" + nextProcess.getId() + " burst: " + nextProcess.getBurstTime());
		enqueue(nextProcess, this.queueType);
		displayArrivedInUI(nextProcess.getId(), nextProcess.getArrivalTime(), nextProcess.getBurstNeeded(), nextProcess.getPriority());
	}

	/**
	 * Promotes the preempted process to an
	 * immediate higher priority queue.
	 * */
	protected void determineToPromote() {
		/**
		 * TODO: Determine if a process needs to be promoted. 
		 * If yes, then promote
		 * */
		
		if(prevQueue == null ||
			!(prevQueue instanceof RoundRobin) ||
				currProcess == null ||
					prevProcess == null ||
						currProcess.getBurstTime() <= 0 ||					
							prevProcess.getId() == currProcess.getId()) {
			return;
		}
		
		int burstLeft = currProcess.getBurstTime();
		if(burstLeft > 0) { 	
			/** 
			 * Promotion for SRTF only occurs when it has a higher queue
			 * that is Round Robin
			 * */
			int burstPreempted = prevProcess.getBurstTime();
			prevProcess.setPrevBurstPreempted(burstPreempted);							
			prevQueue.enqueue(dequeue(), this.queueType);
		}
	}
}

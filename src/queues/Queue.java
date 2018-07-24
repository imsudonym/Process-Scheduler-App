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
	protected static int prevTimeQuantum;
	protected static int clockTime = 0;
	protected int queueStartTime = -1;
	protected int timeNow;
	protected int clockTimeEnd;
	
	protected Queue prevQueue;
	protected Queue nextQueue;
	
	protected static CPUBoundProcess currProcess;
	protected static CPUBoundProcess prevProcess;
	protected boolean running = false;
	public static boolean threadStopped = false;
	
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
			if(Main.processes.size() == 0) {
				if(level == Main.getMaxLevelOfQueues()) {						
					System.out.println("[Queue:] Level = " + level + " stopping simulation...");
					threadStopped = true;
					clockTimeEnd = clockTime;
					simulationDone();					
				}else {							
					System.out.println("[Queue:] starting lower level queues");
					startLowerLevelQueues();													
				}
			}
		}
	}
	
	public abstract void run();
	
	public Queue(int level) {
		this.level = level;
	}
	
	public void enqueue(CPUBoundProcess newProcess, int qType){
		allProcessesDone = 0;
		array.add(newProcess);
		
		if(qType == QueueType.SJF) sortSJF();
		if(qType == QueueType.SRTF) sortSRTF(); 
		if(qType == QueueType.PQ) sortPQ();
		if(qType == QueueType.NPQ) sortNPQ();
		
		if(!processList.contains(newProcess)) {
			totalBurstTime += newProcess.getBurstNeeded();
			processList.add(newProcess);
		}		
		
		startExecution();
		stopLowerLevelQueues();
	}
	
	private void sortNPQ() {
		array.sortNPQ();
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
	
	protected CPUBoundProcess dequeue(){					
		CPUBoundProcess prc = array.remove();											
		return prc;
	}
	
	protected void retain(int qType) {
		enqueue(dequeue(), qType);
	}
	
	protected void insertToReadyQueue(IOBoundProcess process) {
		Main.enqueue(process);
	}
	
	protected void preemptCurrProcess(int timeNow) {
		System.out.println("[Queue:] Preempting current process P" + currProcess.getId() + " [timeNow:" + timeNow + "]");
		currProcess.setPreempted();
		currProcess.setTimePreempted(timeNow);		
		currProcess.setEndTime(timeNow);
		currProcess.preemptedFlag = true;
		prevProcess = currProcess;		
	}
	
	public void startExecution() {		
		if(prevQueue != null && !isHigherQueueDone()) return;
		startThread();
	}
	
	public void stopExecution() {
		stopLowerLevelQueues();				
		if(currProcess != null && hasExecuted(currProcess)) { 	
			int burstExecuted = (int) (currProcess.getEndTime()-currProcess.getStartTime());			
			currProcess.setPrevBurstPreempted(currProcess.getBurstTime());
			displayInUI(burstExecuted, (int)timeNow);
		}
	}
	
	protected void startLowerLevelQueues() {
		if(nextQueue == null) return;			
		nextQueue.startExecution();
	}
	
	protected void stopLowerLevelQueues() {
		if(nextQueue == null) return;		
		nextQueue.stopExecution();			
	}
	
	protected void displayInUI(int burstExecuted, int timeNow) {
		//GanttChart.addExecutingProcess((byte)level, currProcess.getId(), burstExecuted, (int)timeNow);
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
			System.out.println("[Queue:] id:" + i);
			temp.get(i).setWaitTimePreemptive();
			
			System.out.print("[p" + temp.get(i).getId() + "]: ");
			System.out.println("timesPreempted = " + temp.get(i).timePreempted.size() + " timesResumed = " + temp.get(i).timeResumed.size() 
					+ " waitTime: " + temp.get(i).getWaitTime() + " responseTime: " + temp.get(i).getResponseTime() + " turnAround: " + temp.get(i).getTurnaroundTime());
			
			if(!(temp.get(i) instanceof IOBoundProcess)) {
				avgResponse += temp.get(i).getResponseTime();
				avgWait += temp.get(i).getWaitTime();
				avgTurnaround += temp.get(i).getTurnaroundTime();
			}			
			if((temp.get(i) instanceof IOBoundProcess)) continue;
		}
		
		//count--;	// Bakit tayo magma-minus ng isa?
		
		avgResponse = avgResponse/count;
		avgWait = avgWait/count;
		avgTurnaround = avgTurnaround/count;
		
		System.out.println("avgResponse = " + avgResponse + " avgWait = " + avgWait + " avgTurnaround = " + avgTurnaround);
		
		ganttChart.simulationDone(this);
	}
	
	protected int getNextArrivalTime() {
		int nextArrivalTime = Main.getNextArrivalTime();
		//System.out.println("[Roundrobin:] nextArrivalTime: " + nextArrivalTime + " timeNow: " + timeNow);
		return nextArrivalTime;
	}

	protected void getNextProcess() {
		CPUBoundProcess nextProcess = Main.getNextProcess();
		System.out.println("[Roundrobin:] Inserting process P" + nextProcess.getId());
		enqueue(nextProcess, this.queueType);
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

package queues;

import java.util.ArrayList;

import constants.QueueType;
import datastructure.PseudoArray;
import gui.GanttChart;
import process.CPUBoundProcess;
import process.IOBoundProcess;
import scheduler.Scheduler;

public abstract class Queue {
	public static ArrayList<CPUBoundProcess> processList = new ArrayList<CPUBoundProcess>();
	protected PseudoArray array = new PseudoArray(1000);
	
	protected static long prevTime = 0;
	protected static long prevTimeQuantum;
	
	protected Queue prevQueue;
	protected Queue nextQueue;
	
	protected static CPUBoundProcess currProcess;
	protected static CPUBoundProcess prevProcess;
	protected boolean running = false;
	
	protected int level = -1;
	protected long timeStart;
	protected long timeEnd;
	
	public byte allProcessesDone = 1;
	public byte prevQueueDone = 1;
	
	public int queueType;
	
	public abstract void startThread(); 
	public abstract void stopThread();
	
	public Queue(int level) {
		this.level = level;
	}
	
	public void enqueue(CPUBoundProcess newProcess, int qType){
		allProcessesDone = 0;
		array.add(newProcess);
		
		if(qType == QueueType.SJF || qType == QueueType.SRTF) {
			sortSJF();
		}
		
		if(!processList.contains(newProcess)) {
			processList.add(newProcess);
		}		
		
//		System.out.println("level 1: determining if to preempt... ");
		determineIfToPreemptExec(newProcess);		
		startExecution();
		stopLowerLevelQueues();
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
		Scheduler.enqueue(process);
	}
	
	protected void preemptCurrProcess(int timeNow) {
		System.out.println("[Queue:] Preempting current process P" + currProcess.getId() + " [timeNow:" + timeNow + "]");
		currProcess.setPreempted();
		currProcess.setTimePreempted(timeNow);		
		currProcess.setEndTime(timeNow);
		currProcess.preemptedFlag = true;
		prevProcess = currProcess;		
	}
	
	/** 
	 * Preempts a current CPUBound process that was executing if the 
	 * new process is IO-bound.
	 * 
	 * */
	protected void determineIfToPreemptExec(CPUBoundProcess newProcess) {
		if(newProcess instanceof IOBoundProcess) {
			System.out.println("[Queue:] Instance of IOBound");
			long timeNow = Scheduler.clockTime;
			
			System.out.println("[Queue:] newProcess startTime:" + timeNow);
			newProcess.setStartTime(timeNow);
			newProcess.setFirstStartTime(timeNow);
			newProcess.setResponseTime();
			
			if(currProcess != null  && currProcess instanceof CPUBoundProcess) {				
				if(hasExecuted(currProcess)) {			
//					System.out.println("| !! p" + currProcess.getId() + " was executing when p" + newProcess.getId() + " burstLeft = " + currProcess.getBurstTime());				
					prevTimeQuantum = timeNow; 
										
					preemptCurrProcess((int)timeNow);							
					int burstExecuted = (int) (currProcess.getEndTime()-currProcess.getStartTime());
//					System.out.println("| burstExecuted = " + burstExecuted);			
					currProcess.setPrevBurstPreempted(currProcess.getBurstTime());			
					displayInUI(burstExecuted, (int)timeNow);
					
				}else if(!hasExecuted(currProcess)) {										
					currProcess.setStartTime(-1);
					if(currProcess.getBurstTime() == currProcess.getBurstNeeded()) {
//						System.out.println("Resetting first start time p" + currProcess.getId());
						currProcess.setFirstStartTime(-1);
						currProcess.setResponseTime();
					}					
				}
			}
			
			if(getSize() > 1) {
				shiftIoBoundsToFront();
			}
		}
	}
	
	public void startExecution() {
//		System.out.println("Level = " + level + " starting...");
		
		if(prevQueue != null && !isHigherQueueDone()) {
			prevQueueDone = 0;
			return;
		}
		
		//if(getSize() > 0) {
			System.out.println("[Queue:] size = " + getSize());
			restart();
		//}
	}
	
	public void stopExecution() {		
		prevQueueDone = 0;
		
		stopLowerLevelQueues();
				
		if(currProcess != null && hasExecuted(currProcess)) {
			long timeNow = Scheduler.clockTime; 	
			prevTimeQuantum = timeNow;
			
			preemptCurrProcess((int)timeNow);		
			
			int burstExecuted = (int) (currProcess.getEndTime()-currProcess.getStartTime());			
			currProcess.setPrevBurstPreempted(currProcess.getBurstTime());
			displayInUI(burstExecuted, (int)timeNow);

		}
	}
	
	protected void startLowerLevelQueues() {
		if(nextQueue != null) {			
			System.out.println("[Queue:] Level " + level + " Starting next queue.");
			nextQueue.startExecution();
		}
	}
	
	protected void stopLowerLevelQueues() {
		if(nextQueue != null) {
			if(nextQueue instanceof RoundRobin) {
				if(((RoundRobin)(nextQueue)).getSize() > 0) {
					((RoundRobin)(nextQueue)).stopExecution();
				}
			}else if(nextQueue instanceof SRTFQueue) {
				((SRTFQueue)(nextQueue)).stopExecution();
			}else if(nextQueue instanceof PQueue) {
				((PQueue)(nextQueue)).stopExecution();
			}else if(nextQueue instanceof FCFSQueue) {
				((FCFSQueue)(nextQueue)).stopExecution();
			}else if(nextQueue instanceof SJFQueue) {
				((SJFQueue)(nextQueue)).stopExecution();
			}else if(nextQueue instanceof NonPQueue) {
				((NonPQueue)(nextQueue)).stopExecution();
			}
		}
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
		//System.out.println("level " + level + " prevQueue.getSize() = " + prevQueue.getSize());
		if(prevQueue == null) return true;
		
		Queue currPrevQueue = prevQueue;
		
		while(true) {
			if(currPrevQueue == null)
				break;
			
			if(currPrevQueue.getSize() > 0) {
				return false;
			}
			
			currPrevQueue = currPrevQueue.prevQueue;
		}
		/*if(prevQueue.getSize() > 0) {		
			prevQueueDone = 0;
			System.out.println("level " + level + " prevQueue not done.");
			return false;
		}		*/
		
		//System.out.println("level " + level + " prevQueue done.");
		//System.out.println("level = " + level + " size: " + getSize() + "peekHead() = " + peekHead());
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
		
	public void restart() {
		prevTime = Scheduler.clockTime;
		running = true;
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
		ArrayList<CPUBoundProcess> temp = processList;
		int count =  temp.size();
		
		double avgResponse = 0;
		double avgWait = 0;
		double avgTurnaround = 0;
		
		System.out.println("[Queue:] count:" + count);
		for(int i = 0; i < count; i++) {
			System.out.println("[Queue:] i:" + i);
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
			
			int c = temp.get(i).getTimesPreempted();			
			for(int j = 0; j < c; j++) {
				
				System.out.print(temp.get(i).timePreempted.get(j) + "-");
				System.out.print(temp.get(i).timeResumed.get(j) + "|");
				
			}
			System.out.println();
		}
		
		count--;
		
		avgResponse = avgResponse/count;
		avgWait = avgWait/count;
		avgTurnaround = avgTurnaround/count;
		
		System.out.println("avgResponse = " + avgResponse + " avgWait = " + avgWait + " avgTurnaround = " + avgTurnaround);
		
		GanttChart.simulationDone(this);
		Scheduler.stop();
	}
}

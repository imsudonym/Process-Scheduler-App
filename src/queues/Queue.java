package queues;

import java.util.ArrayList;

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
	
	protected CPUBoundProcess currProcess;
	protected CPUBoundProcess prevProcess;
	protected boolean running = false;
	
	protected int level = -1;
	protected long timeStart;
	protected long timeEnd;
	
	public byte allProcessesDone = 1;
	public byte prevQueueDone = 1;
	
	public abstract void startThread(); 
	public abstract void stopThread();
	
	public Queue(int level) {
		this.level = level;
	}
	
	public void enqueue(CPUBoundProcess newProcess){
		allProcessesDone = 0;
		array.add(newProcess);
		
		if(!processList.contains(newProcess)) {
			processList.add(newProcess);
		}		
		if(prevQueue != null && !isHigherQueueDone()) {
			return;
		}
		
		determineIfToPreemptExec(newProcess);		
		startExecution();
		stopLowerLevelQueues();
	}
	
	protected CPUBoundProcess dequeue(){					
		CPUBoundProcess prc = array.remove();											
		return prc;
	}
	
	protected void retain() {
		enqueue(dequeue());
	}
	
	protected void insertToReadyQueue(IOBoundProcess process) {
		System.out.println("Insert p" + process.getId() + " with new arrival time = " + process.getArrivalTime());
		Scheduler.enqueue(process);
	}
	
	protected void preemptCurrProcess(int timeNow) {
		currProcess.setPreempted();
		currProcess.setTimePreempted(timeNow);
		currProcess.setEndTime(timeNow);
		currProcess.preemptedFlag = true;
		prevProcess = currProcess;		
	}
	
	/* 
	 * Preempts a current CPUBound process that was executing if the 
	 * new process is IO-bound.
	 * 
	 * */
	protected void determineIfToPreemptExec(CPUBoundProcess newProcess) {
		if(newProcess instanceof IOBoundProcess) {
			long timeNow = Scheduler.clockTime;
			
			newProcess.setStartTime(timeNow);
			newProcess.setResponseTime();
			
			if(currProcess != null  && currProcess instanceof CPUBoundProcess) {				
				if(hasExecuted(currProcess)) {			
					System.out.println("| !! p" + currProcess.getId() + " was executing when p" + newProcess.getId() + " burstLeft = " + currProcess.getBurstTime());				
					prevTimeQuantum = timeNow; 
										
					preemptCurrProcess((int)timeNow);							
					int burstExecuted = (int) (currProcess.getEndTime()-currProcess.getStartTime());
					System.out.println("| burstExecuted = " + burstExecuted);			
					currProcess.setPrevBurstPreempted(currProcess.getBurstTime());			
					displayInUI(burstExecuted, (int)timeNow);
					
				}else if(!hasExecuted(currProcess)) {										
					currProcess.setStartTime(-1);
					if(currProcess.getBurstTime() == currProcess.getBurstNeeded()) {
						System.out.println("Resetting first start time p" + currProcess.getId());
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
		if(prevQueue != null && !isHigherQueueDone()) return;
		if(getSize() > 0) {
			restart();
			prevQueueDone = 1;
		}
	}
	
	public void stopExecution() {
		System.out.println("	level = " + level + " stopping execution...");
		prevQueueDone = 0;
		
		stopLowerLevelQueues();
				
		if(currProcess != null && hasExecuted(currProcess)) {
			long timeNow = Scheduler.clockTime; 	
			prevTimeQuantum = timeNow;
			
			preemptCurrProcess((int)timeNow);		
			
			int burstExecuted = (int) (currProcess.getEndTime()-currProcess.getStartTime());
			
			System.out.println("| burstExecuted = " + burstExecuted);			
			currProcess.setPrevBurstPreempted(currProcess.getBurstTime());
			displayInUI(burstExecuted, (int)timeNow);

		}
	}
	
	protected void startLowerLevelQueues() {
		if(nextQueue != null) {
			if(nextQueue instanceof RRQueue) {
				((RRQueue)(nextQueue)).startExecution();
			}else if (nextQueue instanceof SRTFQueue) {
				((SRTFQueue)(nextQueue)).startExecution();								
			}else if (nextQueue instanceof FCFSQueue) {
				((FCFSQueue)(nextQueue)).startExecution();								
			}else if (nextQueue instanceof PQueue) {
				((PQueue)(nextQueue)).startExecution();								
			}else if (nextQueue instanceof SJFQueue) {
				((SJFQueue)(nextQueue)).startExecution();								
			}else if (nextQueue instanceof NonPQueue) {
				((NonPQueue)(nextQueue)).startExecution();								
			}
		}
	}
	
	protected void stopLowerLevelQueues() {
		if(nextQueue != null) {
			if(nextQueue instanceof RRQueue) {
				if(((RRQueue)(nextQueue)).getSize() > 0) {
					((RRQueue)(nextQueue)).stopExecution();
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
		GanttChart.addExecutingProcess((byte)level, currProcess.getId(), burstExecuted, (int)timeNow);
	}
	
	protected boolean hasExecuted(CPUBoundProcess currProcess) {
		if(currProcess.getPrevBurstPreempted()-currProcess.getBurstTime() > 0)
			return true;
		return false;
	}
	
	protected boolean isHigherQueueDone() {
		int queueSize = 0;
		
		if(prevQueue instanceof RRQueue) {
			queueSize = ((RRQueue)(prevQueue)).getSize();		
		}else if(prevQueue instanceof FCFSQueue) {
			queueSize = ((FCFSQueue)(prevQueue)).getSize();
		}else if(prevQueue instanceof SJFQueue) {
			queueSize = ((SJFQueue)(prevQueue)).getSize();
		}else if(prevQueue instanceof SRTFQueue) {
			queueSize = ((SRTFQueue)(prevQueue)).getSize();
		}else if(prevQueue instanceof NonPQueue) {
			queueSize = ((NonPQueue)(prevQueue)).getSize();
		}else if(prevQueue instanceof PQueue) {
			queueSize = ((PQueue)(prevQueue)).getSize();
		}
		
		if(queueSize > 0) {		
			prevQueueDone = 0;
			return false;
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
		
	public void restart() {
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
		
		for(int i = 0; i < count; i++) {
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
	}
}

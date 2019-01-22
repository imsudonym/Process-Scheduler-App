package queues;
import constants.QueueType;
import process.CPUBoundProcess;
import process.IOBoundProcess;
import scheduler.Main;

public class RoundRobin extends PreemptiveQueue{	
	public boolean executing = false;
	private boolean quantumTimeDone;
	
	public RoundRobin(int level, int quantum){
		super(level);
		this.quantum = quantum;		
		this.queueType = QueueType.RR;
	}

	public void reenqueue(CPUBoundProcess newProcess){		
		array.add(newProcess);
		allProcessesDone = 0;
	}	
					
	public void run(){		
		clockTime = prevTimeQuantum;
		queueStartTime = clockTime;
		
		while(clockTime != -1 && getNextArrivalTime() == clockTime) {
			getNextProcess();
		}
		
		for(int ctr = 1; ctr <= totalBurstTime; ctr++){			
			if((currProcess = peekHead()) != null){				
				if(isLowerLevelQueue()) { 					
					
					// Current process starts/resumes executing now.
					currProcess.setStartTime(prevTimeQuantum);
					if(currProcess.preemptedFlag) {						
						currProcess.setTimeResumed(prevTimeQuantum);
						currProcess.preemptedFlag = false;
					}
					
					// If a preemption has occurred by an IOBound process.
					if(isRecentlyPreemptedByIOJob()) {						
						
						// Flag the previous process as preempted and set necessary values
						setPrevProcessPreempted();
						
						if(!quantumTimeDone) {					
							if(nextQueue == null) {
								// Retains the preempted process in this queue.
								retainSecondProcess(QueueType.RR);
							}else {
								// Demotes the preempted process in the next queue.
								demoteSecondProcess(dequeueSecondProcess());
							}
						}
						
						counter = 1;
					}
					
				}else{
					
					// Determine if a preemption has occurred by an I/O-bound process
					if(isRecentlyPreemptedByIOJob()) {						
						System.out.println("[RR] lvl" + level +
								"Previous process P" + prevProcess.getId() + 
									" was preempted by P" + currProcess.getId());
						
						// Flag previous process as preempted
						setPrevProcessPreempted();
						
						int burstExecuted = prevProcess.getEndTime()-prevProcess.getStartTime();
						if(!quantumTimeDone) {
							if (burstExecuted == quantum){
								if(nextQueue != null) {
									demoteSecondProcess(dequeueSecondProcess());
								}else {
									retainSecondProcess(QueueType.RR);
								}
							}else {
								retainSecondProcess(QueueType.RR);								
							}
						}
						counter = 1;
						
					// If an IO job is followed by another IO job...
					}else if(isSequentialIOJobs()){											
																				
						prevTimeQuantum = timeNow;		
						//int burstExecuted = quantum - 1;										
					}
					
					//	Set the current process' response time. (Only if it is executing for the first time)
					if(currProcess.getResponseTime() < 0) {
						System.out.println("process: p" + currProcess.getId());
						if(currProcess.getArrivalTime() <= prevTimeQuantum) {
							/***
							 * Sets the 'start time' of the current process to the last time 
							 * a process has reached if its arrival time happened before or at 
							 * prevTimeQuantum. 
							 * ***/
							currProcess.setStartTime(prevTimeQuantum);
							currProcess.setFirstStartTime(prevTimeQuantum);
							
						}else {
							
							currProcess.setStartTime(currProcess.getArrivalTime());
							currProcess.setFirstStartTime(currProcess.getArrivalTime());							
							
							if(currProcess.getArrivalTime() == (queueStartTime + ctr)) {
								currProcess.setBurstTime(currProcess.getBurstTime());
								prevTimeQuantum = timeNow;
								continue;
							}
							while((queueStartTime + ctr) <= currProcess.getArrivalTime()) {
								ctr++;
							}
						}
						
						currProcess.setResponseTime();						
						counter = 1;							
					}
					
					// Flag current process as resumed if previously preempted
					if(currProcess.preemptedFlag){
						if(currProcess.getArrivalTime() <= prevTimeQuantum) {
							currProcess.setStartTime(prevTimeQuantum);
							currProcess.setTimeResumed(prevTimeQuantum);
						}else{
							currProcess.setStartTime(queueStartTime + ctr);
							currProcess.setTimeResumed(queueStartTime + ctr);
						}
						currProcess.preemptedFlag = false;
					}
				}
				prevProcess = currProcess;
								
				int burstLeft = currProcess.getBurstTime() - 1;
				if(burstLeft >= 0) {
					currProcess.setBurstTime(burstLeft);
					prevTimeQuantum = clockTime = timeNow = queueStartTime + ctr;		
				}
				
				System.out.println("[Roundrobin] lvl" + level + 
						" ctr = " + ctr + 
						" tbt = " + totalBurstTime +						
						" executing P" + 
						currProcess.getId() + 
						" startTime = " + 
						currProcess.getStartTime() +
						" burstLeft = " +
						burstLeft +
						" timeNow = " + timeNow +
						" counter = " + counter +
						" clockTime = " + clockTime);									
				
				if(burstLeft <= 0){
					if(currProcess != null) {
						currProcess.setEndTime(timeNow);
						
						dequeue();
						
						currProcess.preemptedFlag = false;
						prevProcess = currProcess;						
					}
					prevTimeQuantum = timeNow;
					counter = 0;
					
				}else if (counter == quantum-1) {
					if(currProcess instanceof IOBoundProcess) {												
						currProcess.setEndTime(timeNow);
						currProcess.setArrivalTime(timeNow+((IOBoundProcess) currProcess).getIoSpeed());
												
						prevTimeQuantum = timeNow;
						Main.enqueue((IOBoundProcess) dequeue());						
						
						counter = 0;
						quantumTimeDone = true;
					}else {
						quantumTimeDone = false;
					}
					
				}else if(counter == quantum){
					prevTimeQuantum = timeNow;
					
					if(currProcess != null) {
						currProcess.setEndTime(timeNow);
						
						if(burstLeft > 0){
							preemptCurrProcess(timeNow);
							currProcess.setPrevBurstPreempted(currProcess.getBurstTime());										
													
							if(nextQueue == null) {
								System.out.println("[RR] Level: " + level + "retaining P" + currProcess.getId());
								retain(QueueType.RR);
							}else {
								System.out.println("[RR] Level: " + level + " demoting P" + currProcess.getId());
								demote(dequeue());
							}											
						}				
					}
					
					counter = 0;
					quantumTimeDone = true;
					
				}else {
					prevTimeQuantum = timeNow;
					quantumTimeDone = false;					
				}
								
				sortByBound();

				/*System.out.println("[RR] lvl" + level + " clockTime: " + clockTime + " getNextArrivalTime(): " + getNextArrivalTime() 
				+ " ctr: " + ctr + " tbt: " + totalBurstTime);*/
				
				while(clockTime != -1 && getNextArrivalTime() == clockTime) {
					getNextProcessForTopQueue();
				}
				
				clockTime++;
				counter++;
				
			}
			
			if(clockTime < getNextArrivalTime()) {
				clockTime++;				
			}
		}		
		stopThread();			
	}	

/*	private void getNextProcessForTopQueue() {
		System.out.println("Getting next process for top queue...");
		if(prevQueue != null && prevQueue instanceof RoundRobin) {
			while(clockTime != -1 && getNextArrivalTime() == clockTime) {
				if(currProcess != null) {
					currProcess.setPreempted();
					currProcess.setTimePreempted(timeNow);
					currProcess.setEndTime(timeNow);
					currProcess.preemptedFlag = true;
					
					if(hasExecuted(currProcess)) {
						prevTimeQuantum = timeNow;
						int burstExecuted = currProcess.getEndTime()-currProcess.getLastTimeResumed();
						displayExecutingInUI(burstExecuted, currProcess.getEndTime(), currProcess.getId());
					}
					currProcess = null;
				}
				Main.queues[0].getNextProcess();
				Main.queues[0].startThread();
			}
		}else {
			prevTimeQuantum = timeNow;
			while(clockTime != -1 && getNextArrivalTime() == clockTime) {
				getNextProcess();
			}
		}
	}*/

	private void demote(CPUBoundProcess process) {
		if(nextQueue == null) return;
		nextQueue.enqueue(process, nextQueue.queueType);
	}
	
	private void demoteSecondProcess(CPUBoundProcess process) {	
		if(nextQueue == null) return;
		//System.out.println("[Q] Level: " + level + " demoting P" + process.getId());
		nextQueue.enqueue(process, nextQueue.queueType);
	}
	
	public long getQuantum(){
		return quantum;
	}
	
}

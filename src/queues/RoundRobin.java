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
		if(prevQueue != null && prevQueue instanceof RoundRobin) {
			clockTime = prevTimeQuantum;
		}
		queueStartTime = clockTime;				
		
		while(clockTime != -1 && getNextArrivalTime() == clockTime) {
			getNextProcess();
		}

		for(int ctr = 1; ctr <= totalBurstTime; ctr++){			
			if((currProcess = peekHead()) != null){			
				
				// Determine if this queue is a lower level queue
				if(prevQueue != null && prevQueue instanceof RoundRobin) { 
					
					// Update the current process' values
					currProcess.setStartTime(prevTimeQuantum);
					if(currProcess.preemptedFlag) {						
						currProcess.setTimeResumed(prevTimeQuantum);
						currProcess.preemptedFlag = false;
					}	
					
					// Determine if a preemption has occurred by an IOBound process
					if(prevProcess != null && 
							prevProcess.getBurstTime() > 0 && 
								prevProcess.getId() != currProcess.getId() &&
									currProcess instanceof IOBoundProcess ) {						
						
						// Flag the previous process as preempted and set necessary values
						prevProcess.setPreempted();
						prevProcess.setTimePreempted(timeNow);
						prevProcess.setEndTime(timeNow);
						prevProcess.preemptedFlag = true;					
						prevTimeQuantum = timeNow;
						
						if(!quantumTimeDone) {							
							if(nextQueue == null) {
								retainSecondProcess(QueueType.RR);
							}else {
								demoteSecondProcess(dequeueSecondProcess());
							}
						}
						counter = 1;
					}
					
				// If this is the highest level queue
				} else {
					
					// Determine if a preemption has occurred by an I/O-bound process
					if(prevProcess != null &&
							prevProcess.getBurstTime() > 0 &&
								prevProcess.getId() != currProcess.getId() &&
									currProcess instanceof IOBoundProcess ) {
						
						/*System.out.println("[RR] level: " + level +
								"Previous process P" + prevProcess.getId() + 
									" was preempted by P" + currProcess.getId());*/
						
						// Flag previous process as preempted and set necessary values
						prevProcess.setPreempted();
						prevProcess.setTimePreempted(timeNow);
						prevProcess.setEndTime(timeNow);
						prevProcess.preemptedFlag = true;												
						prevTimeQuantum = timeNow;
						
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
					}
					
					//	Set the current process' response time if it is executing for the first time
					if(currProcess.getResponseTime() < 0) {			
						if(currProcess.getArrivalTime() <= prevTimeQuantum) {
							currProcess.setStartTime(prevTimeQuantum);
							currProcess.setFirstStartTime(prevTimeQuantum);
						}else {
							currProcess.setStartTime(queueStartTime + ctr - 1);
							currProcess.setFirstStartTime(queueStartTime + ctr - 1);
							prevTimeQuantum = queueStartTime + ctr - 1;
						}
						
						currProcess.setResponseTime();						
						counter = 1;
						
						// Add 1 to the clockTime if the current process's start time is a multiple 
						// of 10 and the quantum time is odd in order to match the values of counter
						// and clock time.
						/*if(currProcess.getStartTime() % 10 == 0 
								&& quantum % 2 == 1) {
							clockTime++;
						}	*/				
					}
					
					// Flag current process as resumed if previously preempted
					if(currProcess.preemptedFlag) {			
						if(currProcess.getArrivalTime() <= prevTimeQuantum) {
							currProcess.setStartTime(prevTimeQuantum);
							currProcess.setTimeResumed(prevTimeQuantum);
						}else {
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
					timeNow = queueStartTime + ctr;
					clockTime = timeNow;
				}
				
				System.out.println("[Roundrobin] Level = " + 
						level + 
						" executing P" + 
						currProcess.getId() + 
						" startTime = " + 
						currProcess.getStartTime() +
						" burstLeft = " +
						burstLeft +
						" timeNow = " + (queueStartTime + ctr) +
						" counter = " + counter +
						" clockTime = " + clockTime);					
				
				while(clockTime != -1 && getNextArrivalTime() == clockTime) {
					getNextProcessForTopQueue();
				}
				
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
					currProcess.setEndTime(timeNow);
					
					if(burstLeft > 0){
						preemptCurrProcess(timeNow);
						currProcess.setPrevBurstPreempted(currProcess.getBurstTime());										
												
						if(nextQueue == null) {
							//System.out.println("[RR] Level: " + level + "retaining P" + currProcess.getId());
							retain(QueueType.RR);
						}else {
							//System.out.println("[RR] Level: " + level + " demoting P" + currProcess.getId());
							demote(dequeue());
						}											
					}								
					
					counter = 0;
					quantumTimeDone = true;
					
				}else {
					quantumTimeDone = false;					
				}
								
				sortByBound();
				
				while(clockTime != -1 && getNextArrivalTime() == clockTime) {
					getNextProcessForTopQueue();
				}
				
				clockTime++;
				counter++;								
			}
			stopThread();			
		}
	}

	private void getNextProcessForTopQueue() {
		if(prevQueue != null && prevQueue instanceof RoundRobin) {
			while(clockTime != -1 && getNextArrivalTime() == clockTime) {
				if(currProcess != null) {
					currProcess.setPreempted();
					currProcess.setTimePreempted(timeNow);
					currProcess.setEndTime(timeNow);
					currProcess.preemptedFlag = true;
					
					if(hasExecuted(currProcess)) {
						prevTimeQuantum = timeNow;
						int burstExecuted = currProcess.getEndTime()-currProcess.getStartTime();
						displayExecutingInUI(burstExecuted, currProcess.getEndTime(), currProcess.getId());
					}
					currProcess = null;
				}
				Main.queues[0].getNextProcess();
				Main.queues[0].startThread();
			}
		}else {
			while(clockTime != -1 && getNextArrivalTime() == clockTime) {
				getNextProcess();
			}
		}
	}

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

package queues;
import constants.QueueType;

public class FCFSQueue extends Queue{
	
	public FCFSQueue(int level){
		super(level);
		this.queueType = QueueType.FCFS;
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
					
					currProcess.setStartTime(prevTimeQuantum);					
					if(currProcess.preemptedFlag) {
						currProcess.setTimeResumed(prevTimeQuantum);
						currProcess.preemptedFlag = false;
					}
					
				}else { 

					// Determine if a preemption has occurred
					if(isRecentlyPreempted()) {
							
							/*System.out.println("[FCFS: ] P" + prevProcess.getId() + 
									" was preempted (endTime: " + timeNow + 
									" counter: " + counter + ")");*/
							
							setPrevProcessPreempted();
							displayExecutingInUI(prevProcess.getEndTime()-prevProcess.getLastTimeResumed(), 
									prevProcess.getEndTime(), 
									prevProcess.getId());
					}
					
					if(currProcess.getResponseTime() < 0) {
						if(currProcess.getArrivalTime() <= prevTimeQuantum) {
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
					}
					
					if(currProcess.preemptedFlag) {		
						if(currProcess.getArrivalTime() <= prevTimeQuantum) {
							currProcess.setStartTime(prevTimeQuantum);
							currProcess.setTimeResumed(prevTimeQuantum);
						}else {
							currProcess.setStartTime(queueStartTime + ctr - 1);
							currProcess.setTimeResumed(queueStartTime + ctr - 1);
						}
						currProcess.preemptedFlag = false;
					}				
				}
				prevProcess = currProcess;
				
				int burstLeft = currProcess.getBurstTime() - 1;
				currProcess.setBurstTime(burstLeft);	
				timeNow = queueStartTime + ctr;
				clockTime = timeNow;								
				
				System.out.println("[FCFS:] Level = " + 
						level + 
						" executing P" + 
						currProcess.getId() + 
						" startTime = " + 
						currProcess.getStartTime() +
						" burstLeft = " +
						burstLeft +
						" timeNow = " + timeNow + 
						" clockTime = " + clockTime);
				
				if(burstLeft <= 0){
					currProcess.setEndTime(timeNow);
					
					dequeue();
					
					System.out.println("[FCFS:] p" + currProcess.getId() + " Done executing. prevBurstPreempted = " + currProcess.getPrevBurstPreempted());
					currProcess.preemptedFlag = false;
					prevProcess = currProcess;
					prevTimeQuantum = timeNow;
				}
				
				sortByBound();
				
				while(clockTime != -1 && getNextArrivalTime() == clockTime) {
					getNextProcessForTopQueue();				
				}				
				clockTime++;
			}			
			stopThread();
		}
	}		

	

	/*public void preemptQueue() {
		if(currProcess != null) {
			currProcess.setPreempted();
			currProcess.setTimePreempted(timeNow);
			currProcess.setEndTime(timeNow);
			currProcess.preemptedFlag = true;					
			prevTimeQuantum = timeNow;
			int burstExecuted = currProcess.getEndTime()-currProcess.getLastTimeResumed();
			displayExecutingInUI(burstExecuted, currProcess.getEndTime(), currProcess.getId());			
		}
	}*/
}

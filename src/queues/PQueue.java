package queues;
import constants.QueueType;

public class PQueue extends Queue{

	public PQueue(int level){
		super(level);
		this.queueType = QueueType.PQ;
	}
				
	public void run(){
		clockTime = prevTimeQuantum;
		queueStartTime = clockTime;
		
		while(clockTime != -1 && getNextArrivalTime() == clockTime) {
			getNextProcess();
		}
		
		for(int ctr = 1; ctr <= totalBurstTime; ctr++){	
			//determineToPromote();
			if(queuePreempted) {
				if(currProcess != null) {
					currProcess.setPreempted();
					currProcess.setTimePreempted(timeNow);
					currProcess.setEndTime(timeNow);
					currProcess.preemptedFlag = true;
					System.out.println("[PQ] timeNow: " + timeNow);					
					System.out.println("[PQ] prevTimeQuantum: " + prevTimeQuantum);					
					if(hasExecuted(currProcess)) {
						prevTimeQuantum = timeNow;
						displayExecutingInUI(currProcess.getEndTime()-currProcess.getLastTimeResumed(),
								currProcess.getEndTime(), 
								currProcess.getId());
					}
				}
				queuePreempted = false;
				break;
			}
			
			if((currProcess = peekHead()) != null){
				if(isLowerLevelQueue()) {
					
					currProcess.setStartTime(prevTimeQuantum);
					if(currProcess.preemptedFlag) {
						currProcess.setTimeResumed(prevTimeQuantum);
						currProcess.preemptedFlag = false;
					}
					
				}else {
					
					if(isRecentlyPreempted()) {
						
						//if(prevProcess.getBurstTime() > 0) {
							setPrevProcessPreempted();
							displayExecutingInUI(prevProcess.getEndTime()-prevProcess.getLastTimeResumed(),
									prevProcess.getEndTime(), 
									prevProcess.getId());
						//}
						
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
							/*currProcess.setStartTime(queueStartTime + ctr);
							currProcess.setTimeResumed(queueStartTime + ctr);*/
							currProcess.setStartTime(queueStartTime + ctr - 1);
							currProcess.setTimeResumed(queueStartTime + ctr - 1 );
						}									
						currProcess.preemptedFlag = false;
					}				
				}
				prevProcess = currProcess;
				
				int burstLeft = currProcess.getBurstTime() - 1;					
				currProcess.setBurstTime(burstLeft);					
				timeNow = queueStartTime + ctr;	
				clockTime = timeNow;
				
				System.out.println("[PQ:] Level = " + 
						level + 
						" executing P" + 
						currProcess.getId() + 
						" startTime = " + 
						currProcess.getStartTime() +
						" burstLeft = " +
						burstLeft +
						" timeNow = " + (queueStartTime + ctr));
							
				if(burstLeft <= 0){	
					currProcess.setEndTime(timeNow);
					
					dequeue();									
					System.out.println("[PQ:] p" + currProcess.getId() + " Done executing. prevBurstPreempted = " + currProcess.getPrevBurstPreempted());
					
					currProcess.preemptedFlag = false;
					prevProcess = currProcess;					
					prevTimeQuantum = timeNow;
				}
				while(clockTime != -1 && getNextArrivalTime() == clockTime) {
					/*if(prevQueue != null && prevQueue instanceof RoundRobin) {						
						if(currProcess != null) {
							currProcess.setPreempted();
							currProcess.setTimePreempted(timeNow);
							currProcess.setEndTime(timeNow);
							currProcess.preemptedFlag = true;
							System.out.println("[PQ] timeNow: " + timeNow);					
							System.out.println("[PQ] prevTimeQuantum: " + prevTimeQuantum);					
							if(hasExecuted(currProcess)) {
								prevTimeQuantum = timeNow;
								int burstExecuted = currProcess.getEndTime()-currProcess.getStartTime();
								displayExecutingInUI(burstExecuted, currProcess.getEndTime(), currProcess.getId());
							}
							currProcess = null;
						}
						Main.queues[0].getNextProcess();
						Main.queues[0].startThread();
					}else {
						getNextProcess();
					}*/		
					getNextProcessForTopQueue();
				}
				clockTime++;
			}
			stopThread();
		}
	}
}

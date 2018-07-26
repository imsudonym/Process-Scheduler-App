package queues;
import constants.QueueType;

public class NonPQueue extends Queue{
	
	public NonPQueue(int level){
		super(level);
		this.queueType = QueueType.NPQ;
	}
			
	public void run(){
		queueStartTime = clockTime;
		
		while(clockTime != -1 && getNextArrivalTime() == clockTime) {
			getNextProcess();
		}
		
		for(int ctr = 0; ctr < totalBurstTime; ctr++){
			determineToPromote();
			if((currProcess = peekHead()) != null){				
				prevProcess = currProcess;
				
				if(currProcess.getResponseTime() < 0) {
					currProcess.setStartTime(queueStartTime + ctr);
					currProcess.setFirstStartTime(queueStartTime + ctr);
					currProcess.setResponseTime();	
				}
				if(currProcess.preemptedFlag) {						
					currProcess.setStartTime(queueStartTime);
					currProcess.setTimeResumed(queueStartTime);						
					currProcess.preemptedFlag = false;
				}				
				int burstLeft = currProcess.getBurstTime() - 1;					
				currProcess.setBurstTime(burstLeft);	
				System.out.println("[NPQ:] Level = " + 
						level + 
						" executing P" + 
						currProcess.getId() + 
						" startTime = " + 
						currProcess.getStartTime() +
						" burstLeft = " +
						burstLeft +
						" timeNow = " + (queueStartTime + ctr));
				timeNow = queueStartTime + ctr;				
				if(burstLeft <= 0){								
					dequeue();									
					System.out.println("[NPQ:] p" + currProcess.getId() + " Done executing. prevBurstPreempted = " + currProcess.getPrevBurstPreempted());
					currProcess.preemptedFlag = false;
					prevProcess = currProcess;					
					prevTimeQuantum = timeNow;
				}
				while(getNextArrivalTime() == clockTime) {
					getNextProcess();
				}
				clockTime++;							
			}
			stopThread();
		}
	}
}
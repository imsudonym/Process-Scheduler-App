package queues;
import constants.QueueType;

public class FCFSQueue extends Queue{
	public FCFSQueue(int level){
		super(level);
		this.queueType = QueueType.FCFS;
	}

	public void run(){
		queueStartTime = clockTime;
		
//		System.out.println("[FCFS] clockTime: " + clockTime);
		
		while(clockTime != -1 && getNextArrivalTime() == clockTime) {
			getNextProcess();
		}
		
		for(int ctr = 0; ctr < totalBurstTime; ctr++){
			if((currProcess = peekHead()) != null){				
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
				
				timeNow = queueStartTime + ctr;
				
				/*System.out.println("[FCFS:] Level = " + 
						level + 
						" executing P" + 
						currProcess.getId() + 
						" startTime = " + 
						currProcess.getStartTime() +
						" burstLeft = " +
						burstLeft +
						" timeNow = " + timeNow + 
						" clockTime = " + clockTime);*/	
				
				if(burstLeft == 0){
					currProcess.setEndTime(timeNow+1);
					dequeue();					
					System.out.println("[FCFS:] p" + currProcess.getId() + " Done executing. prevBurstPreempted = " + currProcess.getPrevBurstPreempted());
					currProcess.preemptedFlag = false;
					prevProcess = currProcess;
					prevTimeQuantum = timeNow;
				}
				while(clockTime != -1 && getNextArrivalTime() == clockTime) {
					getNextProcess();
				}
				clockTime++;
				System.out.println("[FCFS] clockTime: " + clockTime);
			}			
			stopThread();
		}		
	}		

	public void preemptQueue() {
		stopThread();
	}
}

package queues;
import constants.QueueType;

public class FCFSQueue extends Queue{
	public FCFSQueue(int level){
		super(level);
		this.queueType = QueueType.FCFS;
	}

	public void run(){
		//if (clockTime <= 0) return;
		System.out.println("[FCFSQueue:] In run()");		
		queueStartTime = clockTime;
		System.out.println("[FCFSQueue:] clockTime: " + clockTime);

		while(getNextArrivalTime() == clockTime) {			
			getNextProcess();
		}

		//System.out.println("[FCFS:] totalBurstTime: " + totalBurstTime);
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
				System.out.println("[FCFS:] Level = " + 
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
					System.out.println("[FCFS:] p" + currProcess.getId() + " Done executing. prevBurstPreempted = " + currProcess.getPrevBurstPreempted());
					currProcess.preemptedFlag = false;
					prevProcess = currProcess;					
					prevTimeQuantum = timeNow;
				}
				while(clockTime != -1 && getNextArrivalTime() == clockTime) {
					System.out.println("[FCFSQueue:] getNextProcess()");
					getNextProcess();
				}
				clockTime++;
			}			
			stopThread();
		}		
	}		

	public void preemptQueue() {
		stopThread();
	}
}

package queues;
import constants.QueueType;

public class FCFSQueue extends Queue{
	public FCFSQueue(int level){
		super(level);
		this.queueType = QueueType.FCFS;
	}

	public void run(){
		/*System.out.println("[FCFS] inside run");
		System.out.println("[FCFS] queueStartTime: " + queueStartTime);*/
		queueStartTime = clockTime;
		
		while(clockTime != -1 && getNextArrivalTime() == clockTime) {
			getNextProcess();
		}

		for(int ctr = 0; ctr < totalBurstTime; ctr++){
			if((currProcess = peekHead()) != null){		
				if(prevProcess != null && prevProcess.getId() != currProcess.getId()) {
					if(prevProcess.getBurstTime() > 0) {
						prevProcess.setPreempted();
						prevProcess.setTimePreempted(timeNow);		
						prevProcess.setEndTime(timeNow);
						prevProcess.preemptedFlag = true;
						prevTimeQuantum = timeNow;
						int burstExecuted = prevProcess.getEndTime()-prevProcess.getStartTime();
						displayExecutingInUI(burstExecuted, prevProcess.getEndTime(), prevProcess.getId());
					}
				}
				prevProcess = currProcess;	
				if(currProcess.getResponseTime() < 0) {
					if(currProcess.getArrivalTime() <= prevTimeQuantum) {
						currProcess.setStartTime(prevTimeQuantum);
						currProcess.setFirstStartTime(prevTimeQuantum);
					}else {
						currProcess.setStartTime(queueStartTime + ctr);
						currProcess.setFirstStartTime(queueStartTime + ctr);
					}					
					currProcess.setResponseTime();
				}
				if(currProcess.preemptedFlag) {						
					if(currProcess.getArrivalTime() <= prevTimeQuantum) {
						currProcess.setStartTime(prevTimeQuantum);
						currProcess.setStartTime(prevTimeQuantum);
						currProcess.setTimeResumed(prevTimeQuantum);
					}else {
						currProcess.setStartTime(queueStartTime + ctr);
						currProcess.setStartTime(queueStartTime + ctr);
						currProcess.setTimeResumed(queueStartTime + ctr);
					}										
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
			}			
			stopThread();
		}		
	}		

	public void preemptQueue() {
		stopThread();
	}
}

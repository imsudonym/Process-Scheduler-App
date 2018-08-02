package queues;
import constants.QueueType;
import scheduler.Main;

public class NonPQueue extends Queue{
	
	public NonPQueue(int level){
		super(level);
		this.queueType = QueueType.NPQ;
	}
			
	public void run(){
		if(prevQueue != null && prevQueue instanceof RoundRobin) {
			clockTime = prevTimeQuantum;
		}
		queueStartTime = clockTime;
		
		System.out.println("[FCFS] inside run");
		System.out.println("[FCFS] queueStartTime: " + queueStartTime);
		
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
					System.out.println("[FCFS] timeNow: " + timeNow);					
					System.out.println("[FCFS] prevTimeQuantum: " + prevTimeQuantum);					
					if(hasExecuted(currProcess)) {
						prevTimeQuantum = timeNow;
						int burstExecuted = currProcess.getEndTime()-currProcess.getStartTime();
						displayExecutingInUI(burstExecuted, currProcess.getEndTime(), currProcess.getId());
					}
				}
				queuePreempted = false;
				break;
			}
			
			if((currProcess = peekHead()) != null){
				if(prevQueue != null && prevQueue instanceof RoundRobin) {
					currProcess.setStartTime(prevTimeQuantum);
					if(currProcess.preemptedFlag) {
						currProcess.setTimeResumed(prevTimeQuantum);
						currProcess.preemptedFlag = false;
					}
				}else {
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
				currProcess.setBurstTime(burstLeft);
				timeNow = queueStartTime + ctr;
				clockTime = timeNow;
				
				System.out.println("[NPQ:] Level = " + 
						level + 
						" executing P" + 
						currProcess.getId() + 
						" startTime = " + 
						currProcess.getStartTime() +
						" burstLeft = " +
						burstLeft +
						" timeNow = " + (queueStartTime + ctr));
				
				if(burstLeft == 0){		
					currProcess.setEndTime(timeNow);
					dequeue();									
					System.out.println("[NPQ:] p" + currProcess.getId() + " Done executing. prevBurstPreempted = " + currProcess.getPrevBurstPreempted());
					currProcess.preemptedFlag = false;
					prevProcess = currProcess;			
					prevTimeQuantum = timeNow;
				}
				while(clockTime != -1 && getNextArrivalTime() == clockTime) {
					if(prevQueue != null && prevQueue instanceof RoundRobin) {						
						if(currProcess != null) {
							currProcess.setPreempted();
							currProcess.setTimePreempted(timeNow);
							currProcess.setEndTime(timeNow);
							currProcess.preemptedFlag = true;
							System.out.println("[NPQ] timeNow: " + timeNow);					
							System.out.println("[NPQ] prevTimeQuantum: " + prevTimeQuantum);					
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
					}						
				}
				clockTime++;		
			}
			stopThread();
		}
	}
}
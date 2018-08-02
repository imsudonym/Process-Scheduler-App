package queues;
import constants.QueueType;
import scheduler.Main;

public class SJFQueue extends Queue{
	
	public SJFQueue(int level){
		super(level);
		this.queueType = QueueType.SJF;
	}
					
	public void run(){
		if(prevQueue != null && prevQueue instanceof RoundRobin) {
			clockTime = prevTimeQuantum;
		}
		queueStartTime = clockTime;	
		
		System.out.println("[SJF] inside run");
		System.out.println("[SJF] queueStartTime: " + queueStartTime);
		
		while(clockTime != -1 && getNextArrivalTime() == clockTime) {
			getNextProcess();
		}
		
		for(int ctr = 1; ctr <= totalBurstTime; ctr++){			
			if(queuePreempted) {
				if(currProcess != null) {
					currProcess.setPreempted();
					currProcess.setTimePreempted(timeNow);
					currProcess.setEndTime(timeNow);
					currProcess.preemptedFlag = true;
					System.out.println("[SJF] timeNow: " + timeNow);					
					System.out.println("[SJF] prevTimeQuantum: " + prevTimeQuantum);					
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

				System.out.println("[SJF:] Level = " + 
						level + 
						" executing P" + 
						currProcess.getId() + 
						" startTime = " + 
						currProcess.getStartTime() +
						" burstLeft = " +
						burstLeft +
						" timeNow = " + timeNow + 
						" clockTime = " + clockTime);
							
				if(burstLeft == 0){		
					currProcess.setEndTime(timeNow);
					dequeue();									
					System.out.println("[SJF:] p" + currProcess.getId() + " Done executing. prevBurstPreempted = " + currProcess.getPrevBurstPreempted());
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
							System.out.println("*****[SJF] timeNow: " + timeNow);					
							System.out.println("	*****[SJF] prevTimeQuantum: " + prevTimeQuantum);					
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

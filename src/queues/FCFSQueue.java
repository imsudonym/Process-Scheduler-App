package queues;
import constants.QueueType;
import scheduler.Main;

public class FCFSQueue extends Queue{
	
	public FCFSQueue(int level){
		super(level);
		this.queueType = QueueType.FCFS;
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
			if((currProcess = peekHead()) != null){
				if(prevQueue != null && prevQueue instanceof RoundRobin) {
					currProcess.setStartTime(prevTimeQuantum);
					if(currProcess.preemptedFlag) {
						currProcess.setTimeResumed(prevTimeQuantum);
						currProcess.preemptedFlag = false;
					}					
				}else {
					if(prevProcess != null && prevProcess.getId() != currProcess.getId()) {
						if(prevProcess.getBurstTime() > 0) {
							System.out.println("[FCFS: ] P" + prevProcess.getId() + " was preempted (endTime: " + timeNow + ")");
							prevProcess.setPreempted();
							prevProcess.setTimePreempted(timeNow);
							prevProcess.setEndTime(timeNow);
							prevProcess.preemptedFlag = true;
							prevTimeQuantum = timeNow;
							int burstExecuted = prevProcess.getEndTime()-prevProcess.getStartTime();
							displayExecutingInUI(burstExecuted, prevProcess.getEndTime(), prevProcess.getId());
						}
					}
					if(currProcess.getResponseTime() < 0) {
						if(currProcess.getArrivalTime() <= prevTimeQuantum) {
							currProcess.setStartTime(prevTimeQuantum);
							currProcess.setFirstStartTime(prevTimeQuantum);
						}else {
							currProcess.setStartTime(queueStartTime + ctr - 1);
							currProcess.setFirstStartTime(queueStartTime + ctr - 1);
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
	
	private void getNextProcessForTopQueue() {
		System.out.println("[FCFS] GETTING NEXT PROCESSES");
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
				preemptQueue();
				Main.queues[0].startThread();
			}
		}else {
			while(clockTime != -1 && getNextArrivalTime() == clockTime) {
				getNextProcess();
			}
		}
	}

	public void preemptQueue() {
		if(currProcess != null) {
			currProcess.setPreempted();
			currProcess.setTimePreempted(timeNow);
			currProcess.setEndTime(timeNow);
			currProcess.preemptedFlag = true;					
			prevTimeQuantum = timeNow;
			int burstExecuted = currProcess.getEndTime()-currProcess.getStartTime();
			displayExecutingInUI(burstExecuted, currProcess.getEndTime(), currProcess.getId());			
		}
	}
}

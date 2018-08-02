package queues;
import constants.QueueType;
import process.CPUBoundProcess;

public class RoundRobin extends PreemptiveQueue{	
	public boolean executing = false;
	private int counter = 1;
	
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
		queueStartTime = clockTime;				
		
		while(clockTime != -1 && getNextArrivalTime() == clockTime) {
			getNextProcess();
		}
		System.out.println("[Roundrobin:] Inside run method");		
		System.out.println("[Roundrobin:] totalBurstTime: " + totalBurstTime);
		System.out.println("[Roundrobin:] clockTime: " + clockTime + " getNextArrivalTime: " + getNextArrivalTime());

		for(int ctr = 1; ctr <= totalBurstTime; ctr++){	
			if((currProcess = peekHead()) != null){
				if(currProcess.getResponseTime() < 0) {					
					if(currProcess.getArrivalTime() <= prevTimeQuantum) {
						currProcess.setStartTime(prevTimeQuantum);
						currProcess.setFirstStartTime(prevTimeQuantum);
					}else {
						System.out.println("[RR] queueStartTime: " + queueStartTime + " ctr: " + ctr);
						currProcess.setStartTime(queueStartTime);
						currProcess.setFirstStartTime(queueStartTime );
						prevTimeQuantum = queueStartTime;
					}
					currProcess.setResponseTime();
					counter = 1;
					if(currProcess.getStartTime()%10 == 0 && quantum%2 == 1) {
						System.out.println("[RR] ct++");
						clockTime++;
					}					
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
				System.out.println("[Roundrobin] Level = " + 
						level + 
						" executing P" + 
						currProcess.getId() + 
						" startTime = " + 
						currProcess.getStartTime() +
						" burstLeft = " +
						burstLeft +
						" timeNow = " + (queueStartTime + ctr) +
						" counter = " + counter);
				
				if(burstLeft <= 0){		
					currProcess.setEndTime(timeNow);

					dequeue();
					System.out.println("p" + currProcess.getId() + " Done executing.");					

					currProcess.preemptedFlag = false;
					prevProcess = currProcess;
					prevTimeQuantum = timeNow;
					
				}else if(counter == quantum){	
					
					System.out.println("[Roundrobin:] Quantum time is DONE (timeNow = " + timeNow + ")");
					prevTimeQuantum = timeNow;
					currProcess.setEndTime(timeNow);
				
					if(burstLeft > 0){
						preemptCurrProcess(timeNow);
						int burstPreempted = currProcess.getBurstTime();
						currProcess.setPrevBurstPreempted(burstPreempted);										

						while(clockTime != -1 && getNextArrivalTime() == clockTime) {
							getNextProcess();
						}
						
						if(nextQueue == null) {
							retain(QueueType.RR);
						}else {
							demote(dequeue());
						}
					}
					counter = 0;
				}
				
				while(clockTime != -1 && getNextArrivalTime() == clockTime) {
					getNextProcess();
				}
				clockTime++;
				while(clockTime != -1 && getNextArrivalTime() == clockTime) {
					getNextProcess();
				}
				
				counter++;
				
			}
			stopThread();			
		}				
	}

	protected void demote(CPUBoundProcess process) {
		if(nextQueue == null) return;
		System.out.println("****[RR] level = " + level + " demote p" + process.getId() + " burstLeft = " + process.getBurstTime() + " size = " + getSize());
		nextQueue.enqueue(process, nextQueue.queueType);
	}
	
	public long getQuantum(){
		return quantum;
	}
	
}

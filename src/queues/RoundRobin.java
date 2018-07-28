package queues;
import constants.QueueType;
import process.CPUBoundProcess;

public class RoundRobin extends PreemptiveQueue{
	private int quantum = 0;
	public boolean executing = false;
	
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
		queueStartTime = clockTime; // time this queue started executing.		
		
		System.out.println("[Roundrobin:] Inside run method");
		System.out.println("[Roundrobin:] queueStartTime: " + queueStartTime);
		
		while(clockTime != -1 && getNextArrivalTime() == clockTime) {
			getNextProcess();
		}		
		for(int ctr = 0; ctr < totalBurstTime; ctr++){					
			if((currProcess = peekHead()) != null){				
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
				System.out.println("[Roundrobin] Level = " + 
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
					currProcess.setEndTime(timeNow+1);
					
					dequeue();													
					System.out.println("p" + currProcess.getId() + " Done executing.");					
					
					currProcess.preemptedFlag = false;
					prevProcess = currProcess;
					prevTimeQuantum = timeNow;
					
				}else if(timeNow == prevTimeQuantum + quantum){	
					
					System.out.println("[Roundrobin:] Quantum time is DONE (timeNow = " + timeNow + ")");
					prevTimeQuantum = timeNow;
					currProcess.setEndTime(timeNow+1);
				
					if(burstLeft > 0){											
						preemptCurrProcess(timeNow);
						int burstPreempted = currProcess.getBurstTime();
						currProcess.setPrevBurstPreempted(burstPreempted);	
						
						while(clockTime != -1 && getNextArrivalTime() == clockTime) {
							getNextProcess();
						}
						
						if(nextQueue == null) {
							retain(QueueType.RR);
						}/*else {
							demote(dequeue());									
						}*/
					}
				}				
				while(clockTime != -1 && getNextArrivalTime() == clockTime) {
					getNextProcess();
				}
				clockTime++;
			}
			stopThread();
		}				
	}

	protected void demote(CPUBoundProcess process) {
		if(nextQueue == null) return;
		System.out.println("level = " + level + " demote p" + process.getId() + " burstLeft = " + process.getBurstTime() + " size = " + getSize());
		nextQueue.enqueue(process, nextQueue.queueType);
	}
	
	public long getQuantum(){
		return quantum;
	}
	
}

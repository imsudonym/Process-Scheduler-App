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
		
		while(getNextArrivalTime() == clockTime) {
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
				/*if(quantum > 1 && currProcess instanceof IOBoundProcess) {	
					if(timeNow == prevTimeQuantum + (quantum-1)){
						preemptCurrProcess(timeNow);								
						displayInUI(quantum-1, timeNow);

						currProcess.setArrivalTime(timeNow + ((IOBoundProcess)(currProcess)).getIoSpeed());
						if(burstLeft > 0){						
							insertToReadyQueue((IOBoundProcess)currProcess);
							dequeue();
						}
					}							
				}*/

				if(burstLeft <= 0){										
					if(currProcess.getPrevBurstPreempted() < quantum){
						displayInUI(currProcess.getPrevBurstPreempted(), timeNow);								
					}					
					dequeue();													
					System.out.println("p" + currProcess.getId() + " Done executing.");					
					currProcess.preemptedFlag = false;
					prevProcess = currProcess;
					prevTimeQuantum = timeNow;				
				}			
				while(getNextArrivalTime() == clockTime) {
					getNextProcess();
				}
				if(timeNow == prevTimeQuantum + quantum){							
					System.out.println("[Roundrobin:] Quantum time is DONE (timeNow = " + timeNow + ")");
					prevTimeQuantum = timeNow;
					displayInUI(quantum, timeNow);					
					if(burstLeft > 0){											
						preemptCurrProcess(timeNow);
						int burstPreempted = currProcess.getBurstTime();
						currProcess.setPrevBurstPreempted(burstPreempted);						
						if(nextQueue == null) {
							retain(QueueType.RR);
						} /*else {								
							demote(dequeue());									
						}*/
					}
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

package queues;
import constants.QueueType;
import process.CPUBoundProcess;
import scheduler.Main;

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
		if(prevQueue != null && prevQueue instanceof RoundRobin) {
			clockTime = prevTimeQuantum;
		}
		queueStartTime = clockTime;				
		
		while(clockTime != -1 && getNextArrivalTime() == clockTime) {
			getNextProcess();
		}
		System.out.println("[Roundrobin:] Inside run method");		
		System.out.println("[Roundrobin:] totalBurstTime: " + totalBurstTime);
		System.out.println("[Roundrobin:] clockTime: " + clockTime + " getNextArrivalTime: " + getNextArrivalTime());

		for(int ctr = 1; ctr <= totalBurstTime; ctr++){

			if(queuePreempted) {
				if(currProcess != null) {
					currProcess.setPreempted();
					currProcess.setTimePreempted(timeNow);
					currProcess.setEndTime(timeNow);
					currProcess.preemptedFlag = true;
					System.out.println("[RR] timeNow: " + timeNow);					
					System.out.println("[RR] prevTimeQuantum: " + prevTimeQuantum);					
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
						counter = 1;
						currProcess.setTimeResumed(prevTimeQuantum);
						currProcess.preemptedFlag = false;
					}					
				}else {
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
				}
				prevProcess = currProcess;
				
				int burstLeft = currProcess.getBurstTime() - 1;					
				currProcess.setBurstTime(burstLeft);	
				timeNow = queueStartTime + ctr;
				clockTime = timeNow;
				
				System.out.println("[Roundrobin] Level = " + 
						level + 
						" executing P" + 
						currProcess.getId() + 
						" startTime = " + 
						currProcess.getStartTime() +
						" burstLeft = " +
						burstLeft +
						" timeNow = " + (queueStartTime + ctr) +
						" counter = " + counter +
						" clockTime = " + clockTime);
				
				while(clockTime != -1 && getNextArrivalTime() == clockTime) {
					if(prevQueue != null && prevQueue instanceof RoundRobin) {						
						if(currProcess != null) {
							currProcess.setPreempted();
							currProcess.setTimePreempted(timeNow);
							currProcess.setEndTime(timeNow);
							currProcess.preemptedFlag = true;
							System.out.println("[RR] timeNow: " + timeNow);					
							System.out.println("[RR] prevTimeQuantum: " + prevTimeQuantum);					
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

						
						if(prevQueue != null && prevQueue instanceof RoundRobin) {
							while(clockTime != -1 && getNextArrivalTime() == clockTime) {
								if(currProcess != null) {
									currProcess.setPreempted();
									currProcess.setTimePreempted(timeNow);
									currProcess.setEndTime(timeNow);
									currProcess.preemptedFlag = true;
									System.out.println("[RR] timeNow: " + timeNow);					
									System.out.println("[RR] prevTimeQuantum: " + prevTimeQuantum);					
									if(hasExecuted(currProcess)) {
										prevTimeQuantum = timeNow;
										int burstExecuted = currProcess.getEndTime()-currProcess.getStartTime();
										displayExecutingInUI(burstExecuted, currProcess.getEndTime(), currProcess.getId());
									}
									currProcess = null;
								}
								Main.queues[0].getNextProcess();
								Main.queues[0].startThread();
							}
						}else {
							while(clockTime != -1 && getNextArrivalTime() == clockTime) {
								getNextProcess();
							}
						}	
						
						if(nextQueue == null) {
							retain(QueueType.RR);
						}else {
							demote(dequeue());
						}
					}
					counter = 0;
				}
								
				if(prevQueue != null && prevQueue instanceof RoundRobin) {
					while(clockTime != -1 && getNextArrivalTime() == clockTime) {
						if(currProcess != null) {
							currProcess.setPreempted();
							currProcess.setTimePreempted(timeNow);
							currProcess.setEndTime(timeNow);
							currProcess.preemptedFlag = true;
							System.out.println("[RR] timeNow: " + timeNow);					
							System.out.println("[RR] prevTimeQuantum: " + prevTimeQuantum);					
							if(hasExecuted(currProcess)) {
								prevTimeQuantum = timeNow;
								int burstExecuted = currProcess.getEndTime()-currProcess.getStartTime();
								displayExecutingInUI(burstExecuted, currProcess.getEndTime(), currProcess.getId());
							}
							currProcess = null;
						}
						Main.queues[0].getNextProcess();
						Main.queues[0].startThread();
					}
				}else {
					while(clockTime != -1 && getNextArrivalTime() == clockTime) {
						getNextProcess();
					}
				}									
				clockTime++;
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

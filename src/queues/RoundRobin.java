package queues;
import constants.QueueType;
import process.CPUBoundProcess;
import process.IOBoundProcess;
import scheduler.Main;

public class RoundRobin extends PreemptiveQueue{	
	public boolean executing = false;
	private int counter = 1;
	private boolean quantumTimeDone;
	
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
					if(prevProcess != null && 
							prevProcess.getId() != currProcess.getId() &&
								currProcess instanceof IOBoundProcess ) {
					
					System.out.println("[RR] Previous process P" + prevProcess.getId() + " was preempted by P" + currProcess.getId());
					
					if(prevProcess.getBurstTime() > 0) {
						prevProcess.setPreempted();
						prevProcess.setTimePreempted(timeNow);
						prevProcess.setEndTime(timeNow);
						prevProcess.preemptedFlag = true;												
						prevTimeQuantum = timeNow;
						
						int burstExecuted = prevProcess.getEndTime()-prevProcess.getStartTime();
						System.out.println("[RR] quantumTimeDone: " + quantumTimeDone);
						if(!quantumTimeDone) {													
							if(nextQueue == null) {
								System.out.println("[RR] NextQueue is NULL");
								retainSecondProcess(QueueType.RR);
							}else {
								System.out.println("[RR] NextQueue is not NULL");
								//displayExecutingInUI(burstExecuted, prevProcess.getEndTime(), prevProcess.getId());
								demoteSecondProcess(dequeueSecondProcess());
							}
							System.out.println("[RR] displaying P" + prevProcess.getId() + " timeNow: " + timeNow);
						}
						
						counter = 1;
					}
					currProcess.setStartTime(prevTimeQuantum);
					if(currProcess.preemptedFlag) {
						counter = 1;
						currProcess.setTimeResumed(prevTimeQuantum);
						currProcess.preemptedFlag = false;
					}					
				}
				}else {
					if(prevProcess != null && 
								prevProcess.getId() != currProcess.getId() &&
									currProcess instanceof IOBoundProcess ) {
						
						System.out.println("[RR] Previous process P" + prevProcess.getId() + " was preempted by P" + currProcess.getId());
						
						if(prevProcess.getBurstTime() > 0) {
							prevProcess.setPreempted();
							prevProcess.setTimePreempted(timeNow);
							prevProcess.setEndTime(timeNow);
							prevProcess.preemptedFlag = true;												
							prevTimeQuantum = timeNow;
							
							int burstExecuted = prevProcess.getEndTime()-prevProcess.getStartTime();
							System.out.println("[RR] quantumTimeDone: " + quantumTimeDone);
							if(!quantumTimeDone) {
								System.out.println("[RR] displaying P" + prevProcess.getId() + " timeNow: " + timeNow);
								//displayExecutingInUI(burstExecuted, prevProcess.getEndTime(), prevProcess.getId());
								if(nextQueue == null) {
									retainSecondProcess(QueueType.RR);
								}else {
									demoteSecondProcess(dequeueSecondProcess());
								}
							}
							
							counter = 1;
						}
					}
					if(currProcess.getResponseTime() < 0) {					
						if(currProcess.getArrivalTime() <= prevTimeQuantum) {
							currProcess.setStartTime(prevTimeQuantum);
							currProcess.setFirstStartTime(prevTimeQuantum);
						}else {
							currProcess.setStartTime(queueStartTime + ctr - 1);
							currProcess.setFirstStartTime(queueStartTime + ctr - 1);
							prevTimeQuantum = queueStartTime + ctr - 1;
						}
						currProcess.setResponseTime();
						counter = 1;
						if(currProcess.getStartTime()%10 == 0 && quantum%2 == 1) {
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
				
				if(burstLeft < 0) break;
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
					System.out.println("[RR] Heads up #1");
					getNextProcessForTopQueue();
				}
				
				//System.out.println("[RR] quantum: " + quantum + " counter " + counter + " P" + currProcess.getId());
				if(burstLeft <= 0){
					System.out.println("[RR] P" + currProcess.getId() + " Done executing.");					
					currProcess.setEndTime(timeNow);
					
					dequeue();
					
					currProcess.preemptedFlag = false;
					prevProcess = currProcess;
					prevTimeQuantum = timeNow;
					counter = 0;
					
				}else if (counter == quantum-1) {
					if(currProcess instanceof IOBoundProcess) {
						System.out.println("[Roundrobin:] IOBOUND Quantum time is DONE (timeNow = " + timeNow + ")");						
						
						currProcess.setEndTime(timeNow);
						currProcess.setArrivalTime(timeNow+((IOBoundProcess) currProcess).getIoSpeed());
						
						System.out.println("[RR] Next IOBOUND arrival: " + currProcess.getArrivalTime());
												
						prevTimeQuantum = timeNow;
						Main.enqueue((IOBoundProcess) dequeue());						
						
						counter = 0;
						quantumTimeDone = true;
					}else {
						quantumTimeDone = false;
					}
				}else if(counter == quantum){	
					
					System.out.println("[Roundrobin:] Quantum time is DONE (timeNow = " + timeNow + ")");
					prevTimeQuantum = timeNow;
					currProcess.setEndTime(timeNow);
				
					//getNextProcessForTopQueue();
					
					if(burstLeft > 0){
						preemptCurrProcess(timeNow);
						currProcess.setPrevBurstPreempted(currProcess.getBurstTime());										
												
						if(nextQueue == null) {
							retain(QueueType.RR);
						}else {
							demote(dequeue());
						}
												
					}								
					
					counter = 0;
					quantumTimeDone = true;
					
				}else {
					quantumTimeDone = false;					
				}
								
				sortByBound();
				
				while(clockTime != -1 && getNextArrivalTime() == clockTime) {
					System.out.println("[RR] Heads up #2");
					getNextProcessForTopQueue();
				}
				
				clockTime++;
				counter++;								
			}
			stopThread();			
		}				
	}

	private void getNextProcessForTopQueue() {
		System.out.println("[RR] GETTING NEXT PROCESSES");
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
				Main.queues[0].startThread();
			}
		}else {
			while(clockTime != -1 && getNextArrivalTime() == clockTime) {
				getNextProcess();
			}
		}
	}

	private void demote(CPUBoundProcess process) {
		if(nextQueue == null) return;
		nextQueue.enqueue(process, nextQueue.queueType);
	}
	
	private void demoteSecondProcess(CPUBoundProcess process) {
		if(nextQueue == null) return;
		nextQueue.enqueue(process, nextQueue.queueType);
	}
	
	public long getQuantum(){
		return quantum;
	}
	
}

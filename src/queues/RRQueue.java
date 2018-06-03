package queues;
import process.CPUBoundProcess;
import process.IOBoundProcess;
import scheduler.Scheduler;

public class RRQueue extends PreemptiveQueue{
	private int quantum = 0;
	public boolean executing = false;
	
	public RRQueue(int level, int quantum){
		super(level);
		this.quantum = quantum;
	}	
	
	public void startThread(){
		running = true;
		RRThread.start();
	}
	
	public void stopThread(){
		RRThread.interrupt();
		running = false;
	}

	public void reenqueue(CPUBoundProcess newProcess){		
		array.add(newProcess);		
		allProcessesDone = 0;		
	}	
	
	Thread RRThread = new Thread(){				
		public void run(){
			while(running){
				if(prevQueueDone == 1 && peekHead() != null){
					currProcess = peekHead();
					if(timeStart < 0){
						if(timeEnd != 0)						
							timeStart = timeEnd;
						else
							timeStart = Scheduler.clockTime;						
					}					
				
					long startTime = Scheduler.clockTime;
					if(prevProcess != null && prevProcess.preemptedFlag) {
						startTime = prevProcess.getTimePreempted(prevProcess.getTimesPreempted()-1);					
					}else {
						startTime = Scheduler.clockTime;
					}
					
					currProcess.setStartTime(startTime);
					
					if(currProcess.getResponseTime() < 0 && hasExecuted(currProcess)) {
						currProcess.setStartTime(startTime);
						currProcess.setFirstStartTime(startTime);
						currProcess.setResponseTime();
						System.out.println("Setting response time (p" + currProcess.getId() + ") to " + currProcess.getResponseTime());
					}					
					
					if(currProcess.preemptedFlag) {						
						System.out.println("resuming p" + currProcess.getId() + "..... timeStart = " + startTime);						
						currProcess.setStartTime(startTime);
						currProcess.setTimeResumed(startTime);						
						currProcess.preemptedFlag = false;
					}
					
					long timeNow = Scheduler.clockTime;	
					if(prevTime < timeNow){				
						System.out.println("| Level = " + level + " executing p" + currProcess.getId() + " startTime = " + currProcess.getStartTime() + " timeNow = " + timeNow);
						
						int lapse = (int)(timeNow - prevTime);
						int burstLeft = currProcess.getBurstTime() - lapse;					
						currProcess.setBurstTime(burstLeft);																	
											
						/* Conditional immediately below relinquishes IOBound process
						 * for IO operation.
						 * */
						if(currProcess instanceof IOBoundProcess) {							
							if(timeNow == prevTimeQuantum + (quantum-1)){
								preemptCurrProcess((int)timeNow);								
								displayInUI(quantum-1, (int)timeNow);											
								currProcess.setArrivalTime(timeNow + ((IOBoundProcess)(currProcess)).getIoSpeed());								
								if(burstLeft > 0){						
									insertToReadyQueue((IOBoundProcess)currProcess);
									dequeue();
								}							
								prevTimeQuantum = timeNow;
							}							
						}
												
						if(timeNow == prevTimeQuantum + quantum){							
							System.out.println("| -- Quantum time is done timeNow = " + timeNow);
							
							displayInUI(quantum, (int)timeNow);
							
							if(burstLeft > 0){													
								preemptCurrProcess((int)timeNow);
								int burstPreempted = currProcess.getBurstTime();
								currProcess.setPrevBurstPreempted(burstPreempted);
								
								if(nextQueue == null) {
									retain();
								} else {
									demote(currProcess);
									dequeue();
								}
							}							
							prevTimeQuantum = timeNow;
						}
						
						if(burstLeft <= 0){		
							currProcess.setWaitTimePreemptive();
										
							if(currProcess.getPrevBurstPreempted() < quantum){						
								displayInUI(currProcess.getPrevBurstPreempted(), (int)timeNow);								
							}
							
							dequeue();													
							System.out.println("p" + currProcess.getId() + " Done executing.");
							
							//currProcess.setTimeResumed(timeNow);
							currProcess.preemptedFlag = false;
							prevProcess = currProcess;
							
							timeEnd = Scheduler.clockTime;
							prevTimeQuantum = timeNow;
							timeStart = -1;							
						}
																			
					}					
					prevTime = timeNow;					
					
				}else{										
					
					if (allProcessesDone == 0 && getSize() == 0){						
						allProcessesDone = 1;
						startLowerLevelQueues();
						
						if(level == Scheduler.getMaxLevelOfQueues() && Scheduler.processes.size() == 0) {
							System.out.println("Allprocessdon size zero.. stopping simulation...");
							simulationDone();
						}
					}			
				}
			}
		}		
	};

	protected void demote(CPUBoundProcess process) {
		
		if(nextQueue == null) return;
		
		System.out.println("level = " + level + " demote p" + process.getId() + " burstLeft = " + process.getBurstTime() + " size = " + getSize());
		
		if(nextQueue instanceof FCFSQueue) {
			((FCFSQueue)nextQueue).enqueue(process);
		}else if(nextQueue instanceof SJFQueue){
			((SJFQueue)nextQueue).enqueue(process);
		}else if(nextQueue instanceof SRTFQueue) {
			((SRTFQueue)nextQueue).enqueue(process);
		}else if(nextQueue instanceof NonPQueue) {
			((NonPQueue)nextQueue).enqueue(process);
		}else if(nextQueue instanceof PQueue) {
			((PQueue)nextQueue).enqueue(process);
		}else if(nextQueue instanceof RRQueue) {
			((RRQueue)nextQueue).enqueue(process);
		}
	}
	
	public long getQuantum(){
		return quantum;
	}
	
}

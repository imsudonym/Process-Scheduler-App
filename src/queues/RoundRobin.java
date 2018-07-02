package queues;
import constants.QueueType;
import process.CPUBoundProcess;
import process.IOBoundProcess;
import scheduler.Scheduler;

public class RoundRobin extends PreemptiveQueue{
	private int quantum = 0;
	public boolean executing = false;
	
	public RoundRobin(int level, int quantum){
		super(level);
		this.quantum = quantum;
		this.queueType = QueueType.RR;
	}	
	
	public void startThread(){
		prevTime = Scheduler.clockTime;
		running = true;
		thread.start();
	}
	
	public void stopThread(){
		thread.interrupt();
		running = false;
	}

	public void reenqueue(CPUBoundProcess newProcess){		
		array.add(newProcess);		
		allProcessesDone = 0;		
	}	
	
	Thread thread = new Thread(){				
		public void run(){
			while(running){
				System.out.print("");	
				if(peekHead() != null){
					currProcess = peekHead();									
				
					long startTime = Scheduler.clockTime;
					if(prevProcess != null && prevProcess.preemptedFlag) {						
						startTime = prevProcess.getTimePreempted(prevProcess.getTimesPreempted()-1);
					}
										
					//currProcess.setStartTime(startTime);
					
					if(currProcess.getResponseTime() < 0/* && hasExecuted(currProcess)*/) {
						System.out.println("[Roundrobin:] P" + currProcess.getId() + "startTime=" + startTime);
						currProcess.setStartTime(startTime);
						currProcess.setFirstStartTime(startTime);
						currProcess.setResponseTime();
						//System.out.print("");
						//System.out.println("Setting response time (p" + currProcess.getId() + ") to " + currProcess.getResponseTime() + " timeNow = " +  startTime);
					}					
					
					if(currProcess.preemptedFlag) {						
						System.out.println("resuming p" + currProcess.getId() + "..... timeStart = " + startTime);						
						currProcess.setStartTime(startTime);
						currProcess.setTimeResumed(startTime);						
						currProcess.preemptedFlag = false;
					}

					long timeNow = Scheduler.clockTime;
					if(timeNow - prevTime > 1) {
						prevTime = timeNow;
					}
					
					if(prevTime < timeNow){				
						System.out.println("[Roundrobin:] prevTime:" + prevTime + " timeNow:" + timeNow);
						System.out.println("| Level = " + level + " executing p" + currProcess.getId() + " startTime = " + currProcess.getStartTime() + " timeNow = " + timeNow);
						
						int lapse = (int)(timeNow - prevTime);
						int burstLeft = currProcess.getBurstTime() - lapse;					
						currProcess.setBurstTime(burstLeft);																	
											
						/* Block below relinquishes IOBound process
						 * for supposedly 'IO operation'.
						 * */
						if(currProcess instanceof IOBoundProcess) {
							System.out.println("[Roundrobin:] prevTimeQuantum:" + prevTimeQuantum);
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
									retain(QueueType.RR);
								} else {								
									demote(dequeue());									
								}
							}							
							prevTimeQuantum = timeNow;
						}
						
						if(burstLeft <= 0){		
							//currProcess.setWaitTimePreemptive();
										
							if(currProcess.getPrevBurstPreempted() < quantum){						
								displayInUI(currProcess.getPrevBurstPreempted(), (int)timeNow);								
							}
							
							dequeue();													
							System.out.println("p" + currProcess.getId() + " Done executing.");
							
							//currProcess.setTimeResumed(timeNow);
							currProcess.preemptedFlag = false;
							prevProcess = currProcess;
							
							//timeEnd = Scheduler.clockTime;
							prevTimeQuantum = timeNow;
							//timeStart = -1;							
						}				
						
						prevTime = timeNow;
					}															
					
				}else{		
					
					if(peekHead() == null && getSize() == 0 && isHigherQueueDone()) {
						if(Scheduler.processes.size() == 0) {
							if(level == Scheduler.getMaxLevelOfQueues()) {						
								System.out.println("level = " + level + " stopping simulation...");
								simulationDone();
							}else {							
								System.out.println("[Roundrobin:] starting lower level queues");
								startLowerLevelQueues();
								stopThread();								
							}
						}
					}
					
					/*if (allProcessesDone == 0 && getSize() == 0){						
						allProcessesDone = 1;
						startLowerLevelQueues();
						
						if(level == Scheduler.getMaxLevelOfQueues() && Scheduler.processes.size() == 0) {
							System.out.println("Allprocessdon size zero.. stopping simulation...");
							simulationDone();
						}
					}		*/
				}
			}
		}		
	};

	protected void demote(CPUBoundProcess process) {
		if(nextQueue == null) return;
		System.out.println("level = " + level + " demote p" + process.getId() + " burstLeft = " + process.getBurstTime() + " size = " + getSize());
		nextQueue.enqueue(process, nextQueue.queueType);
	}
	
	public long getQuantum(){
		return quantum;
	}
	
}

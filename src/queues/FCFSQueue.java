package queues;
import java.util.ArrayList;

import constants.SchedulingAlgorithm;
import datastructure.PseudoArray;
import gui.GanttChart;
import process.CPUBoundProcess;
import process.IOBoundProcess;
import scheduler.Scheduler;

public class FCFSQueue extends Queue{
		
	private PseudoArray array = new PseudoArray(20);
	private CPUBoundProcess currProcess;	
	private boolean running = false;
	
	private long timeStart = -1;
	private long timeEnd;
	
	private Object prevQueue;
	private Object nextQueue;
	
	byte level = -1;
	private byte allProcessesDone = 1;
	public byte prevQueueDone = 1;
	
	public static ArrayList<CPUBoundProcess> processList = new ArrayList<CPUBoundProcess>();
	
	public FCFSQueue(int level){
		this.level = (byte)level;
	}
	
	public void startThread(){
		running = true;
		FCFSThread.start();
		System.out.println("+++++++++++++++++++++++++++ This thread has long gone started already");
	}
	
	public void stopThread(){
		//suspend();
		FCFSThread.interrupt();
		running = false;
	}	
	
	void suspend() {
		System.out.println("Suspending thread..");
		suspended = true;
	}
	
	synchronized void resume() {
		System.out.println("Trying to restart thread...");
		running = true;
		suspended = false;
		notify();
	}
	
	public CPUBoundProcess dequeue(){
		CPUBoundProcess prc = array.remove();											
		return prc;
	}
	
	public CPUBoundProcess peekHead(){
		if(array.getHead() == null) {
			return null;
		}else {
			return array.getHead().getValue();
		}
	}
	
	public int getSize(){
		return array.getSize();
	}
	
	public void enqueue(CPUBoundProcess newProcess){		
		
		array.add(newProcess);		
		
		if(!processList.contains(newProcess)) {
			processList.add(newProcess);
		}
		
		allProcessesDone = 0;		
		
		/* 
		 * Start executing (this queue) if 
		 * previous higher priority queue 
		 * is NOT executing or null.
		 * 
		 * */
		if(prevQueue != null) {
			int queueSize = 0;
			
			if(prevQueue instanceof RRQueue) {
				queueSize = ((RRQueue)(prevQueue)).getSize();		
			}else if(prevQueue instanceof FCFSQueue) {
				queueSize = ((FCFSQueue)(prevQueue)).getSize();
			}else if(prevQueue instanceof SJFQueue) {
				queueSize = ((SJFQueue)(prevQueue)).getSize();
			}else if(prevQueue instanceof SRTFQueue) {
				queueSize = ((SRTFQueue)(prevQueue)).getSize();
			}else if(prevQueue instanceof NonPQueue) {
				queueSize = ((NonPQueue)(prevQueue)).getSize();
			}else if(prevQueue instanceof PQueue) {
				queueSize = ((PQueue)(prevQueue)).getSize();
			}

			if(queueSize > 0) {				
				prevQueueDone = 0;
				return;
			}				
		}
		
		/*
		 * Conditional below determines if the new process is IO-bound and if
		 * the queue is executing a CPU-bound process when the new process arrived.
		 * If yes, we preempt it by displaying process in Gantt chart and shiftingIoBoundsToFront()
		 * 
		 * */
		if(newProcess instanceof IOBoundProcess) {
			if(currProcess != null  && currProcess instanceof CPUBoundProcess && currProcess.getPrevBurstPreempted()-currProcess.getBurstTime() > 0) {
				System.out.println("| !! p" + currProcess.getId() + " was executing when p" + newProcess.getId() + " burstLeft = " + currProcess.getBurstTime());
				
				long timeNow = Scheduler.clockTime;
				prevTimeQuantum = timeNow; 
									
				// TODO: Make sure to setEndTime the preempted process on all instances where you put preemption.
				
				currProcess.setPreempted();
				currProcess.setTimePreempted(timeNow);
				currProcess.setEndTime(timeNow);
				currProcess.preemptedFlag = true;
				prevProcess = currProcess;				
				
				int burstExecuted = (int) (currProcess.getEndTime()-currProcess.getStartTime());
				System.out.println("| burstExecuted = " + burstExecuted);			
				
				currProcess.setPrevBurstPreempted(currProcess.getBurstTime());			
				GanttChart.addExecutingProcess(level, currProcess.getId(), burstExecuted, (int)timeNow, SchedulingAlgorithm.RR);
				//currProcess = newProcess;
				//currProcess.setStartTime(timeNow);
				
				System.out.println("size = " + getSize());
				if(getSize() > 1) {
					shiftIoBoundsToFront();
				}
			}
		}
		
		startExecution();

	}	
	
	public void startExecution() {
		System.out.println("FcFS tryin to start thread!!!!!!!!");
		
		if(prevQueue != null) {
			int size = 0;
			if(prevQueue instanceof RRQueue) {
				size = ((RRQueue)(prevQueue)).getSize();
			}else if(prevQueue instanceof FCFSQueue) {
				size = ((FCFSQueue)(prevQueue)).getSize();
			}else if(prevQueue instanceof SJFQueue) {
				size = ((SJFQueue)(prevQueue)).getSize();
			}else if(prevQueue instanceof SRTFQueue) {
				size = ((SRTFQueue)(prevQueue)).getSize();
			}else if(prevQueue instanceof NonPQueue) {
				size = ((NonPQueue)(prevQueue)).getSize();
			}else if(prevQueue instanceof PQueue) {
				size = ((PQueue)(prevQueue)).getSize();
			}
			
			if(size > 0) return;
		}
		
		System.out.println("size = " + getSize());
		if(getSize() > 0) {
			//resume();
			System.out.println("Gora barbie");
			running = true;
			prevQueueDone = 1;
		}
	}
	
	public void stopExecution() {		
		prevQueueDone = 0;
		
		/*
		 * Conditional below determines if this Queue is preempted
		 * by a higher priority queue.
		 * 
		 * It indicates that this queue was executing when
		 * a new process arrive at a higher queue, thus preempting the process.
		 * 
		 * */
		if(currProcess != null && currProcess.getPrevBurstPreempted()-currProcess.getBurstTime() > 0) {
			long timeNow = Scheduler.clockTime; 
			prevTimeQuantum = timeNow;
			
			// Indicates that current processes is preempted.
			currProcess.setPreempted();
			currProcess.setTimePreempted(timeNow);
			currProcess.setEndTime(timeNow);
			currProcess.preemptedFlag = true;
			prevProcess = currProcess;
		
			int burstExecuted = (int) (currProcess.getEndTime()-currProcess.getStartTime());
			System.out.println("| burstExecuted = " + burstExecuted);
			
			currProcess.setPrevBurstPreempted(currProcess.getBurstTime());
			GanttChart.addExecutingProcess(level, currProcess.getId(), burstExecuted, (int)timeNow, SchedulingAlgorithm.RR);

		}
	}
	
	Thread FCFSThread = new Thread(){		
		public void run(){
			while(running){
				if(prevQueueDone == 1 && peekHead() != null){									
					currProcess = peekHead();
					if(timeStart < 0) {
						if(timeEnd != 0){						
							timeStart = timeEnd;
						}else{
							timeStart = Scheduler.clockTime;
						}
					}
					
					//currProcess.setStartTime(timeStart);
					if(currProcess.getResponseTime() < 0) {				
						long startTime = Scheduler.clockTime;				
						currProcess.setStartTime(startTime);
						currProcess.setFirstStartTime(startTime);
						currProcess.setResponseTime(startTime-currProcess.getArrivalTime());
						System.out.println("setting p" + currProcess.getId() + " RT = " + currProcess.getResponseTime());
						
						/*// TODO: Make sure to set prevProcess = something to all instances where you put preemption
						
						if(prevProcess != null && prevProcess.preemptedFlag) {
							long startTime = prevProcess.getTimePreempted(prevProcess.getTimesPreempted()-1);
							currProcess.setStartTime(startTime);
							currProcess.setFirstStartTime(startTime);
							currProcess.setResponseTime(startTime-currProcess.getArrivalTime());
						}else {
							long startTime = Scheduler.clockTime;
							currProcess.setStartTime(startTime);
							currProcess.setFirstStartTime(startTime);
							currProcess.setResponseTime(startTime-currProcess.getArrivalTime());
						}*/
					}
					
					if(currProcess.preemptedFlag) {
						long timeStart = Scheduler.clockTime;
						
						System.out.println("resuming p" + currProcess.getId() + "..... timeStart = " + timeStart);
						
						currProcess.setStartTime(timeStart);
						currProcess.setTimeResumed(timeStart);						
						currProcess.preemptedFlag = false;
					}
					
					long timeNow = Scheduler.clockTime;	
					if(prevTime < timeNow) {
						System.out.println("level = " + level + " exec p" + currProcess.getId() + " timeStart = " + currProcess.getStartTime() + " timeNow = " + timeNow);
						
						int lapse = (int)(timeNow - prevTime);
						int burstLeft = currProcess.getBurstTime() - lapse;					
						currProcess.setBurstTime(burstLeft);
						
						/*
						 * Conditional immediately below relinquishes IOBound process
						 * for IO operation.
						 * 
						 * */
						if(currProcess instanceof IOBoundProcess) {
							int burstExecuted = ((IOBoundProcess) currProcess).getIoCpuTime();
							System.out.println("|instanceof IO: timeNow = " + timeNow + " quantum+executed = " + (prevTimeQuantum + burstExecuted));
							if(timeNow == prevTimeQuantum + burstExecuted){
								System.out.println("Just before quantum expires....");
								
								currProcess.preemptedFlag = true;
								prevProcess = currProcess;							
								
								GanttChart.addExecutingProcess(level, currProcess.getId(), burstExecuted, (int)timeNow, SchedulingAlgorithm.RR);
								currProcess.setArrivalTime(timeNow + ((IOBoundProcess)(currProcess)).getIoSpeed());
								
								if(burstLeft > 0){						
									insertToReadyQueue((IOBoundProcess)currProcess);
									dequeue();
								}
							
								prevTimeQuantum = timeNow;
							}							
						}
						
						if(burstLeft <= 0){		
							currProcess.setWaitTimePreemptive();	
							System.out.println("==? p" + currProcess.getId() + " burst = " + currProcess.getBurstTime());
							
							//GanttChart.addExecutingProcess(level, currProcess.getId(), currProcess.getPrevBurstPreempted(), (int)timeNow, SchedulingAlgorithm.RR);
							
							dequeue();														
							System.out.println("p" + currProcess.getId() + " Done executing. prevBurstPreempted = " + currProcess.getPrevBurstPreempted());
							
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
						
						if(level == Scheduler.getMaxLevelOfQueues()) {
							System.out.println("Allprocessdon size zero.. stopping simulation...");
							simulationDone(processList);							
						}/*else {
							System.out.println("allDone = " + allProcessesDone + " Sched.proc.size = " + Scheduler.processes.size());
						}*/
					}
				}
				
			}
		}		
	};	
	
	private void shiftIoBoundsToFront() {
		array.givePriorityToIoBounds();
	}

	public void simulationDone(){
		GanttChart.simulationDone(this);
	}

	public void preemptQueue() {
		System.out.println("Stopping FCFS thread...");
		stopThread();
	}
	
	public void setPrevQueue(Object prevQueue) {
		this.prevQueue = prevQueue;
	}
	
	public void setNextQueue(Object nextQueue){
		this.nextQueue = nextQueue;
	}
	
	public Object getNextQueue(){
		return nextQueue;
	}
	
	public Object getPrevQueue() {
		return prevQueue;
	}
}

package queues;
import constants.SchedulingAlgorithm;
import datastructure.PseudoArray;
import gui.GanttChart;
import process.CPUBoundProcess;
import scheduler.Scheduler;

public class FCFSQueue extends Queue{
		
	private PseudoArray array = new PseudoArray(20);
	private CPUBoundProcess currProcess;	
	private boolean running = false;
	private int numOfProcesses;
	
	private byte allProcessesDone = 1;
	
	private long timeStart = -1;
	private long timeEnd;
	
	byte level = -1;
	private Object prevQueue;
	private Object nextQueue;
	
	public byte prevQueueDone = 1;
	
	public FCFSQueue(int level){
		this.level = (byte)level;
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
	
	public void startThread(){
		running = true;
		FCFSThread.start();
	}
	
	public void stopThread(){
		FCFSThread.interrupt();
		running = false;		
	}	
	
	public void enqueue(CPUBoundProcess newProcess){		
		array.add(newProcess);		
		allProcessesDone = 0;		
		numOfProcesses--;
		
		/* 
		 * Start executing (this queue) if 
		 * previous higher priority queue 
		 * is NOT executing or null.
		 * 
		 * */
		if(prevQueue != null) {
			System.out.println("level = " + level + " prevQueue is NOT NULL.");
			System.out.println("    instanceof " + prevQueue);
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
			
			if(queueSize <= 0) {
				System.out.println("Queue size = " + queueSize);
				startExecution();
			}else {
				stopExecution();
			}
			
		}else {
			System.out.println("level = " + level + " prevQueue is NULL.");
			startExecution();
		}
		
		System.out.print("level = " + level + " ");
		array.printContents();
	}	
	
	public void startExecution() {
		System.out.println("level = " + level + " starting exec...");
		if(prevQueue != null) {
			System.out.println("     PrevQueue Not null");
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
		
		if(getSize() > 0) {
			System.out.println("   preQueue is null and size > 0");
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
		 * We update the prevQuantumTime to the time the process is preempted
		 * so the timer starts counting at that time.
		 * 
		 * */
		if(currProcess != null  && currProcess.getPrevBurstPreempted()-currProcess.getBurstTime() > 0) {
			prevTimeQuantum = Scheduler.clockTime; 	
		
			GanttChart.addExecutingProcess(level, currProcess.getId(), currProcess.getPrevBurstPreempted()-currProcess.getBurstTime(), SchedulingAlgorithm.FCFS);
			GanttChart.addLastCompletionTime(level, SchedulingAlgorithm.FCFS);
			currProcess.setPrevBurstPreempted(currProcess.getBurstTime());
		}
		
		System.out.println("level = " + level + " stopping execution...");
		System.out.println("****updated prevTimeQuantum = " + prevTimeQuantum);
	}
	
	public CPUBoundProcess dequeue(){
		CPUBoundProcess prc = array.remove();											
		return prc;
	}
	
	public CPUBoundProcess peekHead(){
		return array.getHead().getValue(); 
	}
	
	public CPUBoundProcess peekTail(){
		return array.get(getSize()-1).getValue(); 		
	}
	
	public int getSize(){
		return array.getSize();
	}
	
	Thread FCFSThread = new Thread(){		
		public void run(){
			while(running){
				if(getSize() > 0 && prevQueueDone == 1 && (currProcess = peekHead()) != null){									
					//currProcess = peekHead();
					if(timeStart < 0) {
						if(timeEnd != 0){						
							timeStart = timeEnd;
						}else{
							timeStart = Scheduler.clockTime;
						}
					}
					currProcess.setStartTime(timeStart);
					if(currProcess.getResponseTime() < 0) {
						currProcess.setResponseTime(timeStart-currProcess.getArrivalTime());
					}
					
					long timeNow = Scheduler.clockTime;	
					if(prevTime < timeNow) {
						System.out.println("level = " + level + " exec p" + currProcess.getId() + " timeNow = " + timeNow);
						int lapse = (int)(timeNow - prevTime);
						int burstLeft = currProcess.getBurstTime() - lapse;					
						currProcess.setBurstTime(burstLeft);
						
						if(burstLeft <= 0){								
							dequeue();									
							GanttChart.addExecutingProcess(level, currProcess.getId(), currProcess.getPrevBurstPreempted(), SchedulingAlgorithm.FCFS);

							System.out.println("p" + currProcess.getId() + " Done executing. prevBurstPreempted = " + currProcess.getPrevBurstPreempted());
							timeEnd = Scheduler.clockTime;
							prevTimeQuantum = timeNow;
							timeStart = -1;							
						}					
					}
					prevTime = timeNow;
								
					//timeEnd = Scheduler.clockTime;			
					//currProcess.setWaitTimeNonPreemptive();
					
				}else{				
					if (allProcessesDone == 0 && getSize() == 0){
						GanttChart.addLastCompletionTime(level, SchedulingAlgorithm.FCFS);		
						allProcessesDone = 1;		
						
						if(level == Scheduler.getMaxLevelOfQueues()) {
							simulationDone();
						}
					}	
					
					/*
					if(numOfProcesses <= 0){
						int s = Scheduler.processes.length;
						Process[] p = Scheduler.processes;
						
						double totalRT = 0;
						double totalWT = 0;
						double totalTT = 0;
						
						for(int i = 0; i < s; i++) {
							GanttChart.addTimesInformation(p[i].getId(), p[i].getResponseTime(), p[i].getWaitTime(), p[i].getTurnaroundTime());
							totalRT += p[i].getResponseTime();
							totalWT += p[i].getWaitTime();
							totalTT += p[i].getTurnaroundTime();
						}
						
						GanttChart.addTimeAverages(totalRT/s, totalWT/s, totalTT/s);
					}*/
				}
			}
		}		
	};	

	public void simulationDone(){
		GanttChart.simulationDone(this);
	}
	
	public void setNumberOFProcesses(int length) {
		this.numOfProcesses = length;
	}

	public void restart() {
		running = true;
	}

	public void preemptQueue() {
		System.out.println("Stopping FCFS thread...");
		stopThread();
	}
}

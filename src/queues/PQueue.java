package queues;
import constants.SchedulingAlgorithm;
import datastructure.PseudoArray;
import gui.GanttChart;
import process.CPUBoundProcess;
import scheduler.Scheduler;

public class PQueue extends Queue{
		
	private PseudoArray array = new PseudoArray(20);
	private CPUBoundProcess currProcess;
	private CPUBoundProcess prevProcess;
	private boolean running = false;
	private boolean preempted = false;
	private int numOfProcesses;
	
	private Object prevQueue;
	private Object nextQueue;
	
	private byte level = -1;
	private byte allProcessesDone = 1;
	public byte prevQueueDone = 1;
	
	public PQueue(int level){
		this.level = (byte)level;
	}
	
	public void setPrevQueue(Object prevQueue) {
		this.prevQueue = prevQueue;
		if(prevQueue == null) {
			prevQueueDone = 1;
		}
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
		PThread.start();
	}
	
	public void stopThread(){
		PThread.interrupt();
		running = false;
		reset();
	}
	
	private void reset(){
		currProcess = null;
		prevProcess = null;
		running = false;
		preempted = false;
		numOfProcesses = 0;
		allProcessesDone = 1;	
		prevTime = 0;
	}
	
	public void enqueue(CPUBoundProcess newProcess){
		numOfProcesses--;
		deterMineIfToPreempt(newProcess);	
		array.add(newProcess);				
		sortPriority();
		allProcessesDone = 0;		
		
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
			
			if(prevQueue instanceof RoundRobin) {
				queueSize = ((RoundRobin)(prevQueue)).getSize();		
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
				startExecution();
			}else {
				stopExecution();
			}
			
		}else {
			startExecution();
		}
		
		System.out.print("level = " + level + " ");
		array.printContents();
	}	
	
	private void deterMineIfToPreempt(CPUBoundProcess newProcess) {
		if(currProcess != null){
			int currPriority = currProcess.getPriority();
			int newPriority = newProcess.getPriority();			
			if(currPriority > newPriority){		
				preempt(newProcess);
			}
		}
	}

	private void preempt(CPUBoundProcess newProcess) {		
		preempted = true;
		
		int burstNeeded = currProcess.getBurstNeeded();
		int burstTime = currProcess.getBurstTime(); 
		if(burstNeeded-burstTime > 0){
			int prevBurst = currProcess.getPrevBurstPreempted();
			int burst = currProcess.getBurstTime();
			if(prevBurst-burst == 0){
				prevProcess = null;				
			}else{
				prevProcess = currProcess;
			}
		}else{
			prevProcess = null;
			currProcess.setResponseTime(-1);
		}		
		
		long timeNow = Scheduler.clockTime;
		currProcess.setTimePreempted(timeNow);
		currProcess.preemptedFlag = true;
		
		if(currProcess.getResponseTime() < 0) {
			currProcess.setStartTime(timeNow);
			currProcess.setResponseTime(timeNow-newProcess.getArrivalTime());
		}
	}

	public CPUBoundProcess dequeue(){
					
		CPUBoundProcess prc = array.remove();											
		return prc;
	}
	
	public void sortPriority(){
		array.sortPriority();
	}
	
	public CPUBoundProcess peekHead(){
		return array.getHead().getValue(); 
	}
	
	/*public CPUBoundProcess peekTail(){
		return array.get(getSize()-1).getValue(); 		
	}*/
	
	public int getSize(){
		return array.getSize();
	}
	
	public void startExecution() {
		if(prevQueue != null) {
			int size = 0;
			size = ((RoundRobin)(prevQueue)).getSize();
			
			/*}else if(prevQueue instanceof FCFSQueue) {
				size = ((FCFSQueue)(prevQueue)).getSize();
			}else if(prevQueue instanceof SJFQueue) {
				size = ((SJFQueue)(prevQueue)).getSize();
			}else if(prevQueue instanceof SRTFQueue) {
				size = ((SRTFQueue)(prevQueue)).getSize();
			}else if(prevQueue instanceof NonPQueue) {
				size = ((NonPQueue)(prevQueue)).getSize();
			}else if(prevQueue instanceof PQueue) {
				size = ((PQueue)(prevQueue)).getSize();
			}*/
			
			if(size > 0) return;
		}
		
		if(getSize() > 0) {		
			prevQueueDone = 1;
		}
	}
	
	public void stopExecution() {		
		System.out.println("level = " + level + " stopping execution...");
		prevQueueDone = 0;
		
		/*
		 * Code below determines if a process needs to be promoted. 
		 * If yes, promote then.
		 * 
		 * */
		
		if(currProcess != null) {
			System.out.println("level = " + level + " p" + currProcess.getId() + " was executing when queue preempted.");
			System.out.println("    burstExecuted = " + (currProcess.getPrevBurstPreempted()-currProcess.getBurstTime()));
			if(currProcess.getBurstTime() > 0 && (currProcess.getPrevBurstPreempted()-currProcess.getBurstTime()) > 0) {
				prevTimeQuantum = Scheduler.clockTime; 
				System.out.println("    p" + currProcess.getId() + " should be promoted.");
				promote((currProcess.getPrevBurstPreempted()-currProcess.getBurstTime()));
			}else {
				System.out.println("    p" + currProcess.getId() + " need not be promoted.");
			}
		}
		System.out.println("****updated prevTimeQuantum = " + prevTimeQuantum);
	}
	
	/*
	 * Promotes the preempted process to an
	 * immediate higher priority queue.
	 * 
	 * */
	private void promote(int timeElapsed) {
		if(prevQueue != null) {
			
			/*
			 * We might not need to implement promotion up to other scheduling
			 * algorithms other than RR. This is because the other scheduling algorithms
			 * cannot demote. Consequently, this means that any queue following the 
			 * non-demoting queue don't execute any process at all and therefore have nothing
			 * to promote. 
			 * 
			 * This makes sense because if a process needs to go one queue up (to be promoted), it needs
			 * to be at least one queue down first.
			 * 
			 * */
			if(prevQueue instanceof RoundRobin) {
				System.out.println("Promoted p" + currProcess.getId());		
				prevProcess = currProcess;
				currProcess = null;
				
				if(prevProcess != null){
					int burstPreempted = prevProcess.getBurstTime();
					int prevBurstPreempted = prevProcess.getPrevBurstPreempted();
					System.out.println("   burstPreempted = " + burstPreempted + " exec = " + (prevBurstPreempted-burstPreempted));
					prevProcess.setPrevBurstPreempted(burstPreempted);
					//GanttChart.addExecutingProcess(level, prevProcess.getId(), (prevBurstPreempted-burstPreempted), SchedulingAlgorithm.SRTF);							
				}
				((RoundRobin)(prevQueue)).enqueue(dequeue());
			}
		}
	}
	
	Thread PThread = new Thread(){				
		public void run(){
			while(running){									
				if(getSize() > 0 && prevQueueDone == 1 && (currProcess = peekHead()) != null){
					
					long timeNow = Scheduler.clockTime;
					
					if(!preempted){
												
						if(currProcess.preemptedFlag) {
							currProcess.setTimeResumed(timeNow);
							currProcess.preemptedFlag = false;
						}
						
					}else {
						
						if(prevProcess != null){
							int burstPreempted = prevProcess.getBurstTime();
							prevProcess.setPrevBurstPreempted(burstPreempted);
							//GanttChart.addExecutingProcess(level, prevProcess.getId(), prevProcess.getBurstNeeded()-burstPreempted, SchedulingAlgorithm.PRIO);
						}
						
						preempted = false;
					}
					
					if(currProcess.getResponseTime() < 0) {
						//System.out.println("p" + currProcess.getId() + "; start = " + timeNow);
						currProcess.setStartTime(timeNow);
						currProcess.setResponseTime(timeNow-currProcess.getArrivalTime());
					}
					
					if(prevTime < timeNow){
						long lapse = timeNow - prevTime;
						int burstLeft = (int)(currProcess.getBurstTime() - lapse);					
						currProcess.setBurstTime(burstLeft);														
						
						if(currProcess.getBurstTime() <= 0){
							currProcess.setWaitTimePreemptive();
							prevTimeQuantum = timeNow;
							dequeue();
							
							//GanttChart.addExecutingProcess(level, currProcess.getId(), currProcess.getPrevBurstPreempted(), SchedulingAlgorithm.PRIO);
							System.out.println("p" + currProcess.getId() + " Done executing.");
						}													
					}
					preempted = false;
					prevTime = timeNow;
					
				}else{										
				
					/* 
					 * Add the last completion time only if
					 * allProcessesDone = 0 (execution not done) 
					 * but there may be no more processes in 
					 * the queue to execute.
					 * 
					 * */
					
					if (allProcessesDone == 0 && getSize() == 0){
						GanttChart.addLastCompletionTime(level, SchedulingAlgorithm.PRIO);		
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
}

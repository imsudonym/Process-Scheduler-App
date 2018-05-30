package queues;
import Process.CPUBoundProcess;
import constants.SchedulingAlgorithm;
import ctrl.Scheduler;
import gui.GanttChart;
import utils.PseudoArray;

public class SRTFQueue extends Queue{
		
	private PseudoArray array = new PseudoArray(20);
	private CPUBoundProcess currProcess;
	private CPUBoundProcess prevProcess;
	private boolean running = false;
	private boolean preempted = false;
	private int numOfProcesses;
	private byte allProcessesDone = 1;	
	
	byte level = -1;
	private Object prevQueue;
	private Object nextQueue;
	
	public byte prevQueueDone = 1;
	
	public SRTFQueue(int level){
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
		SRTFThread.start();
	}
	
	public void stopThread(){
		SRTFThread.interrupt();
		running = false;
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
		deterMineIfToPreempt(newProcess);
		array.add(newProcess);			
		sortSJF();
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
				System.out.println("I'm here..");
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
	
	private void deterMineIfToPreempt(CPUBoundProcess newProcess) {
		if(currProcess != null){
			long currBurst = currProcess.getBurstTime();
			long newBurst = newProcess.getBurstNeeded();
			if(currBurst > newBurst){
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
		
		if(newProcess.getResponseTime() < 0) {
			newProcess.setStartTime(timeNow);
			newProcess.setResponseTime(timeNow-newProcess.getArrivalTime());
		}
	}

	public CPUBoundProcess dequeue(){					
		CPUBoundProcess prc = array.remove();											
		return prc;
	}
	
	public void sortSJF(){
		array.sortSJF();
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
	
	public void startExecution() {
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
		
		if(getSize() > 0) {
			//System.out.println("level = " + level + " starting execution...");
			//restart();
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
				System.out.println("    p" + currProcess.getId() + " should be promoted.");
				prevTimeQuantum = Scheduler.clockTime; 	
				promote((currProcess.getPrevBurstPreempted()-currProcess.getBurstTime()));
			}else {
				System.out.println("    p" + currProcess.getId() + " need not be promoted.");
			}
		}
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
			if(prevQueue instanceof RRQueue) {
				System.out.println("Promoted p" + currProcess.getId());		
				prevProcess = currProcess;
				currProcess = null;
				
				if(prevProcess != null){
					int burstPreempted = prevProcess.getBurstTime();
					int prevBurstPreempted = prevProcess.getPrevBurstPreempted();
					System.out.println("   burstPreempted = " + burstPreempted + " exec = " + (prevBurstPreempted-burstPreempted));
					prevProcess.setPrevBurstPreempted(burstPreempted);
					GanttChart.addExecutingProcess(level, prevProcess.getId(), (prevBurstPreempted-burstPreempted), SchedulingAlgorithm.SRTF);							
				}
				((RRQueue)(prevQueue)).enqueue(dequeue());
			}
		}
	}

	Thread SRTFThread = new Thread(){				
		public void run(){
			while(running){
				if(getSize() > 0 &&  prevQueueDone == 1 && (currProcess = peekHead()) != null){	
					
					long timeNow = Scheduler.clockTime;
					
					if(!preempted){						
						
						if(currProcess != null && currProcess.preemptedFlag) {
							currProcess.setTimeResumed(timeNow);
							currProcess.preemptedFlag = false;
						}
						
					}else{
												
						if(prevProcess != null){
							int burstPreempted = prevProcess.getBurstTime();
							System.out.println("burstPreempted = " + burstPreempted);
							prevProcess.setPrevBurstPreempted(burstPreempted);
							GanttChart.addExecutingProcess(level, prevProcess.getId(), prevProcess.getBurstNeeded()-burstPreempted, SchedulingAlgorithm.SRTF);
						}
						
						preempted = false;
					}
					
					if(currProcess.getResponseTime() < 0) {
						currProcess.setStartTime(timeNow);
						currProcess.setResponseTime(timeNow-currProcess.getArrivalTime());
					}
					
					if(prevTime < timeNow){
						System.out.println("level = " + level + " executing p" + currProcess.getId() + " prevTime = " + prevTime + " timeNow = " + timeNow);
						long lapse = timeNow - prevTime;
						int burstLeft = (int)(currProcess.getBurstTime() - lapse);					
						currProcess.setBurstTime(burstLeft);		
						
						if(currProcess.getBurstTime() <= 0){
							currProcess.setWaitTimePreemptive();
							prevTimeQuantum = timeNow;
							dequeue();						
							
							array.printContents();
							GanttChart.addExecutingProcess(level, currProcess.getId(), currProcess.getPrevBurstPreempted(), SchedulingAlgorithm.SRTF);					
							
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
						System.out.println("level = " + level + " we called addlastcompletion");
						GanttChart.addLastCompletionTime(level, SchedulingAlgorithm.SRTF);		
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

package queues;
import constants.QueueType;
import constants.SchedulingAlgorithm;
import ctrl.Scheduler;
import gui.GanttChart;
import utils.Process;
import utils.PseudoArray;

public class RRQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;
	private boolean running = false;
	private int numOfProcesses;
	private int quantum = 0;
	private static long prevTime;	
	private long timeStart;
	private long timeEnd;
	
	private static long prevTimeQuantum;
	
	private Process prevProcess;
	private byte level = -1;
	
	private Object prevQueue;
	private Object nextQueue = null;
	
	private int prevQueueType;
	private int nextQueueType;
	
	public byte allProcessesDone = 1;
	public byte prevQueueDone = 1;

	public boolean executing = false;
	
	public RRQueue(int level, int quantum){
		this.level = (byte)level;
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
	
	public void enqueue(Process newProcess){
		System.out.println("level = " + level + " enter p" + newProcess.getId());
		array.add(newProcess);
		allProcessesDone = 0;
		numOfProcesses--;
		
		if(prevQueue != null) {
			System.out.println("prevQueue was not null");
			System.out.println(prevQueue);
			if(prevQueue instanceof RRQueue) {
				System.out.println("============");
				if(((RRQueue)(prevQueue)).allProcessesDone == 1) {
					System.out.println("	apparently all processes were done.");
					startExecution();
				}else {
					stopExecution();
				}
				System.out.println("	level = " + level + " started next execution...");
			}
		}else {
			System.out.println("prevQueue was null");
			startExecution();
		}/*
		if(nextQueue != null) {
			if(nextQueue instanceof FCFSQueue)
				//((FCFSQueue)(nextQueue)).startExecution();
			if(nextQueue instanceof SJFQueue) 
				//((FCFSQueue)(nextQueue)).startExecution();
			if(nextQueue instanceof SRTFQueue)
				//((SRTFQueue)(nextQueue)).startExecution();
			if(nextQueue instanceof NonPQueue)
				//((NonPQueue)(nextQueue)).startExecution();
			if(nextQueue instanceof PQueue)
				//((PQueue)(nextQueue)).startExecution();
			if(nextQueue instanceof RRQueue) {
				((RRQueue)(nextQueue)).stopExecution();
				System.out.println("started next execution...");
			}
		}*/
		simulationDone();
	}
	
	public void reenqueue(Process newProcess){		
		array.add(newProcess);		
		allProcessesDone = 0;		
	}	
	
	public Process dequeue(){
					
		Process prc = array.remove();											
		return prc;
	}
	
	public Process peekHead(){
		if(array.getHead() == null) {
			return null;
		}else {
			return array.getHead().getValue();
		}
	}
	
	public Process peekTail(){
		return array.get(getSize()-1).getValue(); 		
	}
	
	public int getSize(){
		return array.getSize();
	}
	
	public void startExecution() {
		System.out.println("level = " + level + " starting execution...");
		prevQueueDone = 1;
	}
	
	public void stopExecution() {
		System.out.println("	level = " + level + " stopping execution...");
		prevQueueDone = 0;
	}
	
	Thread RRThread = new Thread(){				
		private int prevBurstLeft = -1;

		public void run(){
			while(running){
				
				if(getSize() > 0 &&  prevQueueDone == 1){
					currProcess = peekHead();
					if(timeStart < 0){
						if(timeEnd != 0)						
							timeStart = timeEnd;
						else
							timeStart = Scheduler.clockTime;						
					}					
					
					executing = true;
					
					if(currProcess != null && currProcess.getResponseTime() < 0) {
						if(prevProcess != null && prevProcess.preemptedFlag) {
							long startTime = prevProcess.getTimePreempted(prevProcess.getTimesPreempted()-1);
							currProcess.setStartTime(startTime);
							currProcess.setResponseTime(startTime-currProcess.getArrivalTime());
						}else {
							currProcess.setStartTime(timeStart);
							currProcess.setResponseTime(timeStart-currProcess.getArrivalTime());
						}
					}
					
					if(currProcess.preemptedFlag) {
						currProcess.setTimeResumed(Scheduler.clockTime);
						currProcess.preemptedFlag = false;
					}
					
					long timeNow = Scheduler.clockTime;					
					if(prevTime < timeNow){
						System.out.println("executing p" + currProcess.getId() + " prevTime = " + prevTime + " timeNow = " + timeNow);		
						int lapse = (int)(timeNow - prevTime);
						int burstLeft = currProcess.getBurstTime() - lapse;					
						currProcess.setBurstTime(burstLeft);																	
						
						System.out.println(timeNow + " == " +  (prevTimeQuantum + quantum));
						if(timeNow == prevTimeQuantum + quantum){
							currProcess.setPreempted();
							currProcess.setTimePreempted(timeNow);
							currProcess.preemptedFlag = true;

							prevProcess = currProcess;
							
							GanttChart.addExecutingProcess(level, currProcess.getId(), quantum, SchedulingAlgorithm.RR);
							
							if(burstLeft > 0){																
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
							System.out.println("burstLeft = " + burstLeft);
							currProcess.setWaitTimePreemptive();
							int s = currProcess.getTimesPreempted();
							
							if(currProcess.getPrevBurstPreempted() < quantum){						
								GanttChart.addExecutingProcess(level, currProcess.getId(), currProcess.getPrevBurstPreempted(), SchedulingAlgorithm.RR);								
							}
							
							dequeue();													
							System.out.println("p" + currProcess.getId() + " Done executing.");
							timeEnd = Scheduler.clockTime;
							prevTimeQuantum = timeNow;
							timeStart = -1;							
						}													
					}					
					prevTime = timeNow;					
					
				}else{										
				
					//System.out.println("Hahahaha");
					if (allProcessesDone == 0){
						GanttChart.addLastCompletionTime(level, SchedulingAlgorithm.RR);		
						allProcessesDone = 1;
						if(nextQueue != null) {
							if(nextQueue instanceof RRQueue) {
								((RRQueue)(nextQueue)).startExecution();
								System.out.println("   I was called. Expect to see 2nd queue start exec.");
							}
						}
					}			
					
					//System.out.println("numOfProcess = " + numOfProcesses);
					if(numOfProcesses <= 0){						
						if(nextQueue != null) {
							if(nextQueue instanceof RRQueue) {
								((RRQueue)(nextQueue)).startExecution();
							}
						}
						
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
					}
				}
			}
		}
	};
	
	public void simulationDone(){

		RRQueue q = (RRQueue) Scheduler.queues[1];
		q.array.printContents();
		
		//GanttChart.simulationDone();
		/*
		if(nextQueueType == QueueType.FCFS) {
			if(((FCFSQueue)nextQueue).getSize() > 0)
				((FCFSQueue)nextQueue).startThread();
		}else if(nextQueueType == QueueType.SJF){
			if(((SJFQueue)nextQueue).getSize() > 0)
				((SJFQueue)nextQueue).startThread();
		}else if(nextQueueType == QueueType.SRTF){
			if(((SRTFQueue)nextQueue).getSize() > 0)
				((SRTFQueue)nextQueue).startThread();
		}else if(nextQueueType == QueueType.NP) {
			if(((NonPQueue)nextQueue).getSize() > 0)
				((NonPQueue)nextQueue).startThread();
		}else if(nextQueueType == QueueType.P) {
			if(((PQueue)nextQueue).getSize() > 0)
				((PQueue)nextQueue).startThread();
		}else if(nextQueueType == QueueType.RR){
			if(((RRQueue)nextQueue).getSize() > 0)
				((RRQueue)nextQueue).startThread();
		}*/
	}
	
	protected void retain() {
		enqueue(dequeue());
	}

	protected void demote(Process process) {
		
		if(nextQueue == null) return;
		
		System.out.println("level = " + level + " demote p" + process.getId() + " burstLeft = " + process.getBurstTime() + " size = " + getSize() + " np = " + numOfProcesses);
		
		if(nextQueue instanceof FCFSQueue) {
			((FCFSQueue)nextQueue).enqueue(process);
		}else if(nextQueue instanceof SJFQueue) {
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
	
	/*
	public void preemptLowerQueue() {
		if(nextQueue == null) return;
		if(nextQueueType == QueueType.FCFS) {
			((FCFSQueue)nextQueue).preemptQueue();
		}/*else if(nextQueueType == QueueType.SJF) {
			((SJFQueue)nextQueue).preemptQueue();
		}else if(nextQueueType == QueueType.SRTF) {
			((SRTFQueue)nextQueue).preemptQueue();
		}else if(nextQueueType == QueueType.NP) {
			((NonPQueue)nextQueue).preemptQueue();
		}else if(nextQueueType == QueueType.P) {
			((PQueue)nextQueue).preemptQueue();
		}else if(nextQueueType == QueueType.RR) {
			((RRQueue)nextQueue).preemptQueue();
		}
	}

	private void preemptQueue() {
		stopExecution();
		if(executing) {
			System.out.println("p" + currProcess.getId() + " preempted.");
			enqueue(dequeue());
			executing = false;
		}
	}*/

	public void setNumberOFProcesses(int length) {
		this.numOfProcesses = length;
	}
	
	public void restart() {
		running = true;
	}
	
	public void setPrevQueue(Object prevQueue) {
		this.prevQueue = prevQueue;
		if(prevQueue == null) prevQueueDone = 1;
		System.out.println("prevQueueDone = " + prevQueueDone);
		if(prevQueue instanceof FCFSQueue) {
			prevQueueType = QueueType.FCFS;
		}else if(prevQueue instanceof RRQueue) {
			prevQueueType = QueueType.RR;
		}else if(prevQueue instanceof SJFQueue) {
			prevQueueType = QueueType.SJF;
		}else if(prevQueue instanceof SRTFQueue) {
			prevQueueType = QueueType.SRTF;
		}else if(prevQueue instanceof NonPQueue) {
			prevQueueType = QueueType.NP;
		}else if(prevQueue instanceof PQueue) {
			prevQueueType = QueueType.P;
		}
	}
	
	public void setNextQueue(Object nextQueue){
		//System.out.println("Setting next queues");
		this.nextQueue = nextQueue;
		
		if(nextQueue instanceof FCFSQueue) {
			nextQueueType = QueueType.FCFS;
			//System.out.println("	next queue = FCFS");
		}else if(nextQueue instanceof RRQueue) {
			nextQueueType = QueueType.RR;
			//System.out.println("	next queue = RR");
		}else if(nextQueue instanceof SJFQueue) {
			nextQueueType = QueueType.SJF;
			System.out.println("	next queue = SJF");
		}else if(nextQueue instanceof SRTFQueue) {
			nextQueueType = QueueType.SRTF;
			System.out.println("	next queue = SRTF");
		}else if(nextQueue instanceof NonPQueue) {
			nextQueueType = QueueType.NP;
			System.out.println("	next queue = NP");
		}else if(nextQueue instanceof PQueue) {
			nextQueueType = QueueType.P;
			System.out.println("	next queue = P");
		}
	}
	
	public Object getNextQueue(){
		return nextQueue;
	}
	
	public Object getPrevQueue() {
		return prevQueue;
	}
	
	public long getQuantum(){
		return quantum;
	}
	
}

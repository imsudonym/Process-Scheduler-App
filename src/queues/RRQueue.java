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
	private byte allProcessesDone = 1;
	private int numOfProcesses;
	private int quantum = 0;
	private long prevTime;	
	private long timeStart;
	private long timeEnd;
	
	private Process prevProcess;
	private byte level = -1;
	
	private Object prevQueue;
	private Object nextQueue = null;
	
	private int prevQueueType;
	private int nextQueueType;
	
	public RRQueue(int level, int quantum){
		this.level = (byte)level;
		this.quantum = quantum;
		//startThread();
	}	
	
	public void setPrevQueue(Object prevQueue) {
		this.prevQueue = prevQueue;
		
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
		System.out.println("Setting next queues");
		this.nextQueue = nextQueue;
		
		if(nextQueue instanceof FCFSQueue) {
			nextQueueType = QueueType.FCFS;
		}else if(prevQueue instanceof RRQueue) {
			nextQueueType = QueueType.RR;
		}else if(prevQueue instanceof SJFQueue) {
			nextQueueType = QueueType.SJF;
		}else if(prevQueue instanceof SRTFQueue) {
			nextQueueType = QueueType.SRTF;
		}else if(prevQueue instanceof NonPQueue) {
			nextQueueType = QueueType.NP;
		}else if(prevQueue instanceof PQueue) {
			nextQueueType = QueueType.P;
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
	
	public void startThread(){
		running = true;
		RRThread.start();
	}
	
	public void stopThread(){
		RRThread.interrupt();
		running = false;
	}
	
	public void enqueue(Process newProcess){		
		array.add(newProcess);
		allProcessesDone = 0;
		numOfProcesses--;
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
		return array.getHead().getValue(); 
	}
	
	public Process peekTail(){
		return array.get(getSize()-1).getValue(); 		
	}
	
	public int getSize(){
		return array.getSize();
	}
	
	Thread RRThread = new Thread(){				
		private long prevTimeQuantum;
		private int prevBurstLeft = -1;

		public void run(){
			while(running){					
				if(getSize() > 0){											
					currProcess = peekHead();
					
					if(timeStart < 0){
						if(timeEnd != 0)						
							timeStart = timeEnd;
						else
							timeStart = Scheduler.clockTime;						
					}					
					
					if(currProcess.getResponseTime() < 0) {
						if(prevProcess != null && prevProcess.preemptedFlag) {
							long startTime = prevProcess.getTimePreempted(prevProcess.getTimesPreempted()-1);
							currProcess.setStartTime(startTime);
							currProcess.setResponseTime(startTime-currProcess.getArrivalTime());
						}else {
							currProcess.setStartTime(timeStart);
							currProcess.setResponseTime(timeStart-currProcess.getArrivalTime());
						}
					}
					
					//System.out.println("p" + currProcess.getId() + " startTime = " + currProcess.getStartTime());
					//System.out.println("    response = " + currProcess.getResponseTime());
					
					if(currProcess.preemptedFlag) {
						//System.out.println("p" + currProcess.getId() + " resumed @ " + Scheduler.clockTime);
						currProcess.setTimeResumed(Scheduler.clockTime);
						currProcess.preemptedFlag = false;
					}
					
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					long timeNow = Scheduler.clockTime;					
					if(prevTime < timeNow){
																		
						int lapse = (int)(timeNow - prevTime);
						//System.out.println("p" + currProcess.getId() + " burst: " + currProcess.getBurstTime() + " lapse: " + lapse);
						int burstLeft = currProcess.getBurstTime() - lapse;					
						currProcess.setBurstTime(burstLeft);																	
						
						//System.out.println("prevTimeQuantum: " + prevTimeQuantum + " timeNow: " + timeNow);
						if(timeNow == prevTimeQuantum + quantum){
							//System.out.println("Time na!");
							//System.out.println("   burstLeft: " + burstLeft);
														
							//System.out.println("burstDone: " + quantum);
							//System.out.println("p" + currProcess.getId() + " preempted @ " + timeNow);
							currProcess.setPreempted();
							currProcess.setTimePreempted(timeNow);
							currProcess.preemptedFlag = true;
							demote(currProcess);

							prevProcess = currProcess;
							
							GanttChart.addExecutingProcess(level, currProcess.getId(), quantum, SchedulingAlgorithm.RR);
							
							if(burstLeft > 0){																
								int burstPreempted = currProcess.getBurstTime();
								currProcess.setPrevBurstPreempted(burstPreempted);
								dequeue();
							}
							
							prevTimeQuantum = timeNow;
						}						
						
						if(burstLeft <= 0){		
							currProcess.setWaitTimePreemptive();
							int s = currProcess.getTimesPreempted();
							
							if(currProcess.getPrevBurstPreempted() < quantum){							
								GanttChart.addExecutingProcess(level, currProcess.getId(), currProcess.getPrevBurstPreempted(), SchedulingAlgorithm.RR);								
							}
							dequeue();													
							//System.out.println(" Done executing.");
							timeEnd = Scheduler.clockTime;
							prevTimeQuantum = timeNow;
							timeStart = -1;							
						}													
					}					
					prevTime = timeNow;										
				}else{										
				
					if (allProcessesDone == 0){
						GanttChart.addLastCompletionTime(level, SchedulingAlgorithm.RR);		
						allProcessesDone = 1;						
					}		
					
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
						
						simulationDone();
					}
				}
			}
		}
	};
	
	public void simulationDone(){
		System.out.println("RRQueue done.");
		GanttChart.simulationDone();
		System.out.println("next queue type: " + nextQueueType);
		if(nextQueueType == QueueType.FCFS) {
			System.out.println("Trying to start thread..");
			((FCFSQueue)nextQueue).startThread();
		}else if(nextQueueType == QueueType.SJF){
			((SJFQueue)nextQueue).startThread();
		}else if(nextQueueType == QueueType.SRTF){
			((SRTFQueue)nextQueue).startThread();
		}else if(nextQueueType == QueueType.NP) {
			((NonPQueue)nextQueue).startThread();
		}else if(nextQueueType == QueueType.P) {
			((PQueue)nextQueue).startThread();
		}else if(nextQueueType == QueueType.RR){
			((RRQueue)nextQueue).startThread();
		}
	}
	
	protected void demote(Process process) {
		if(nextQueueType == QueueType.FCFS) {
			((FCFSQueue)nextQueue).enqueue(process);
		}else if(nextQueueType == QueueType.SJF) {
			((SJFQueue)nextQueue).enqueue(process);
		}else if(nextQueueType == QueueType.SRTF) {
			((SRTFQueue)nextQueue).enqueue(process);
		}else if(nextQueueType == QueueType.NP) {
			((NonPQueue)nextQueue).enqueue(process);
		}else if(nextQueueType == QueueType.P) {
			((PQueue)nextQueue).enqueue(process);
		}else if(nextQueueType == QueueType.RR) {
			((RRQueue)nextQueue).enqueue(process);
		}
	}

	public void setNumberOFProcesses(int length) {
		this.numOfProcesses = length;
	}
	
	public void restart() {
		running = true;
	}
}

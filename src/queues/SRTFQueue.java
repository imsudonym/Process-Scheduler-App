package queues;
import constants.SchedulingAlgorithm;
import ctrl.Scheduler;
import gui.GanttChart;
import utils.Process;
import utils.PseudoArray;

public class SRTFQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;
	private Process prevProcess;
	private boolean running = false;
	private boolean preempted = false;
	private int numOfProcesses;
	private byte allProcessesDone = 1;	
	private long prevTime;
	
	public SRTFQueue(){		
		startThread();
	}
	
	private void startThread(){
		running = true;
		SRTFThread.start();
	}
	
	public void stopThread(){
		SRTFThread.interrupt();
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
	
	public void enqueue(Process newProcess){		
		numOfProcesses--;
		deterMineIfToPreempt(newProcess);
		array.add(newProcess);			
		sortSJF();
		allProcessesDone = 0;
	}	
	
	private void deterMineIfToPreempt(Process newProcess) {
		if(currProcess != null){
			long currBurst = currProcess.getBurstTime();
			long newBurst = newProcess.getBurstNeeded();
			if(currBurst > newBurst){
				preempt(newProcess);
			}
		}
	}

	private void preempt(Process newProcess) {				
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

	public Process dequeue(){					
		Process prc = array.remove();											
		return prc;
	}
	
	public void sortSJF(){
		array.sortSJF();
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
	
	Thread SRTFThread = new Thread(){				
		public void run(){
			while(running){																
				if(getSize() > 0 && peekHead() != null){	
					
					long timeNow = Scheduler.clockTime;
					currProcess = peekHead();	
					
					if(!preempted){						
						
						if(currProcess.preemptedFlag) {
							currProcess.setTimeResumed(timeNow);
							currProcess.preemptedFlag = false;
						}
						
					}else{
												
						if(prevProcess != null){
							int burstPreempted = prevProcess.getBurstTime();
							prevProcess.setPrevBurstPreempted(burstPreempted);
							GanttChart.addExecutingProcess(prevProcess.getId(), prevProcess.getBurstNeeded()-burstPreempted, SchedulingAlgorithm.SRTF);							
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
							dequeue();													
							GanttChart.addExecutingProcess(currProcess.getId(), currProcess.getPrevBurstPreempted(), SchedulingAlgorithm.SRTF);
						}													
					}
					preempted = false;
					prevTime = timeNow;
					
				}else{										
				
					if (allProcessesDone == 0){
						GanttChart.addLastCompletionTime(SchedulingAlgorithm.SRTF);		
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
		GanttChart.simulationDone();
	}
	
	public void setNumberOFProcesses(int length) {
		this.numOfProcesses = length;
	}
	
	public void restart() {
		running = true;
	}
}

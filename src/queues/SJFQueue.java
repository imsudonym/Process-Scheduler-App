package queues;
import constants.SchedulingAlgorithm;
import ctrl.Scheduler;
import gui.GanttChart;
import utils.Process;
import utils.PseudoArray;

public class SJFQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;
	private boolean running = false;
	private byte allProcessesDone = 1;
	private int numOfProcesses;
	private long timeStart;
	private long timeEnd;
	
	private byte queuePriority = -1;
	
	public SJFQueue(int priority){
		this.queuePriority = (byte)priority;
		startThread();
	}
	
	private void startThread(){
		running = true;
		SJFThread.start();
	}
	
	public void stopThread(){
		SJFThread.interrupt();
		running = false;
	}
	
	public void enqueue(Process newProcess){		
		array.add(newProcess);		
		sortSJF();
		allProcessesDone = 0;
		numOfProcesses--;
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
	
	Thread SJFThread = new Thread(){				
		public void run(){
			while(running){					
				if(getSize() > 0 && peekHead() != null){									
					currProcess = dequeue();
					
					if(timeEnd != 0){						
						timeStart = timeEnd;
					}else{
						timeStart = Scheduler.clockTime;
					}
					
					currProcess.setStartTime(timeStart);
					if(currProcess.getResponseTime() < 0) {
						currProcess.setResponseTime(timeStart-currProcess.getArrivalTime());
					}
					
					int burstTime = currProcess.getBurstTime();																								
					GanttChart.addExecutingProcess(currProcess.getId(), burstTime, SchedulingAlgorithm.SJF);
					
					while(Scheduler.clockTime != (timeStart + burstTime)){					
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}				
					}
								
					timeEnd = Scheduler.clockTime;
					currProcess.setWaitTimeNonPreemptive();
				
				}else{				
					if (allProcessesDone == 0){
						GanttChart.addLastCompletionTime(SchedulingAlgorithm.SJF);		
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


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
			long currBurst = currProcess.getBurstNeeded();
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
			prevProcess = currProcess;
		}
		currProcess = newProcess;	
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
					if(!preempted){						
						currProcess = peekHead();							
					}else{
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						if(prevProcess != null){
							int burstPreempted = prevProcess.getBurstTime();
							prevProcess.setPrevBurstPreempted(burstPreempted);
							GanttChart.addExecutingProcess(prevProcess.getId(), prevProcess.getBurstNeeded()-burstPreempted, SchedulingAlgorithm.SRTF);
						}
					}
					
					long timeNow = Scheduler.clockTime;
					
					if(prevTime < timeNow){
						long lapse = timeNow - prevTime;
						//System.out.println("p" + currProcess.getId() + " burst: " + currProcess.getBurstTime() + " lapse: " + lapse);
						int burstLeft = (int)(currProcess.getBurstTime() - lapse);					
						currProcess.setBurstTime(burstLeft);		
						//System.out.println("   burstLeft: " + burstLeft);
						//GanttChart.addExecutingProcess(currProcess.getId(), currProcess.getBurstTime(), SchedulingAlgorithm.PRIO);
						
						if(currProcess.getBurstTime() <= 0){
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

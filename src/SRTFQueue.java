
public class SRTFQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;
	private boolean running = false;
	private boolean preempted = false;
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
	}
	
	public void enqueue(Process newProcess){		
		deterMineIfToPreempt(newProcess);
		array.add(newProcess);											
		sortSJF();
		allProcessesDone = 0;
	}	
	
	private void deterMineIfToPreempt(Process newProcess) {
		if(currProcess != null){
			long currBurst = currProcess.getBurstNeeded();
			long newBurst = newProcess.getBurstNeeded();
			System.out.println("curBurst = " + currBurst + " newBurst = " + newBurst);
			if(currBurst > newBurst){
				preempt(newProcess);
			}
		}
	}

	private void preempt(Process newProcess) {				
		preempted = true;
		System.out.println("p" + currProcess.getId() + " = " + currProcess.getBurstTime());
		System.out.println("newProcess: p" + newProcess.getId());
		this.currProcess = newProcess;
		System.out.println("currProcess: p" + currProcess.getId());
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
						System.out.println("Was preempted");
					}
					
					long timeNow = Scheduler.clockTime;
					
					if(prevTime < timeNow){
						long lapse = timeNow - prevTime;
						System.out.println("p" + currProcess.getId() + " burst: " + currProcess.getBurstTime() + " lapse: " + lapse);
						long burstLeft = currProcess.getBurstTime() - lapse;					
						currProcess.setBurstTime(burstLeft);		
						System.out.println("   burstLeft: " + burstLeft);
						//GanttChart.addExecutingProcess(currProcess.getId(), currProcess.getBurstTime(), SchedulingAlgorithm.PRIO);
						
						if(currProcess.getBurstTime() <= 0){
							dequeue();						
							//GanttChart.addExecutingProcess(currProcess.getId(), currProcess.getBurstTime(), SchedulingAlgorithm.PRIO);
							System.out.println("Process p" + currProcess.getId() + " Done executing.");
						}													
					}
					preempted = false;
					prevTime = timeNow;
					
				}else{										
				
					if (allProcessesDone == 0){
						//GanttChart.addLastCompletionTime(SchedulingAlgorithm.PRIO);		
						allProcessesDone = 1;						
					}		
				}				
			}
		}
	};
}

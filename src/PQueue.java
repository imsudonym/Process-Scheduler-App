
public class PQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;
	private Process prevProcess;
	private boolean running = false;
	private boolean preempted = false;
	private byte allProcessesDone = 1;
	private long prevTime;
	
	public PQueue(){		
		startThread();
	}
	
	private void startThread(){
		running = true;
		PThread.start();
	}
	
	public void stopThread(){
		PThread.interrupt();
		running = false;
	}
	
	public void enqueue(Process newProcess){		
		deterMineIfToPreempt(newProcess);	
		array.add(newProcess);				
		sortPriority();
		allProcessesDone = 0;
	}	
	
	private void deterMineIfToPreempt(Process newProcess) {
		if(currProcess != null){
			int currPriority = currProcess.getPriority();
			int newPriority = newProcess.getPriority();			
			if(currPriority > newPriority){		
				preempt(newProcess);
			}
		}
	}

	private void preempt(Process newProcess) {		
		preempted = true;
		System.out.println("p" + currProcess.getId() + " = " + currProcess.getBurstTime());
		prevProcess = currProcess;
		currProcess = newProcess;		
	}

	public Process dequeue(){
					
		Process prc = array.remove();											
		return prc;
	}
	
	public void sortPriority(){
		array.sortPriority();
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
	
	Thread PThread = new Thread(){				
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
						int burstPreempted = prevProcess.getBurstTime();
						prevProcess.setPrevBurstPreempted(burstPreempted);
						GanttChart.addExecutingProcess(prevProcess.getId(), prevProcess.getBurstNeeded()-burstPreempted, SchedulingAlgorithm.PRIO);						
					}
					
					long timeNow = Scheduler.clockTime;
					
					if(prevTime < timeNow){
						long lapse = timeNow - prevTime;
						//System.out.println("p" + currProcess.getId() + " burst: " + currProcess.getBurstTime() + " lapse: " + lapse);
						int burstLeft = (int)(currProcess.getBurstTime() - lapse);					
						currProcess.setBurstTime(burstLeft);		
						//System.out.println("   burstLeft: " + burstLeft);												
						
						if(currProcess.getBurstTime() <= 0){
							//System.out.println("TimeDone: " + (currProcess.getPrevBurstPreempted()));
							GanttChart.addExecutingProcess(currProcess.getId(), currProcess.getPrevBurstPreempted(), SchedulingAlgorithm.PRIO);
							dequeue();													
							//System.out.println("Process p" + currProcess.getId() + " Done executing.");
						}													
					}
					preempted = false;
					prevTime = timeNow;
					
				}else{										
				
					if (allProcessesDone == 0){
						GanttChart.addLastCompletionTime(SchedulingAlgorithm.PRIO);		
						allProcessesDone = 1;						
					}		
				}
			}
		}
	};
	
	public void restart() {
		running = true;
	}
}

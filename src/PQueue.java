
public class PQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;
	private Process prevProcess;
	private boolean running = false;
	private boolean preempted = false;
	private int numOfProcesses;
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
		System.out.println("p" + currProcess.getId() + " preempted! burst = " + currProcess.getBurstTime());
		
		int burstNeeded = currProcess.getBurstNeeded();
		int burstTime = currProcess.getBurstTime(); 
		if(burstNeeded-burstTime > 0){
			int prevBurst = currProcess.getPrevBurstPreempted();
			int burst = currProcess.getBurstTime();
			if(prevBurst-burst == 0){
				prevProcess = null;
				System.out.println("prevProcess = null");				
			}else{
				prevProcess = currProcess;
				System.out.println("prevProcess = currProcess");
			}
		}else{
			prevProcess = null;
		}
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
						
						if(prevProcess != null){
							int burstPreempted = prevProcess.getBurstTime();
							prevProcess.setPrevBurstPreempted(burstPreempted);
							GanttChart.addExecutingProcess(prevProcess.getId(), prevProcess.getBurstNeeded()-burstPreempted, SchedulingAlgorithm.PRIO);
						}
						preempted = false;
					}
					
					long timeNow = Scheduler.clockTime;
					currProcess.setStartTime(timeNow);
					
					if(prevTime < timeNow){
						long lapse = timeNow - prevTime;
						System.out.println("p" + currProcess.getId() + " burst: " + currProcess.getBurstTime() + " lapse: " + lapse);
						int burstLeft = (int)(currProcess.getBurstTime() - lapse);					
						currProcess.setBurstTime(burstLeft);		
						System.out.println("   burstLeft: " + burstLeft);												
						
						if(currProcess.getBurstTime() <= 0){
							System.out.println("TimeDone: " + (currProcess.getPrevBurstPreempted()));
							GanttChart.addExecutingProcess(currProcess.getId(), currProcess.getPrevBurstPreempted(), SchedulingAlgorithm.PRIO);
							dequeue();													
							System.out.println("Process p" + currProcess.getId() + " Done executing.");
						}													
					}
					preempted = false;
					prevTime = timeNow;
					
				}else{										
				
					if (allProcessesDone == 0){
						GanttChart.addLastCompletionTime(SchedulingAlgorithm.PRIO);		
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

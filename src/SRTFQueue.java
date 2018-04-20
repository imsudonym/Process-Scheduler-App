
public class SRTFQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;
	private boolean running = false;
	private byte allProcessesDone = 1;
	private long timeStart;
	private long timeArrive;	
	private long timeElapsed;
	
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
		array.add(newProcess);				
		
		timeArrive = Scheduler.clockTime;		
		
		deterMineIfToPreempt(newProcess);
		sortSJF();
		allProcessesDone = 0;
	}	
	
	private void deterMineIfToPreempt(Process newProcess) {
		if(currProcess != null){
			long currBurst = currProcess.getBurstTime();
			long newBurst = newProcess.getBurstTime();		
			System.out.println("currBurst: " + currBurst + " > newBurst: " + newBurst);
			if(currBurst > newBurst){
				preempt();
			}
		}
	}

	private void preempt() {		
		SRTFThread.interrupt();						
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
				timeStart = 0;
													
				if(getSize() > 0 && peekHead() != null){											
					try {						
						currProcess = dequeue();												
						timeStart = timeElapsed;			
						
						System.out.println("p" + currProcess.getId() + " timeStart: " + timeStart);
						System.out.println("Process p" + currProcess.getId() + " executing...");
						
						long burstTime = currProcess.getBurstTime();	
						GanttChart.addExecutingProcess(currProcess.getId(), burstTime, SchedulingAlgorithm.SRTF);																				
								
						Thread.sleep(burstTime);						
						System.out.println("Done executing.");
						
						timeElapsed += burstTime;
						
					} catch (InterruptedException e) {

						currProcess.setPreempted();
						System.out.println("Process p" + currProcess.getId() + " preempted!");										
																
						long lapse = (timeArrive - timeStart);					
						long burstLeft = currProcess.getBurstTime() - lapse;			
						
						//System.out.println("  timeArrive: " + timeArrive);
						//System.out.println("  timeStart: " + timeStart);
						System.out.println("	lapse: " + lapse);
						
						timeElapsed += lapse;
						
						GanttChart.updatePreemptedProcess(GanttChart.srtfInnerCounter-1, currProcess.getBurstTime(), lapse, SchedulingAlgorithm.SRTF);																		
						currProcess.setBurstTime(burstLeft);
						//System.out.println("Reenqueuing p" + currProcess.getId());
						enqueue(currProcess);						
					}	
				}else{										
					if (allProcessesDone == 0){
						GanttChart.addLastCompletionTime(SchedulingAlgorithm.SRTF);		
						allProcessesDone = 1;						
					}
				}
				
				
			}
		}
	};
}

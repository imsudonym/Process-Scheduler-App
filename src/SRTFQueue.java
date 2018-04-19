
public class SRTFQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;
	private boolean running = false;
	private byte allProcessesDone = 1;
	private long timeStart;
	private long timeArrive;	
	private long timeEnd;
	
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
		
		timeArrive = System.currentTimeMillis();
		
		array.add(newProcess);				
		deterMineIfToPreempt(newProcess);
		sortSJF();
		allProcessesDone = 0;
	}	
	
	private void deterMineIfToPreempt(Process newProcess) {
		if(currProcess != null){
			long currBurst = currProcess.getBurstTime();
			long newBurst = newProcess.getBurstTime();		
			
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
				timeStart = 0; timeEnd = 0;
													
				if(getSize() > 0 && peekHead() != null){											
					try {						
						currProcess = dequeue();
						
						System.out.println("Process p" + currProcess.getId() + " executing...");
						timeStart = System.currentTimeMillis();							
						
						long burstTime = currProcess.getBurstTime();	
						GanttChart.addExecutingProcess(currProcess.getId(), burstTime, SchedulingAlgorithm.SRTF);																				
								
						Thread.sleep(currProcess.getBurstTime());						
						System.out.println("Done executing.");
												
						timeEnd = System.currentTimeMillis();
						
					} catch (InterruptedException e) {

						currProcess.setPreempted();
						System.out.println("Process preempted!");										
						enqueue(currProcess);					
						
						long lapse = (timeStart == 0)? 0 : (timeArrive - timeStart);
						
						// Save burst time left for current executing process..
						long burstLeft = currProcess.getBurstTime() - lapse;				
						currProcess.setBurstTime(burstLeft);
												
						//GanttChart.updatePreemptedProcess(currProcess.getId(), currProcess.getBurstTime(), burstLeft, SchedulingAlgorithm.SRTF);
						running = false;
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

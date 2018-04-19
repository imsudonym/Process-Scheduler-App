
public class NonPQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;
	private boolean running = false;
	private byte allProcessesDone = 1;
	private long timeStart;
	private long timeEnd;
	
	public NonPQueue(){		
		startThread();
	}
	
	private void startThread(){
		running = true;
		NonPThread.start();
	}
	
	public void stopThread(){
		NonPThread.interrupt();
		running = false;
	}
	
	public void enqueue(Process newProcess){
		
		array.add(newProcess);
		array.sortNonPQ();
		allProcessesDone = 0;		
	}	
	
	public Process dequeue(){
					
		Process prc = array.remove();											
		return prc;
	}
	
	public Process peekHead(){
		return array.get(0).getValue(); 
	}
	
	public Process peekTail(){
		return array.get(getSize()-1).getValue(); 		
	}
	
	public int getSize(){
		return array.getSize();
	}
	
	Thread NonPThread = new Thread(){		
		public void run(){
			while(running){					
				timeStart = 0; timeEnd = 0;
										
				if((currProcess = dequeue()) != null){						
					try {
						
						System.out.println("Process p" + currProcess.getId() + " executing...");
						timeStart = System.currentTimeMillis();							
						
						long burstTime = currProcess.getBurstTime();																									
						GanttChart.addExecutingProcess(currProcess.getId(), burstTime, SchedulingAlgorithm.NP_PRIO);																				
								
						Thread.sleep(currProcess.getBurstTime());
						System.out.println("Done executing.");

						array.sortNonPQ();

						timeEnd = System.currentTimeMillis();
						
						
					} catch (InterruptedException e) {

						currProcess.setPreempted();
						System.out.println("Process preempted!");

					}	
				}else{										
					if (allProcessesDone == 0){
						GanttChart.addLastCompletionTime(SchedulingAlgorithm.NP_PRIO);		
						allProcessesDone = 1;						
					}
				}
			}
		}
	};
}

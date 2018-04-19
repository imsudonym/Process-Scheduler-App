
public class NPQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;
	private boolean running = false;
	private byte allProcessesDone = 1;
	private long timeStart;
	private long timeEnd;
	
	public NPQueue(){		
		startThread();
	}
	
	private void startThread(){
		running = true;
		NPThread.start();
	}
	
	public void stopThread(){
		NPThread.interrupt();
		running = false;
	}
	
	public void enqueue(Process newProcess){
		
		array.add(newProcess);		
		sortPriority();
		allProcessesDone = 0;
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
	
	Thread NPThread = new Thread(){				
		public void run(){
			while(running){					
				timeStart = 0; timeEnd = 0;
													
				if(getSize() > 0 && peekHead() != null){											
					try {						
						currProcess = dequeue();
						
						System.out.println("Process p" + currProcess.getId() + " executing...");
						timeStart = System.currentTimeMillis();							
						
						long burstTime = currProcess.getBurstTime();	
						GanttChart.addExecutingProcess(currProcess.getId(), burstTime, SchedulingAlgorithm.NP_PRIO);																				
								
						Thread.sleep(currProcess.getBurstTime());						
						System.out.println("Done executing.");
												
						timeEnd = System.currentTimeMillis();
						
					} catch (InterruptedException e) {

						currProcess.setPreempted();
						System.out.println("Process preempted!");
						//insertOnQueue(currProcess);
																			
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

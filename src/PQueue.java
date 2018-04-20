
public class PQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;
	private boolean running = false;
	private byte allProcessesDone = 1;
	private long timeStart;
	private long timeArrive;	
	private long timeEnd;
	
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
		
		timeArrive = System.currentTimeMillis();
		
		array.add(newProcess);		
		deterMineIfToPreempt(newProcess);	
		sortPriority();
		allProcessesDone = 0;
	}	
	
	private void deterMineIfToPreempt(Process newProcess) {
		if(currProcess != null){
			int currPriority = currProcess.getPriority();
			int newPriority = newProcess.getPriority();			
			System.out.println("" + currPriority + " > " + newPriority);
			if(currPriority > newPriority){
				preempt();
			}
		}
	}

	private void preempt() {
		PThread.interrupt();				
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
				timeStart = 0; timeEnd = 0;
													
				if(getSize() > 0 && peekHead() != null){											
					try {						
						currProcess = dequeue();
						timeStart = System.currentTimeMillis();	
						
						System.out.println("Process p" + currProcess.getId() + " executing...");											
						
						long burstTime = currProcess.getBurstTime();	
						GanttChart.addExecutingProcess(currProcess.getId(), burstTime, SchedulingAlgorithm.PRIO);																				
								
						Thread.sleep(currProcess.getBurstTime());						
						System.out.println("Done executing.");						
						
						timeEnd = System.currentTimeMillis();
						
					} catch (InterruptedException e) {

						currProcess.setPreempted();
						System.out.println("Process preempted!");
												
						long lapse = (timeStart == 0)? 0 : (timeArrive - timeStart);						
						long burstLeft = currProcess.getBurstTime() - lapse;							
						
						System.out.println("lapse: " + lapse + "\n burstLeft: " + burstLeft);						
						
						GanttChart.updatePreemptedProcess(GanttChart.preemptiveInnerCounter-1, currProcess.getBurstTime(), lapse, SchedulingAlgorithm.PRIO);
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						currProcess.setBurstTime(burstLeft);
						enqueue(currProcess);																			
					}	
					
				}else{										
					if (allProcessesDone == 0){
						GanttChart.addLastCompletionTime(SchedulingAlgorithm.PRIO);		
						allProcessesDone = 1;						
					}
				}
				
				
			}
		}
	};
}

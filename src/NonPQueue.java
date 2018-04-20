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
		return array.getHead().getValue(); 
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
				if(getSize() > 0 && peekHead() != null){									
					currProcess = dequeue();
					
					if(timeEnd != 0){						
						timeStart = timeEnd;
					}else{
						timeStart = Scheduler.clockTime;
					}
					
					System.out.println("Process p" + currProcess.getId() + " executing... timeStart = " + timeStart);
					
					long burstTime = currProcess.getBurstTime();																								
					GanttChart.addExecutingProcess(currProcess.getId(), burstTime, SchedulingAlgorithm.NP_PRIO);
					System.out.println("burstTime: " + burstTime);
					System.out.println("clockTime: " + Scheduler.clockTime);
					
					while(Scheduler.clockTime != (timeStart + burstTime)){					
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}				
					}
								
					timeEnd = Scheduler.clockTime;											
					System.out.println("  burstTime: " + burstTime);
					System.out.println("Done executing. timeEnd = " + timeEnd);			
				
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
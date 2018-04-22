
public class FCFSQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;	
	private byte allProcessesDone = 1;
	private boolean running = false;
	private long timeStart;
	private long timeEnd;
	
	public FCFSQueue(){		
		startThread();
	}
	
	private void startThread(){
		running = true;
		FCFSThread.start();
	}	
	
	public void enqueue(Process newProcess){		
		array.add(newProcess);		
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
	
	Thread FCFSThread = new Thread(){
		public void run(){
			while(running){
				if(getSize() > 0 && peekHead() != null){									
					currProcess = dequeue();
					
					if(timeEnd != 0){						
						timeStart = timeEnd;
					}else{
						timeStart = Scheduler.clockTime;
					}
					
					System.out.println("\nProcess p" + currProcess.getId() + " executing... timeStart = " + timeStart);
					//System.out.println("Process p" + currProcess.getId() + " executing... timeStart = " + timeStart);
					
					int burstTime = currProcess.getBurstTime();																								
					GanttChart.addExecutingProcess(currProcess.getId(), burstTime, SchedulingAlgorithm.FCFS);
					//System.out.println("burstTime: " + burstTime);
					//System.out.println("clockTime: " + Scheduler.clockTime);
					
					while(Scheduler.clockTime != (timeStart + burstTime)){					
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}				
					}
								
					timeEnd = Scheduler.clockTime;											
					//System.out.println("  burstTime: " + burstTime);
					//System.out.println("Done executing. timeEnd = " + timeEnd);			
				
				}else{				
					if (allProcessesDone == 0){
						GanttChart.addLastCompletionTime(SchedulingAlgorithm.FCFS);		
						allProcessesDone = 1;						
					}
				}
			}
		}		
	};
}

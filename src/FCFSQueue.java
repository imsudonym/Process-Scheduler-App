
public class FCFSQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;
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
	
	public void stopThread(){
		FCFSThread.interrupt();
		running = false;
	}
	
	public void enqueue(Process newProcess){
		
		array.add(newProcess);		
		//displayQueue(newProcess);		
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
	
	Thread FCFSThread = new Thread(){		
		public void run(){
			while(running){					
				timeStart = 0; timeEnd = 0;
										
				if((currProcess = dequeue()) != null){						
					try {
						
						System.out.println("Process p" + currProcess.getId() + " executing...");
						timeStart = System.currentTimeMillis();							
						
						long burstTime = currProcess.getBurstTime();																									
						GanttChart.addExecutingProcess(currProcess.getId(), burstTime, SchedulingAlgorithm.FCFS);																				
								
						Thread.sleep(currProcess.getBurstTime());						
						System.out.println("Done executing.");
						timeEnd = System.currentTimeMillis();
						
					} catch (InterruptedException e) {

						currProcess.setPreempted();
						System.out.println("Process preempted!");
						//insertOnQueue(currProcess);
																			
					}	
				}
			}
		}
	};
}

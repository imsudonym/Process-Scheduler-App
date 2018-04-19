
public class RRQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;
	private Object nextQueue = null;
	private boolean running = false;
	private byte allProcessesDone = 1;
	private long quantum = 0;
	private long timeStart;
	private long timeEnd;
	
	public RRQueue(long quantum){
		this.quantum = quantum;
		startThread();
	}	
	
	public void setNextQueue(Object nextQueue){
		this.nextQueue = nextQueue;
	}
	
	public Object getNextQueue(){
		return nextQueue;
	}
	
	public long getQuantum(){
		return quantum;
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
	
	Thread FCFSThread = new Thread(){		
		public void run(){
			while(running){					
				timeStart = 0; timeEnd = 0;
										
				if((currProcess = dequeue()) != null){						
					try {
						
						System.out.println("Process p" + currProcess.getId() + " executing...");
						
						timeStart = System.currentTimeMillis();	
						
						long burstTime = currProcess.getBurstTime();			
						long quantum = getQuantum();
						long sleepValue = (burstTime > quantum)? quantum: burstTime;
						
						GanttChart.addExecutingProcess(currProcess.getId(), sleepValue, SchedulingAlgorithm.RR);
						
						Thread.sleep(sleepValue);
						
						long burstLeft = (currProcess.getBurstTime() > getQuantum())? currProcess.getBurstTime() - getQuantum() : 0;	
						System.out.println("process p" + currProcess.getId() + " burstLeft: " + burstLeft);
						currProcess.setBurstTime(burstLeft);
						
						if((getNextQueue() != null) && burstLeft > 0){
							if(getNextQueue() instanceof FCFSQueue){								
								((FCFSQueue) getNextQueue()).enqueue(currProcess);
							}else if(getNextQueue() instanceof RRQueue){
								((RRQueue) getNextQueue()).enqueue(currProcess);
							}
							System.out.println("	Process p" + currProcess.getId() + " moved one queue down.");
						}else if (burstLeft > 0){
							enqueue(currProcess);
						}
							
						timeEnd = System.currentTimeMillis();
					} catch (InterruptedException e) {

						currProcess.setPreempted();
						System.out.println("Process preempted!");
						//insertOnQueue(currProcess);																			
					}	
				}else{
					if (allProcessesDone == 0){
						GanttChart.addLastCompletionTime(SchedulingAlgorithm.RR);		
						allProcessesDone = 1;						
					}
				}
			}
		}
	};
}

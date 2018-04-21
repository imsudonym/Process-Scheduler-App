
public class RRQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;
	private Object nextQueue = null;
	private boolean running = false;
	private byte allProcessesDone = 1;
	private long quantum = 0;
	private boolean preempted = false;
	private long prevTime;
	private long timeNow; 
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
		RRThread.start();
	}
	
	public void stopThread(){
		RRThread.interrupt();
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
		return array.getHead().getValue(); 
	}
	
	public Process peekTail(){
		return array.get(getSize()-1).getValue(); 		
	}
	
	public int getSize(){
		return array.getSize();
	}
	
	Thread RRThread = new Thread(){				
		private long prevTimeQuantum;

		public void run(){
			while(running){					
				if(getSize() > 0){											
					currProcess = peekHead();
					
					if(timeStart < 0){
						if(timeEnd != 0)						
							timeStart = timeEnd;
						else
							timeStart = Scheduler.clockTime;						
					}					
					long timeNow = Scheduler.clockTime;
					
					if(prevTime < timeNow){
																		
						long lapse = timeNow - prevTime;
						System.out.println("p" + currProcess.getId() + " burst: " + currProcess.getBurstTime() + " lapse: " + lapse);
						long burstLeft = currProcess.getBurstTime() - lapse;					
						currProcess.setBurstTime(burstLeft);					
						//GanttChart.addExecutingProcess(currProcess.getId(), currProcess.getBurstTime(), SchedulingAlgorithm.PRIO);
						
						System.out.println("prevTimeQuantum: " + prevTimeQuantum + " timeNow: " + timeNow);
						if(timeNow == prevTimeQuantum + quantum){
							System.out.println("Time na!");
							System.out.println("   burstLeft: " + burstLeft);
							if(burstLeft > 0){
								enqueue(dequeue());
							}							
							prevTimeQuantum = timeNow;
						}						
						
						if(burstLeft <= 0){
							dequeue();						
							//GanttChart.addExecutingProcess(currProcess.getId(), currProcess.getBurstTime(), SchedulingAlgorithm.PRIO);
							System.out.println(" Done executing.");
							timeEnd = Scheduler.clockTime;
							timeStart = -1;
						}													
					}
					preempted = false;
					prevTime = timeNow;	
					
					/*if(prevTime < timeNow){
						
						if(timeNow == prevTimeQuantum + quantum){							
							//enqueue(dequeue());
							//currProcess = peekHead(); 
							System.out.println("prevTime: " + prevTime + " timeNow: " + timeNow);
							prevTimeQuantum = timeNow;		
						}
						
						/*long lapse = timeNow - currProcess.getArrivalTime();
						long burstLeft = currProcess.getBurstTime() - lapse;					
						currProcess.setBurstTime(burstLeft);				
						System.out.println("burstLeft: " + burstLeft);
						//GanttChart.addExecutingProcess(currProcess.getId(), currProcess.getBurstTime(), SchedulingAlgorithm.RR);
						
						if(currProcess.getBurstTime() <= 0){
							dequeue();						
							///GanttChart.addExecutingProcess(currProcess.getId(), currProcess.getBurstTime(), SchedulingAlgorithm.RR);
							System.out.println("Process p" + currProcess.getId() + " Done executing.");
						}
						System.out.println("prevTime: " + prevTime + " timeNow: " + timeNow);
					}
					preempted = false;		
					prevTime = timeNow;*/		
					/*try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}*/
					
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

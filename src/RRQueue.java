
public class RRQueue {
		
	private PseudoArray array = new PseudoArray(20);
	private Process currProcess;
	private Object nextQueue = null;
	private boolean running = false;
	private byte allProcessesDone = 1;
	private int quantum = 0;
	private long prevTime;	
	private long timeStart;
	private long timeEnd;

	
	public RRQueue(int quantum){
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
		private int prevBurstLeft = -1;

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
																		
						int lapse = (int)(timeNow - prevTime);
						//System.out.println("p" + currProcess.getId() + " burst: " + currProcess.getBurstTime() + " lapse: " + lapse);
						int burstLeft = currProcess.getBurstTime() - lapse;					
						currProcess.setBurstTime(burstLeft);																	
						
						//System.out.println("prevTimeQuantum: " + prevTimeQuantum + " timeNow: " + timeNow);
						if(timeNow == prevTimeQuantum + quantum){
							//System.out.println("Time na!");
							//System.out.println("   burstLeft: " + burstLeft);
														
							//System.out.println("burstDone: " + quantum);
							GanttChart.addExecutingProcess(currProcess.getId(), quantum, SchedulingAlgorithm.RR);
							
							if(burstLeft > 0){																
								int burstPreempted = currProcess.getBurstTime();
								currProcess.setPrevBurstPreempted(burstPreempted);
								enqueue(dequeue());
							}
							
							prevTimeQuantum = timeNow;
						}						
						
						if(burstLeft <= 0){							
							if(currProcess.getPrevBurstPreempted() < quantum){							
								GanttChart.addExecutingProcess(currProcess.getId(), currProcess.getPrevBurstPreempted(), SchedulingAlgorithm.RR);								
							}
							dequeue();													
							//System.out.println(" Done executing.");
							timeEnd = Scheduler.clockTime;
							prevTimeQuantum = timeNow;
							timeStart = -1;							
						}													
					}					
					prevTime = timeNow;										
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

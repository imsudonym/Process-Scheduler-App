
public class Scheduler {
	private static final int MAX_QUEUE = 8;
	private static Queue[] queues; 	
	private int numOfQueues = 0;
	private int cur = 0;
	private long timeStart = 0;
	private long timeArrive = 0;
	private long timeEnd = 0;
	
	private static Process currProcess = null;
	
	public Scheduler(int numOfQueues){		
		if (numOfQueues > MAX_QUEUE){
			// Raise exception
		}
		
		this.numOfQueues = numOfQueues;		
		queues = new Queue[numOfQueues];	
	}
		
	public void generateQueues(/*int[] algorithms*/){
		//for(int i = 0; i < numOfQueues; i++){
			queues[0] = new Queue(/*algorithms[i]*/SchedulingAlgorithm.RR);
			queues[0].setQuantum(2000);
			queues[1] = new Queue(/*algorithms[i]*/SchedulingAlgorithm.RR);
			queues[1].setQuantum(3000);
			queues[2] = new Queue(/*algorithms[i]*/SchedulingAlgorithm.FCFS);
		//}					
	}		
	
	public void simulate(){
		simulate.start();
	}
	
	public void insertOnQueue(Process newProcess){
				
		timeArrive = System.currentTimeMillis();
		System.out.println("Inserting newProcess..\n   Lapse: " + (timeArrive-timeStart));
					
		queues[0].enqueue(newProcess);

		// TODO It should allow preemption
		if(timeEnd == 0 && cur > 0){			
			preempt();			
		}		
	}

	public void insertOnQueue(int queue, Process newProcess){	
		queues[queue].enqueue(newProcess);
		
		if(queues[queue].getSchedAlg() == SchedulingAlgorithm.SJF){
			sortSJF(queue);
		}
	}
	
	private void sortSJF(int queue){
		
	}
	
	private void preempt(){
		
		simulate.interrupt();
		
		long lapse = (timeStart == 0)? 0 : (timeArrive - timeStart);
		
		// Save burst time left for current executing process..
		long burstLeft = currProcess.getBurstTime() - lapse;
		currProcess.setBurstTime(burstLeft);
			
		System.out.println("Current executing process #" + currProcess.getId() + " preempted. Burst time left: " + burstLeft);

		// Change current executing queue to Queue #0;
		cur = 0;		
	}
	
	// Thread to allow burst time latency
	Thread simulate = new Thread(){
		public void run(){
			while(true){
				
				// Execute process on head of queue
				cur = 0;
				while(cur < numOfQueues){
					
					timeStart = 0; timeEnd = 0;
										
					if((currProcess = queues[cur].dequeue()) != null){						
						try {
							timeStart = System.currentTimeMillis();							
							
							if(queues[cur].getSchedAlg() == SchedulingAlgorithm.RR){
								
								System.out.println("\n(Queue #" + cur + " RR Quantum=" + queues[cur].quantum() + " Executing process " + 
										currProcess.getId() + " Burst: " + currProcess.getBurstTime() + "ms");
								
								System.out.println("cur: " + cur);
								
								Thread.sleep(queues[cur].quantum());
								
								long burstLeft = (currProcess.getBurstTime() > queues[cur].quantum())? currProcess.getBurstTime() - queues[cur].quantum() : 0;
								
								System.out.println("\nCurrent executing process: #" + currProcess.getId() + " Burst time left: " + burstLeft);
								
								currProcess.setBurstTime(burstLeft);
								
								if(burstLeft > 0){																		
									insertOnQueue((numOfQueues-1 > cur)? cur + 1 : cur, currProcess);
									System.out.println("	Process #" + currProcess.getId() + " moved one queue down.");
								}
								
							}else if (queues[cur].getSchedAlg() == SchedulingAlgorithm.FCFS){
								
								System.out.println("\n(Queue #" + cur + " FCFS Executing process " + 
										currProcess.getId() + " Burst: " + currProcess.getBurstTime() + "ms");
								
								Thread.sleep(currProcess.getBurstTime());
							}else if (queues[cur].getSchedAlg() == SchedulingAlgorithm.SJF){
								System.out.println("\n(Queue #" + cur + " SJF Executing process " + 
										currProcess.getId() + " Burst: " + currProcess.getBurstTime() + "ms");
								
								Thread.sleep(currProcess.getBurstTime());
							}
							
							timeEnd = System.currentTimeMillis();
						} catch (InterruptedException e) {

							currProcess.setPreempted();
							insertOnQueue(cur, currProcess);
							
							System.out.println("Preempted process #" + currProcess.getId() + " reinserted to Queue #" + cur);							
						}
						
						break;
					}else {						
						cur++;
					}
				}												
			}
		}
	};
}

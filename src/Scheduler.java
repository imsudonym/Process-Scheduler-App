
public class Scheduler {
	private static final int MAX_QUEUE = 8;	
	private int numOfQueues = 0;
	
	private int itr = 0;
	private long timeStart = 0;
	private long timeArrive = 0;
	private long timeEnd = 0;
	private long prevArrivalTime = 0;
	
	private Object[] queues;
	private Process[] processes;
	private static Process currProcess = null;
	
	private FCFSQueue fcfsQueue;
	private RRQueue rrQueue;
	private NonPQueue nonpQueue;
	
	public Scheduler(int numOfQueues){		
		if (numOfQueues > MAX_QUEUE){
			// Raise exception
		}
		
		this.numOfQueues = numOfQueues;		
		this.queues = new Object[numOfQueues];
	}
		
	public void initProcesses(Process[] processes){
		this.processes = processes; 
	}
	
	public void simulate(){
		thread.start();
	}
	
	public void generateQueues(int[] algorithms, long[] quantums){
		for(int i = 0; i < numOfQueues; i++){	
			if(algorithms[i] == SchedulingAlgorithm.FCFS){
				queues[i] = new FCFSQueue();
			}else if (algorithms[i] == SchedulingAlgorithm.RR){
				queues[i] = new RRQueue(quantums[i]);
			}else if (algorithms[i] == SchedulingAlgorithm.SJF){
				queues[i] = new SJFQueue();
			}else if (algorithms[i] == SchedulingAlgorithm.NP_PRIO){
				queues[i] = new NonPQueue();
			}
		}							
	}			
	
	public void insertOnQueue(Process newProcess){				
		timeArrive = System.currentTimeMillis();	
		
		if(queues[0] instanceof FCFSQueue){
			((FCFSQueue) queues[0]).enqueue(newProcess);		
		}else if(queues[0] instanceof RRQueue){
			((RRQueue) queues[0]).enqueue(newProcess);
		}else if(queues[0] instanceof SJFQueue){
			((SJFQueue) queues[0]).enqueue(newProcess);
		}else if(queues[0] instanceof NonPQueue){
			((NonPQueue) queues[0]).enqueue(newProcess);
		}

		/*if(timeEnd == 0 && cur > 0){			
			preempt();			
		}*/

		long burstTime = newProcess.getBurstTime();
		long arrivalTime = newProcess.getArrivalTime();
		
		GanttChart.addNewArrivedProcess(newProcess.getId(), arrivalTime, burstTime);
	}

	public void insertOnQueue(Object queue, Process newProcess){	
		if(queue instanceof FCFSQueue){
			fcfsQueue.enqueue(newProcess);
		}else if(queue instanceof SJFQueue){
			// do something
		}else if(queue instanceof RRQueue){
			rrQueue.enqueue(newProcess);
		}else if(queue instanceof SJFQueue){
			//sjfQueue.enqueue(newProcess);
		}else if(queue instanceof NonPQueue){
			nonpQueue.enqueue(newProcess);
		}
		
		long burstTime = newProcess.getBurstTime();
		long arrivalTime = newProcess.getArrivalTime();
		
		GanttChart.addNewArrivedProcess(newProcess.getId(), arrivalTime, burstTime);
	}
	
	private void preempt(){
		
		//simulate.interrupt();
		
		long lapse = (timeStart == 0)? 0 : (timeArrive - timeStart);
		
		// Save burst time left for current executing process..
		long burstLeft = currProcess.getBurstTime() - lapse;
		currProcess.setBurstTime(burstLeft);
			
		System.out.println("Current executing process #" + currProcess.getId() + " preempted. Burst time left: " + burstLeft);
		//cur = 0;		
	}
	
	// Thread to allow arrival time latency
	Thread thread = new Thread(){
		public void run(){
			while(true){
				Process process = null;		
					//System.out.println("itr: " + itr);
					while(itr < processes.length){						
						process = processes[itr++]; 
						try {
							//Process arrival time delay
							if(prevArrivalTime != process.getArrivalTime()){
								Thread.sleep(process.getArrivalTime());
							}
							prevArrivalTime = process.getArrivalTime();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
							// Add process on scheduler queue for execution
							System.out.println("Insert process p" + process.getId());
							insertOnQueue(process);										
					}
				}
			}
		};								
	
	/*
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
							
							long burstTime = currProcess.getBurstTime();
							int algorithm = queues[cur].getSchedAlg();
							long quantum = 0;
							
							if(queues[cur].getSchedAlg() == SchedulingAlgorithm.RR){
								quantum = queues[cur].quantum();
								
								if(burstTime > quantum){
									GanttChart.addExecutingProcess(currProcess.getId(), quantum, algorithm);
								}else{
									GanttChart.addExecutingProcess(currProcess.getId(), burstTime, algorithm);
								}
									
							}else{
								GanttChart.addExecutingProcess(currProcess.getId(), burstTime, algorithm);
							}
							
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
	};*/
}

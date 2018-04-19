
public class Scheduler {
	private static final int MAX_QUEUE = 8;	
	private int numOfQueues = 0;
	
	private int itr = 0;
	private long timeStart = 0;
	private long timeArrive = 0;	
	private long prevArrivalTime = 0;
	
	private Object[] queues;
	private Process[] processes;
	private static Process currProcess = null;
	
	private FCFSQueue fcfsQueue;
	private RRQueue rrQueue;		
	
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
				queues[i] = new NPQueue();
			}else if (algorithms[i] == SchedulingAlgorithm.PRIO){
				queues[i] = new PQueue();
			}else if (algorithms[i] == SchedulingAlgorithm.SRTF){
				queues[i] = new SRTFQueue();
			}
			
		}							
	}			
	
	private void insertOnQueue(Process newProcess){				
		timeArrive = System.currentTimeMillis();	
		
		if(queues[0] instanceof FCFSQueue){
			((FCFSQueue) queues[0]).enqueue(newProcess);		
		}else if(queues[0] instanceof RRQueue){
			((RRQueue) queues[0]).enqueue(newProcess);
		}else if(queues[0] instanceof SJFQueue){
			((SJFQueue) queues[0]).enqueue(newProcess);
		}else if(queues[0] instanceof NPQueue){
			((NPQueue) queues[0]).enqueue(newProcess);
		}else if(queues[0] instanceof PQueue){
			((PQueue) queues[0]).enqueue(newProcess);
		}else if(queues[0] instanceof SRTFQueue){
			((SRTFQueue) queues[0]).enqueue(newProcess);
		}

		/*if(timeEnd == 0 && cur > 0){			
			preempt();			
		}*/

		long burstTime = newProcess.getBurstTime();
		long arrivalTime = newProcess.getArrivalTime();
		
		GanttChart.addNewArrivedProcess(newProcess.getId(), arrivalTime, burstTime);
	}		
	
	// Thread to allow arrival time latency
	Thread thread = new Thread(){
		public void run(){
			while(true){
				Process process = null;		
					while(itr < processes.length){									
						process = processes[itr++]; 
						try {
							
							//Process arrival time delay
							if(prevArrivalTime != process.getArrivalTime()){
								System.out.println("arrivalTime = " + process.getArrivalTime());
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
}

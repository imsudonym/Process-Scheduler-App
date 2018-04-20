
public class Scheduler {
	private static final int MAX_QUEUE = 8;	
	private int numOfQueues = 0;
	
	private int itr = 0;
	public static long clockTime = 0;	
	private long prevArrivalTime = 0;
	
	private Object[] queues;
	private Process[] processes;
	private static Process currProcess = null;	
	
	public Scheduler(int numOfQueues){		
		if (numOfQueues > MAX_QUEUE){
			// Raise exception
		}
		
		this.numOfQueues = numOfQueues;		
		this.queues = new Object[numOfQueues];
	}
		
	public void initProcesses(Process[] processes){
		this.processes = processes;
		if(queues[0] instanceof PQueue){
			preSortSameArrivalTime();
		}
	}
	
	private void preSortSameArrivalTime() {
		for(int i = 0; i < processes.length; i++){
			for(int j = i; j < processes.length; j++){
				if(processes[i].getArrivalTime() == processes[j].getArrivalTime() && processes[i].getPriority() > processes[j].getPriority()){
					System.out.println("Swapping p" + processes[i].getId() + " and p" + processes[j].getId());
					Process temp = processes[i];
					processes[i] = processes[j];
					processes[j] = temp; 
				}
			}			
		}
		printContents(processes);
	}

	private void printContents(Process[] processes2) {
		System.out.print("[");
		for(int i = 0; i < processes2.length; i++){
			System.out.print("p" + processes2[i].getId());
		}
		System.out.println("]");		
	}

	public void simulate(){
		//thread.start();
		clock.start();
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
			}else if (algorithms[i] == SchedulingAlgorithm.PRIO){
				queues[i] = new PQueue();
			}else if (algorithms[i] == SchedulingAlgorithm.SRTF){
				queues[i] = new SRTFQueue();
			}
			
		}							
	}			
	
	private void insertOnQueue(Process newProcess){				
		//timeArrive = System.currentTimeMillis();	
		
		if(queues[0] instanceof FCFSQueue){
			((FCFSQueue) queues[0]).enqueue(newProcess);		
		}else if(queues[0] instanceof RRQueue){
			((RRQueue) queues[0]).enqueue(newProcess);
		}else if(queues[0] instanceof SJFQueue){
			((SJFQueue) queues[0]).enqueue(newProcess);
		}else if(queues[0] instanceof NonPQueue){
			((NonPQueue) queues[0]).enqueue(newProcess);
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
	
	Thread clock = new Thread(){
		public void run(){
			while(true){				
								
				for(int i = itr; i < processes.length; i++){								
					if(processes[i].getArrivalTime() == clockTime){						
						System.out.println("Insert process p" + processes[i].getId() + " time: " + clockTime);
						insertOnQueue(processes[i]);
						itr++;
					}else if(processes[i].getArrivalTime() > clockTime){
						break;
					}
				}
								
				clockTime++;
				
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}
			}
		}
	};
	
	// Thread to allow arrival time latency
	Thread thread = new Thread(){
		public void run(){
			while(true){
				Process process = null;		
					while(itr < processes.length){									
						process = processes[itr++]; 
						try {
							if(prevArrivalTime != process.getArrivalTime()){
								System.out.println("arrivalTime = " + process.getArrivalTime());
								Thread.sleep(process.getArrivalTime());
							}
							prevArrivalTime = process.getArrivalTime();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
							// Add process on scheduler queue for execution
																	
					}
				}
			}
		};									
}

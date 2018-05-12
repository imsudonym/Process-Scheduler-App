
public class Scheduler {
	private static final int MAX_QUEUE = 8;	
	private int numOfQueues = 0;
	
	private static int itr = 0;
	public static long clockTime = 0;	
	private long prevArrivalTime = 0;
	private static boolean running = false;
	
	public static Object[] queues;
	public static Process[] processes;
	
	public Scheduler(int numOfQueues){		
		if (numOfQueues > MAX_QUEUE){
			// Raise exception
		}
		
		this.numOfQueues = numOfQueues;		
		Scheduler.queues = new Object[numOfQueues];
	}
		
	public void initProcesses(Process[] processes){
		Scheduler.processes = processes;
		sortByArrivalTime();
		if(queues[0] instanceof PQueue){
			preSortSameArrivalTime();
		}				
	}
	
	private void sortByArrivalTime() {
		for(int i = 0; i < processes.length; i++){
			for(int j = i; j < processes.length; j++){
				if(processes[i].getArrivalTime() > processes[j].getArrivalTime()){
					Process temp = processes[i];
					processes[i] = processes[j];
					processes[j] = temp; 
				}
			}			
		}
		printContents(processes);
	}

	private void preSortSameArrivalTime() {
		for(int i = 0; i < processes.length; i++){
			for(int j = i; j < processes.length; j++){
				if(processes[i].getArrivalTime() == processes[j].getArrivalTime() && processes[i].getPriority() > processes[j].getPriority()){
					//System.out.println("Swapping p" + processes[i].getId() + " and p" + processes[j].getId());
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
		running  = true;		
		clock.start();
	}
	
	public static void stop(){
		clock.interrupt();		
		clockTime = 0;
	}
	
	public void generateQueues(int algorithm, int quantum){
		for(int i = 0; i < numOfQueues; i++){	
			if(algorithm == SchedulingAlgorithm.FCFS){
				queues[0] = new FCFSQueue();
				((FCFSQueue) queues[0]).setNumberOFProcesses(processes.length);
			}else if (algorithm == SchedulingAlgorithm.RR){
				queues[0] = new RRQueue(quantum);
				((RRQueue) queues[0]).setNumberOFProcesses(processes.length);
			}else if (algorithm == SchedulingAlgorithm.SJF){
				queues[0] = new SJFQueue();
				((SJFQueue) queues[0]).setNumberOFProcesses(processes.length);
			}else if (algorithm == SchedulingAlgorithm.NP_PRIO){
				queues[0] = new NonPQueue();
				((NonPQueue) queues[0]).setNumberOFProcesses(processes.length);
			}else if (algorithm == SchedulingAlgorithm.PRIO){
				queues[0] = new PQueue();
				((PQueue) queues[0]).setNumberOFProcesses(processes.length);
			}else if (algorithm == SchedulingAlgorithm.SRTF){
				queues[0] = new SRTFQueue();
				((SRTFQueue) queues[0]).setNumberOFProcesses(processes.length);
			}
			
		}							
	}	
	
	public void generateQueues(int[] algorithms, int[] quantums){
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
	
	private static void insertOnQueue(Process newProcess){				
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

		int burstTime = newProcess.getBurstNeeded();
		int arrivalTime = newProcess.getArrivalTime();
		int priority = newProcess.getPriority();
		
		GanttChart.addNewArrivedProcess(newProcess.getId(), arrivalTime, burstTime, priority);
	}		
	
	static Thread clock = new Thread(){
		public void run(){
			System.out.println("running: " + running);
			while(running){				
								
				for(int i = itr; i < processes.length; i++){								
					if(processes[i].getArrivalTime() == clockTime){						
						//System.out.println("Clock time: " + clockTime + " insert p" + processes[i].getId());
						insertOnQueue(processes[i]);
						itr++;
					}else if(processes[i].getArrivalTime() > clockTime){						
						break;
					}
				}
								
				clockTime++;
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}
	};
		
	public void restart() {
		itr = 0;
		clockTime = 0;
		running = true;		
	}
									
}

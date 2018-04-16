
public class Main {
	private static Queue arrivOrderOfProcesses = new Queue(-1);
	private static long prevArrivalTime;
	
	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler(3);
		
		//int algorithms[] = {SchedulingAlgorithm.FCFS};
		
		scheduler.generateQueues(/*algorithms*/);
				
		arrivOrderOfProcesses.enqueue(new Process(1, 0, 10000, 0));
		arrivOrderOfProcesses.enqueue(new Process(2, 1000, 10000, 0));
		arrivOrderOfProcesses.enqueue(new Process(3, 2000, 5000, 0));
		arrivOrderOfProcesses.enqueue(new Process(4, 5000, 5000, 0));
		arrivOrderOfProcesses.enqueue(new Process(5, 5000, 5000, 0));
		
		// Thread to allow arrival time latency
		Thread thread = new Thread(){
			public void run(){
				while(true){
					Process process = null;
					while((process = arrivOrderOfProcesses.dequeue()) != null ){
						try {
							// Process arrival time delay
							if(prevArrivalTime != process.getArrivalTime()){
								Thread.sleep(process.getArrivalTime());
							}
							prevArrivalTime = process.getArrivalTime();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						// Add process on scheduler queue for execution
						System.out.println("=> Insert process #" + process.getId() + " on arrv time: " + process.getArrivalTime());
						scheduler.insertOnQueue(process);										
					}
				}
			}
		};
				
		thread.start();
		scheduler.simulate();
	}
}

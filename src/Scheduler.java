import java.util.Queue;

public class Scheduler {
	private static final int MAX_QUEUE = 8;	
	private int[] algorithms; 
	private int numOfQueues = 0;
	
	public Scheduler(int numOfQueues){		
		if (numOfQueues > MAX_QUEUE){
			// Raise exception
		}
		
		this.numOfQueues = numOfQueues;
		algorithms = new int[numOfQueues];
	}
		
	public void generateQueues(int[] algorithms){
		for(int i = 0; i < numOfQueues; i++){
			this.algorithms[i] = algorithms[i];
		}
		
		Queue[] queue = new Queue[numOfQueues];
	}		
	
	public void simulate(){
		simulate.start();
	}
	
	Thread simulate = new Thread(){
		public void run(){
			while(true){
				
			}
		}
	};
}

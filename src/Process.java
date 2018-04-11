
public class Process {
	private int id;
	private int arrivalTime;
	private int burstTime;
	private int priority;
	private int timesPreempted = 0;
	
	public Process(int id, int arrivalTime, int burstTime, int priority){
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.burstTime = burstTime;
		this.priority = priority;
	}
	
	public int getId(){
		return id;
	}
	
	public int getArrivalTime(){
		return arrivalTime;
	}
	
	public int getBurstTime(){
		return burstTime;
	}
	
	public int getPriority(){
		return priority;
	}
	
	public int getTimesPreempted(){
		return timesPreempted;
	}
	
	public void setPreempted(){
		timesPreempted++;
	}

}

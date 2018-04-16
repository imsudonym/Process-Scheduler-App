
public class Process {
	private int id;
	private long arrivalTime;
	private long burstTime;
	private int priority;
	private int timesPreempted = 0;
	
	public Process(int id, long arrivalTime, long burstTime, int priority){
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.burstTime = burstTime;
		this.priority = priority;
	}
	
	public int getId(){
		return id;
	}
	
	public long getArrivalTime(){
		return arrivalTime;
	}
	
	public void setBurstTime(long burstLeft){
		this.burstTime = burstLeft;
	}
	
	public long getBurstTime(){
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

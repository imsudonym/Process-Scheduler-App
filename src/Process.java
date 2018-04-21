
public class Process {
	private int id;
	private int arrivalTime;
	private int burstNeeded;
	private int burstTime;
	private int priority;
	private int timesPreempted = 0;
	private int prevBurstPreempted;
	
	public Process(int id, int arrivalTime, int burstTime, int priority){
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.burstTime = burstTime;		
		this.burstNeeded = burstTime;
		this.prevBurstPreempted = burstTime;
		this.priority = priority;		
	}
	
	public int getId(){
		return id;
	}
	
	public int getBurstNeeded(){
		return burstNeeded;
	}
	
	public int getArrivalTime(){
		return arrivalTime;
	}
	
	public void setBurstTime(int burstLeft){
		this.burstTime = burstLeft;
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

	public int getPrevBurstPreempted() {
		return prevBurstPreempted;
	}

	public void setPrevBurstPreempted(int prevBurstPreempted) {
		this.prevBurstPreempted = prevBurstPreempted;
	}

}

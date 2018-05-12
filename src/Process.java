import java.util.ArrayList;

public class Process {
	private int id;
	private int arrivalTime;
	private int burstNeeded;
	private int burstTime;
	private int priority;
	
	private int timesPreempted = 0;
	private int prevBurstPreempted;
	
	private ArrayList<Long> timePreempted = new ArrayList<Long>();
	private ArrayList<Long> timeResumed = new ArrayList<Long>();
	
	private long startTime;
	private long endTime;
	private long waitTime;
	private long responseTime;
	private long turnaroundTime;
	
	public boolean preemptedFlag = false;
	
	public Process(int id, int arrivalTime, int burstTime, int priority){
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.burstTime = burstTime;		
		this.burstNeeded = burstTime;
		this.prevBurstPreempted = burstTime;
		this.priority = priority;		
		
		this.endTime = this.waitTime = this.responseTime 
				= this.turnaroundTime = -1;
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

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getTimePreempted(int index) {
		return timePreempted.get(index);
	}

	public void setTimePreempted(long timePreempted) {
		this.timePreempted.add(timePreempted);
	}

	public long getTimeResumed(int index) {
		return timeResumed.get(index);
	}

	public void setTimeResumed(long timeResumed) {
		this.timeResumed.add(timeResumed);
	}

	public long getWaitTime() {
		return waitTime;
	}
	
	public void setWaitTimePreemptive() {
		long waitTime = (responseTime - arrivalTime) < 0 ? 0: (responseTime - arrivalTime);
		
		for(int i = 0; i < timeResumed.size(); i++) {
			waitTime += (timeResumed.get(i) - timePreempted.get(i));
		}
		this.waitTime = waitTime;
	}

	public void setWaitTimeNonPreemptive() {
		this.waitTime = (startTime - arrivalTime) < 0 ? 0: (startTime - arrivalTime);
	}
	
	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	public long getTurnaroundTime() {
		return getWaitTime() + getBurstTime();
	}

	public void setStartTime(long timeStart) {
		this.startTime = timeStart;
	}
}

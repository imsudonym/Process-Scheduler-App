package util;
import java.util.ArrayList;

public class CPUBoundProcess {
	protected int id;
	protected int arrivalTime;
	protected int burstNeeded;
	protected int burstTime;
	protected int priority;
	
	protected int timesPreempted = 0;
	protected int prevBurstPreempted;
	
	public ArrayList<Long> timePreempted = new ArrayList<Long>();
	public ArrayList<Long> timeResumed = new ArrayList<Long>();
	
	protected long firstStartTime;
	protected long startTime;
	protected long endTime;
	protected long waitTime;
	protected long responseTime;
	protected long turnaroundTime;
	
	public boolean preemptedFlag = false;
	
	public CPUBoundProcess(int id, int arrivalTime, int burstTime, int priority){
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
		long waitTime = (firstStartTime - arrivalTime) < 0 ? 0: (firstStartTime - arrivalTime);
		
		for(int i = 0; i < timeResumed.size(); i++) {
			waitTime += (timeResumed.get(i) - timePreempted.get(i));
		}
		this.waitTime = waitTime;
	}

	public void setWaitTimeNonPreemptive() {
		this.waitTime = (firstStartTime - arrivalTime) < 0 ? 0: (firstStartTime - arrivalTime);
	}
	
	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	public long getTurnaroundTime() {
		return getWaitTime() + getBurstNeeded();
	}

	public void setStartTime(long timeStart) {
		this.startTime = timeStart;
	}
	
	public void setFirstStartTime(long firstStartTime) {
		this.firstStartTime = firstStartTime;
	}
	
	public long getStartTime() {
		return startTime;
	}

	public void setArrivalTime(long time) {
		this.arrivalTime = (int)time;
	}
}

package process;
import java.util.ArrayList;

public class CPUBoundProcess {
	protected int id;
	protected int arrivalTime;
	protected int burstNeeded;
	protected int burstTime;
	protected int priority;
	
	protected int timesPreempted = 0;
	protected int prevBurstPreempted;
	
	public ArrayList<Integer> timePreempted = new ArrayList<Integer>();
	public ArrayList<Integer> timeResumed = new ArrayList<Integer>();
	
	protected int firstStartTime;
	protected int startTime;
	protected int endTime;
	protected int waitTime;
	protected int responseTime;
	protected int turnaroundTime;
	
	public boolean preemptedFlag = false;
	
	public CPUBoundProcess(int id, int arrivalTime, int burstTime, int priority){
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.burstTime = burstTime;		
		this.burstNeeded = burstTime;
		this.prevBurstPreempted = burstTime;
		this.priority = priority;		
		
		this.startTime = this.endTime = this.waitTime = this.responseTime 
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
		return timePreempted.size();
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

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public long getTimePreempted(int index) {
		return timePreempted.get(index);
	}

	public void setTimePreempted(int timePreempted) {
		this.timePreempted.add(timePreempted);
	}

	public int getTimeResumed(int index) {
		return timeResumed.get(index);
	}

	public void setTimeResumed(int timeResumed) {
		this.timeResumed.add(timeResumed);
	}

	public int getWaitTime() {
		return waitTime;
	}
	
	public void setWaitTimePreemptive() {
		int waitTime = (firstStartTime - arrivalTime) < 0 ? 0: (firstStartTime - arrivalTime);
		
		/*System.out.println("[CPUBoundProcess] process P" + id);
		System.out.println("[CPUBoundProcess:] timeResumed.size:" + timeResumed.size());
		System.out.println("[CPUBoundProcess:] timePreemted.size:" + timePreempted.size());*/
		
		for(int i = 0; i < timeResumed.size(); i++) {
			System.out.println("[CPUBoundProcess] timeResumed: " + timeResumed.get(i) + " timePreempted: " + timePreempted.get(i));
			waitTime += (timeResumed.get(i) - timePreempted.get(i));
		}
		this.waitTime = waitTime;
	}

	public void setWaitTimeNonPreemptive() {
		this.waitTime = (firstStartTime - arrivalTime) < 0 ? 0: (firstStartTime - arrivalTime);
	}
	
	public int getResponseTime() {
		return responseTime;
	}

	public void setResponseTime() {
		this.responseTime = firstStartTime - arrivalTime;
	}

	public int getTurnaroundTime() {
		return getWaitTime() + getBurstNeeded();
	}

	public void setStartTime(int timeStart) {
		this.startTime = timeStart;
	}
	
	public void setFirstStartTime(int firstStartTime) {
		this.firstStartTime = firstStartTime;
	}
	
	public int getStartTime() {
		return startTime;
	}

	public void setArrivalTime(long time) {
		this.arrivalTime = (int)time;
	}
}

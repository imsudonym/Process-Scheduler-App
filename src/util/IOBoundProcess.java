package util;

public class IOBoundProcess extends CPUBoundProcess{
	
	private static final int IOSPEED = 5;
	
	public IOBoundProcess(int id, int arrivalTime, int burstTime, int priority) {
		super(id, arrivalTime, burstTime, priority);
	}

	public int getIoSpeed() {
		return IOSPEED;
	}

}

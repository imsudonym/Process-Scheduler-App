package Process;

public class IOBoundProcess extends CPUBoundProcess{
	
	private static final int IOSPEED = 10;
	
	public IOBoundProcess(int id, int arrivalTime, int burstTime, int priority) {
		super(id, arrivalTime, burstTime, priority);
	}

}

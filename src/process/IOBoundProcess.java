package process;

public class IOBoundProcess extends CPUBoundProcess{
	
	private static final int IOSPEED = 10;
	private static final int IOCPUTIME = 5;
	
	public IOBoundProcess(int id, int arrivalTime, int burstTime, int priority) {
		super(id, arrivalTime, burstTime, priority);
	}

	public int getIoSpeed() {
		return IOSPEED;
	}

	public int getIoCpuTime() {
		return IOCPUTIME;
	}

}

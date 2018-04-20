import javax.swing.JFrame;

public class Main {
	private static Process[] processes = new Process[5];	
	static int ctr = 0;
	
	public static void main(String[] args) {
		
		int algorithms[] = {SchedulingAlgorithm.FCFS};
		long respectiveQuantum[] = {2000};
		
		GanttChart gantt = new GanttChart();
		gantt.setVisible(true);
		gantt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gantt.init(algorithms, respectiveQuantum);
		
		Scheduler scheduler = new Scheduler(algorithms.length);	
		
		scheduler.generateQueues(algorithms, respectiveQuantum);
				
		processes[0] = new Process(1, 0, 10000, 5);
		processes[1] = new Process(2, 1, 2000, 4);
		processes[2] = new Process(3, 1, 1000, 1);
		processes[3] = new Process(4, 5, 5000, 4);
		processes[4] = new Process(5, 5, 3000, 3);
		
		scheduler.initProcesses(processes);
		scheduler.simulate();
	}
}

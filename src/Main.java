import javax.swing.JFrame;
import java.util.Random();
import java.util.Scanner;

public class Main {
	private static Process[] processes;	
	static int ctr = 0;
	static int noOfProcesses = 0;
	Scanner scan;
	Random rand = new Random();
	//burstTime 0-50
	//noOfProcesses 0-20
	public static void main(String[] args) {

		System.out.println("Enter number of processes: ");
		scan = new Scanner(System.in);
		noOfProcesses = scan.nextInt();
		
		while(noOfProcesses > 20){
			System.out.println("Enter number of processes: ");
			scan = new Scanner(System.in);
			noOfProcesses = scan.nextInt();
		}
		
		int algorithms[] = {SchedulingAlgorithm.SRTF};
		long respectiveQuantum[] = {2000};
		
		processes = new Process[noOfProcesses];
		int burstTime[] = new int[noOfProcesses];
		int arrivalTime[] = new int[noOfProcesses];
		
		GanttChart gantt = new GanttChart();
		gantt.setVisible(true);
		gantt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gantt.init(algorithms, respectiveQuantum);
		
		Scheduler scheduler = new Scheduler(algorithms.length);	
		
		scheduler.generateQueues(algorithms, respectiveQuantum);
		
		for(int i = 0; i < noOfProcesses; i++){
			arrivalTime[i] = rand.nextInt((noOfProcesses*50)+1);
			burstTime[i] = rand.nextInt(51);
			processes[i] = new Processes(i+1, arrivalTime[i], burstTime[i], 0)
		}
		/*
		processes[0] = new Process(1, 0, 10000, 5);
		processes[1] = new Process(2, 1000, 2000, 4);
		processes[2] = new Process(3, 1000, 1000, 1);
		processes[3] = new Process(4, 5000, 5000, 4);
		processes[4] = new Process(5, 5000, 3000, 3);
		*/
		scheduler.initProcesses(processes);
		scheduler.simulate();
	}
}

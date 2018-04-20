import javax.swing.JFrame;
import java.util.Random;
import java.util.Scanner;
import java.lang.*;

public class Main {
	private static Process[] processes;	
	static int ctr = 0;
	static int noOfProcesses = 0;
	static int algorithm = 0;
	
	public static void main(String[] args) {
		/*System.out.println("Enter number of processes: ");
		Scanner scan = new Scanner(System.in);
		noOfProcesses = scan.nextInt();
		
		while(noOfProcesses > 20){
			System.out.println("Enter number of processes: ");
			noOfProcesses = scan.nextInt();
		}
		
		System.out.println("Choose which algorithm to use: ");
		System.out.print("0 - FCFS\n1 - SJF\n2 - SRTF\n3 - NP_PRIO\n4 - PRIO\n5 - RR\nCHOICE: ");
		algorithm = scan.nextInt();*/
		
		int algorithms[] = {SchedulingAlgorithm.PRIO};
		long respectiveQuantum[] = {2000};
		
		processes = new Process[5];
		long burstTime[] = new long[noOfProcesses];
		long arrivalTime[] = new long[noOfProcesses];
		int priority[] = new int[noOfProcesses];
		
		GanttChart gantt = new GanttChart();
		gantt.setVisible(true);
		gantt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gantt.init(algorithms, respectiveQuantum);
		
		Scheduler scheduler = new Scheduler(algorithms.length);	
		scheduler.generateQueues(algorithms, respectiveQuantum);
		//int id, long arrivalTime, long burstTime, int priority
		processes[0] = new Process(1, 0, 10, 3);
		processes[1] = new Process(2, 1, 10, 1);
		processes[2] = new Process(3, 2, 10, 2);
		processes[3] = new Process(4, 3, 10, 5);
		processes[4] = new Process(5, 4, 10, 4);
		
		/*Random rand = new Random();
		int tmp1, tmp2;
		for(int i = 0; i < noOfProcesses; i++){
			tmp1 = rand.nextInt(10000);
			arrivalTime[i] = new Long(tmp1);
			tmp2 = rand.nextInt(5000);
			burstTime[i] = new Long(tmp2);
		} 
		
		if(algorithm == SchedulingAlgorithm.FCFS || algorithm == SchedulingAlgorithm.SJF || algorithm == SchedulingAlgorithm.SRTF || algorithm == SchedulingAlgorithm.RR){
			System.out.println("PID		Arrival Time 		Burst Time");
			for(int i = 0; i < noOfProcesses; i++){
				processes[i] = new Process(i+1, arrivalTime[i], burstTime[i], 0);
				System.out.println(" " + (i+1) + " 		" + arrivalTime[i] + "			" + burstTime[i]);
			}
		}else if(algorithm == SchedulingAlgorithm.NP_PRIO || algorithm == SchedulingAlgorithm.PRIO){
			System.out.println("PID		Arrival Time 		Burst Time		Priority");
			for(int i = 0; i < noOfProcesses; i++){
				priority[i] = rand.nextInt(20)+1;
				processes[i] = new Process(i+1, arrivalTime[i], burstTime[i], priority[i]);
				System.out.println(" " + (i+1) + " 		" + arrivalTime[i] + "			" + burstTime[i] + "	" + priority[i]);
			}
		}
		
		//sort processes by arrival time
		Process temp;
		for(int i = 0; i < noOfProcesses; i++){  
			for(int j = 1; j < (noOfProcesses - i); j++){  
				if(processes[j - 1].getArrivalTime() > processes[j].getArrivalTime()){  
					//swap elements  
					temp = processes[j-1];
					processes[j-1] = processes[j];				
					processes[j] = temp;
				}
			}
    }
		
		System.out.println("PID		Arrival Time 		Burst Time");
		for(int i = 0; i < noOfProcesses; i++){
			//processes[i] = new Process(i+1, arrivalTime[i], burstTime[i], 0);
			System.out.println(" " + processes[i].getId() + " 		" + processes[i].getArrivalTime() + "			" + processes[i].getBurstTime());
		}*/

		scheduler.initProcesses(processes);
		scheduler.simulate();
	}
}

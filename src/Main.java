import javax.swing.JFrame;
import java.util.Random;
import java.util.Scanner;
import java.lang.*;

public class Main {
	private static Process[] processes;	
	static int ctr = 0;
	static int choice = 0;
	static int noOfProcesses = 0;
	static int algorithm = 0;
	
	public static void main(String[] args) {
		System.out.println("Enter number of processes: ");
		Scanner scan = new Scanner(System.in);
		noOfProcesses = scan.nextInt();
		
		while(noOfProcesses > 20){
			System.out.println("Enter number of processes: ");
			noOfProcesses = scan.nextInt();
		}
		
		System.out.println("Choose which algorithm to use: ");
		System.out.print("0 - FCFS\n1 - SJF\n2 - SRTF\n3 - NP_PRIO\n4 - PRIO\n5 - RR\nCHOICE: ");
		algorithm = scan.nextInt();
		
		while(algorithm < 0 || algorithm > 5){
			System.out.println("Choose which algorithm to use: ");
			System.out.print("0 - FCFS\n1 - SJF\n2 - SRTF\n3 - NP_PRIO\n4 - PRIO\n5 - RR\nCHOICE: ");
			algorithm = scan.nextInt();
		}
		
		System.out.println("Processes will be: ");
		System.out.print("1 - Random\n2 - User-defined\nCHOICE: ");
		choice = scan.nextInt();
		
		while(choice > 2){
			System.out.println("Processes will be: ");
			System.out.print("1 - Random\n2 - User-defined\nCHOICE: ");
			choice = scan.nextInt();
		}
		
		int algorithms[] = {algorithm};
		long respectiveQuantum[] = {2};
		
		processes = new Process[noOfProcesses];
		long burstTime[] = new long[noOfProcesses];
		long arrivalTime[] = new long[noOfProcesses];
		int priority[] = new int[noOfProcesses];
		
		GanttChart gantt = new GanttChart();
		gantt.setVisible(true);
		gantt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gantt.init(algorithms, respectiveQuantum);
		
		Scheduler scheduler = new Scheduler(algorithms.length);	
		scheduler.generateQueues(algorithms, respectiveQuantum);
		
		int tmp1, tmp2, tmp3;
		if(choice == 1){
			Random rand = new Random();
			
			for(int i = 0; i < noOfProcesses; i++){
				tmp1 = rand.nextInt(noOfProcesses);
				arrivalTime[i] = new Long(tmp1);
				tmp2 = rand.nextInt(50);
				burstTime[i] = new Long(tmp2);
				tmp3 = rand.nextInt(noOfProcesses);
				priority[i] = new Integer(tmp3);
			} 			
		}else{
			for(int i = 0; i < noOfProcesses; i++){
				System.out.println("Process " +(i+1) + ":" );
				System.out.print("Arrival time: ");
				tmp1 = scan.nextInt();
				arrivalTime[i] = new Long(tmp1);
				System.out.print("Burst time: ");
				tmp2 = scan.nextInt();
				burstTime[i] = new Long(tmp2);
				System.out.print("Priority: ");
				tmp3 = scan.nextInt();
				priority[i] = new Integer(tmp3);
			} 
		}

		
		if(algorithm == SchedulingAlgorithm.FCFS || algorithm == SchedulingAlgorithm.SJF || algorithm == SchedulingAlgorithm.SRTF || algorithm == SchedulingAlgorithm.RR){
			System.out.println("PID		Arrival Time 		Burst Time 		Priority");
			for(int i = 0; i < noOfProcesses; i++){
				processes[i] = new Process(i+1, arrivalTime[i], burstTime[i], priority[i]);
				System.out.println(" " + (i+1) + " 		" + arrivalTime[i] + "			" + burstTime[i] + "			" + priority[i]);
			}
		}else if(algorithm == SchedulingAlgorithm.NP_PRIO || algorithm == SchedulingAlgorithm.PRIO){
			System.out.println("PID		Arrival Time 		Burst Time	Priority");
			for(int i = 0; i < noOfProcesses; i++){
				//priority[i] = rand.nextInt(20)+1;
				processes[i] = new Process(i+1, arrivalTime[i], burstTime[i], priority[i]);
				System.out.println(" " + (i+1) + " 		" + arrivalTime[i] + "			" + burstTime[i] + "		" + priority[i]);
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
		
		System.out.println("PID		Arrival Time 		Burst Time		Priority");
		for(int i = 0; i < noOfProcesses; i++){
			//processes[i] = new Process(i+1, arrivalTime[i], burstTime[i], 0);
			System.out.println(" " + processes[i].getId() + " 		" + processes[i].getArrivalTime() + "			" + processes[i].getBurstTime() +
													"			" + processes[i].getPriority());
		}

		// Uncomment below to start simulation.
		scheduler.initProcesses(processes);
		scheduler.simulate();
	}
}

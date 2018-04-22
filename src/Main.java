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
		
		System.out.print("Processes will be:\n1 - Random\n2 - User-defined\nCHOICE: ");
		choice = scan.nextInt();
		
		while(choice > 2){
			System.out.print("Processes will be:\n1 - Random\n2 - User-defined\nCHOICE: ");
			choice = scan.nextInt();
		}
		
		int algorithms[] = {algorithm};
		int respectiveQuantum[] = {2};
		
		processes = new Process[noOfProcesses];
		int burstTime[] = new int[noOfProcesses];
		int arrivalTime[] = new int[noOfProcesses];
		int priority[] = new int[noOfProcesses];
		
		GanttChart gantt = new GanttChart();
		gantt.setVisible(true);
		gantt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		gantt.init(algorithms, respectiveQuantum);
		
		Scheduler scheduler = new Scheduler(algorithms.length);	
		scheduler.generateQueues(algorithms, respectiveQuantum);
		/*
		processes[0] = new Process(1, 0, 10, 3);
		processes[1] = new Process(2, 1, 9, 1);
		processes[2] = new Process(3, 2, 8, 2);
		processes[3] = new Process(4, 3, 7, 5);
		processes[4] = new Process(5, 4, 6, 4);
		processes[5] = new Process(6, 5, 5, 3);
		processes[6] = new Process(7, 6, 4, 1);
		processes[7] = new Process(8, 7, 3, 2);
		processes[8] = new Process(9, 8, 2, 5);
		processes[9] = new Process(10, 9, 1, 4);
		*/
		
		if(choice == 1){
			Random rand = new Random();
			
			for(int i = 0; i < noOfProcesses; i++){
				arrivalTime[i] = rand.nextInt(noOfProcesses);
				burstTime[i] = rand.nextInt(50);
				priority[i] = rand.nextInt(noOfProcesses);
			} 			
		}else{
			for(int i = 0; i < noOfProcesses; i++){
				System.out.println("Process " +(i+1) + ":" );
				System.out.print("Arrival time: ");
				arrivalTime[i] = scan.nextInt();
				System.out.print("Burst time: ");
				burstTime[i] = scan.nextInt();
				System.out.print("Priority: ");
				priority[i] = scan.nextInt();
			} 
		}

		for(int i = 0; i < noOfProcesses; i++){
			processes[i] = new Process(i+1, arrivalTime[i], burstTime[i], priority[i]);
			System.out.println(" " + (i+1) + " 		" + arrivalTime[i] + "			" + burstTime[i] + "			" + priority[i]);
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
		
		System.out.println("\nPID		Arrival Time 		Burst Time		Priority");
		for(int i = 0; i < noOfProcesses; i++){
			System.out.println(" " + processes[i].getId() + " 		" + processes[i].getArrivalTime() + "			" + processes[i].getBurstTime() +
													"			" + processes[i].getPriority());
		}
		System.out.println();
		
		// Uncomment below to start simulation.
		scheduler.initProcesses(processes);
		scheduler.simulate();
	}
}

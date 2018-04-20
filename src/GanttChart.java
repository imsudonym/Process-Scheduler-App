import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class GanttChart extends JFrame{

	private static JPanel fcfsPanel;	
	private static JPanel srtfPanel;
	private static JPanel sjfPanel;	
	private static JPanel preemptivePanel;
	private static JPanel nonpreemptivePanel;	
	private static JPanel roundrobinPanel;	
	
	private static JPanel fcfsTimePanel;
	private static JPanel srtfTimePanel;
	private static JPanel sjfTimePanel;
	private static JPanel nonpreemptiveTimePanel;
	private static JPanel preemptiveTimePanel;
	private static JPanel roundrobinTimePanel;
	
	private static JPanel preemptiveInnerPanel[];
	private static JPanel srtfInnerPanel[];
	
	public static int preemptiveInnerCounter = 0;
	public static int srtfInnerCounter = 0;
	
	public JLabel processArrivedLabel;
	public JLabel srtfLabel;
	public JLabel sjfLabel;
	public JLabel preemptiveLabel;
	public JLabel nonpreemptiveLabel;
	public JLabel roundrobinLabel;	
	
	private static JLabel[] fcfsTimeLabel = new JLabel[50];
	private static JLabel[] srtfTimeLabel = new JLabel[50];
	private static JLabel[] sjfTimeLabel = new JLabel[50];
	private static JLabel[] nonpreemptiveTimeLabel = new JLabel[50];
	private static JLabel[] preemptiveTimeLabel = new JLabel[50];
	private static JLabel[] roundrobinTimeLabel = new JLabel[50];
	
	private static Font font = new Font("Helvetica", Font.BOLD, 20);
	private static Font timeLabelFont = new Font("Helvetica", Font.BOLD, 12);
	
	private static Container con;
	
	private static int y = -1;			
	private static int xFCFS = -1;
	private static int xSRTF = -1;
	private static int xSJF = -1;
	private static int xNP = -1;
	private static int xP = -1;
	private static int xRR = -1;
	
	private static int prevProcLengthName = -1;
	private static int prevFCFSBurstLength = -1;
	private static int prevRRBurstLength = -1;
	private static int prevSJFBurstLength = -1;
	private static int prevNonPreemptiveBurstLength = -1;
	private static int prevPreemptiveBurstLength = -1;
	private static int prevSRTFBurstLength = -1;		
	private static int prevNPBurstLength = -1;
	
	private int queue_ypos = -1;
	
	private static Color darkBlue = new Color(0, 46, 70);
	
	private static int fcfsTimeCounter = 0;
	private static int roundrobinTimeCounter = 0;
	private static int sjfTimeCounter = 0;
	private static int nonpreemptiveTimeCounter = 0;
	private static int preemptiveTimeCounter = 0;
	private static int srtfTimeCounter = 0;
	
	
	private static int fcfsTimeLapse = 0;
	private static int roundrobinTimeLapse = 0;
	private static int sjfTimeLapse = 0;
	private static int nonpreemptiveTimeLapse = 0;
	private static int preemptiveTimeLapse = 0;
	private static int srtfTimeLapse = 0;
	private static int npTimeLapse = 0;

	public GanttChart(){
		super("CPU Scheduling Gantt Chart");		
		setExtendedState(MAXIMIZED_BOTH);
		con = getContentPane();
		con.setBackground(new Color(0, 46, 70));
		con.setLayout(null);
		
		preemptiveInnerPanel = new JPanel[50];
		srtfInnerPanel = new JPanel[50];
	}
	
	public void init(int[] algorithms, long[] quanta){
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();						
		
		processArrivedLabel = new JLabel("ARRIVED: ");
		processArrivedLabel.setFont(font);
		processArrivedLabel.setForeground(Color.WHITE);
		processArrivedLabel.setBounds(100, 70, 100, 20);
		add(processArrivedLabel);									
		
		for(int i = 0; i < algorithms.length; i++){
			
			if (queue_ypos < 0) {
				queue_ypos = 150;					
			} else {
				queue_ypos += 70 + 20;
			}
			
			if(algorithms[i] == SchedulingAlgorithm.FCFS){								
				
				TitledBorder title = BorderFactory.createTitledBorder(
		                loweredbevel, "FCFS", TitledBorder.ABOVE_TOP, TitledBorder.ABOVE_TOP, font, Color.WHITE);
				
				fcfsPanel = new JPanel();
				fcfsPanel.setLayout(null);
				fcfsPanel.setBorder(title);
				fcfsPanel.setBackground(darkBlue);
				fcfsPanel.setBounds(100, queue_ypos, 1150, 70);
				
				fcfsTimePanel = new JPanel();
				fcfsTimePanel.setLayout(null);
				fcfsTimePanel.setBackground(darkBlue);
				fcfsTimePanel.setBounds(100, queue_ypos + 70, 1150, 20);																				
				
				add(fcfsPanel);
				add(fcfsTimePanel);
				
			}else if (algorithms[i] == SchedulingAlgorithm.RR){
				
				TitledBorder title = BorderFactory.createTitledBorder(
		                loweredbevel, "Round Robin (q = " + (quanta[i]/1000) + ")", TitledBorder.ABOVE_TOP, TitledBorder.ABOVE_TOP, font, Color.WHITE);
				
				roundrobinPanel = new JPanel();
				roundrobinPanel.setLayout(null);
				roundrobinPanel.setBorder(title);
				roundrobinPanel.setBackground(darkBlue);
				roundrobinPanel.setBounds(100, queue_ypos, 1150, 70);		
				
				roundrobinTimePanel = new JPanel();
				roundrobinTimePanel.setLayout(null);
				roundrobinTimePanel.setBackground(darkBlue);
				roundrobinTimePanel.setBounds(100, queue_ypos + 70, 1150, 20);															
								
				add(roundrobinTimePanel);
				add(roundrobinPanel);				
			}else if (algorithms[i] == SchedulingAlgorithm.SJF){
				
				TitledBorder title = BorderFactory.createTitledBorder(
		                loweredbevel, "SJF", TitledBorder.ABOVE_TOP, TitledBorder.ABOVE_TOP, font, Color.WHITE);
				
				sjfPanel = new JPanel();
				sjfPanel.setLayout(null);
				sjfPanel.setBorder(title);
				sjfPanel.setBackground(darkBlue);
				sjfPanel.setBounds(100, queue_ypos, 1150, 70);		
				
				sjfTimePanel = new JPanel();
				sjfTimePanel.setLayout(null);
				sjfTimePanel.setBackground(darkBlue);
				sjfTimePanel.setBounds(100, queue_ypos + 70, 1150, 20);														
								
				add(sjfTimePanel);
				add(sjfPanel);				
			}else if (algorithms[i] == SchedulingAlgorithm.NP_PRIO){
				
				TitledBorder title = BorderFactory.createTitledBorder(loweredbevel, 
						"NonPreemptive Priority Queue", TitledBorder.ABOVE_TOP, TitledBorder.ABOVE_TOP, font, Color.WHITE);

				
				nonpreemptivePanel = new JPanel();
				nonpreemptivePanel.setLayout(null);
				nonpreemptivePanel.setBorder(title);
				nonpreemptivePanel.setBackground(darkBlue);
				nonpreemptivePanel.setBounds(100, queue_ypos, 1150, 70);		
				
				nonpreemptiveTimePanel = new JPanel();
				nonpreemptiveTimePanel.setLayout(null);
				nonpreemptiveTimePanel.setBackground(darkBlue);
				nonpreemptiveTimePanel.setBounds(100, queue_ypos + 70, 1150, 20);														
								
				add(nonpreemptiveTimePanel);
				add(nonpreemptivePanel);				
			}else if (algorithms[i] == SchedulingAlgorithm.PRIO){
				
				TitledBorder title = BorderFactory.createTitledBorder(
		                loweredbevel, "Preemptive", TitledBorder.ABOVE_TOP, TitledBorder.ABOVE_TOP, font, Color.WHITE);
				
				preemptivePanel = new JPanel();
				preemptivePanel.setLayout(null);
				preemptivePanel.setBorder(title);
				preemptivePanel.setBackground(darkBlue);
				preemptivePanel.setBounds(100, queue_ypos, 1150, 70);		
				
				preemptiveTimePanel = new JPanel();
				preemptiveTimePanel.setLayout(null);
				preemptiveTimePanel.setBackground(darkBlue);
				preemptiveTimePanel.setBounds(100, queue_ypos + 70, 1150, 20);														
								
				add(preemptiveTimePanel);
				add(preemptivePanel);				
			}else if (algorithms[i] == SchedulingAlgorithm.SRTF){
				
				TitledBorder title = BorderFactory.createTitledBorder(
		                loweredbevel, "SRTF", TitledBorder.ABOVE_TOP, TitledBorder.ABOVE_TOP, font, Color.WHITE);
				
				srtfPanel = new JPanel();
				srtfPanel.setLayout(null);
				srtfPanel.setBorder(title);
				srtfPanel.setBackground(darkBlue);
				srtfPanel.setBounds(100, queue_ypos, 1150, 70);		
				
				srtfTimePanel = new JPanel();
				srtfTimePanel.setLayout(null);
				srtfTimePanel.setBackground(darkBlue);
				srtfTimePanel.setBounds(100, queue_ypos + 70, 1150, 20);														
								
				add(srtfTimePanel);
				add(srtfPanel);								
			}
		}	
	}
	
	public static void addExecutingProcess(int processId, long executionTime, int algorithm) {
						
		Container container = null;		
		int burstLength = (int)(executionTime/100);
		int procLength = (burstLength < 20)? 20: burstLength;
		String processName = "p" + processId;		
		
		JPanel comp = new JPanel();		
		JLabel label = new JLabel(processName, JLabel.CENTER);		
		comp.add(label);
		
		if( algorithm == SchedulingAlgorithm.FCFS){
			container = fcfsPanel;
			
			if(prevFCFSBurstLength < 0){
				xFCFS = 4;
				y = 29;				
			}else{							
				xFCFS += prevFCFSBurstLength + 1;											
			}
			
			fcfsTimeLabel[fcfsTimeCounter] = new JLabel("" + (fcfsTimeLapse/1000));
			fcfsTimeLabel[fcfsTimeCounter].setFont(timeLabelFont);
			fcfsTimeLabel[fcfsTimeCounter].setForeground(Color.WHITE);
			fcfsTimeLabel[fcfsTimeCounter].setBounds(xFCFS-5, 0, 50, 10);
			
			fcfsTimePanel.add(fcfsTimeLabel[fcfsTimeCounter++]);
									
			fcfsTimeLapse += executionTime;
			prevFCFSBurstLength = burstLength + ((burstLength < 20)? 20-burstLength : 0);					
			comp.setBounds(xFCFS, y, procLength, 37);
			fcfsTimePanel.repaint();
			
		} else if (algorithm == SchedulingAlgorithm.RR) {
			container = roundrobinPanel;
			
			if(prevRRBurstLength < 0){
				xRR = 4;
				y = 29;				
			}else{				
				xRR += prevRRBurstLength + 1;								
			}
			
			roundrobinTimeLabel[roundrobinTimeCounter] = new JLabel("" + (roundrobinTimeLapse/1000));
			roundrobinTimeLabel[roundrobinTimeCounter].setFont(timeLabelFont);
			roundrobinTimeLabel[roundrobinTimeCounter].setForeground(Color.WHITE);
			roundrobinTimeLabel[roundrobinTimeCounter].setBounds(xRR-5, 0, 50, 10);
			
			roundrobinTimePanel.add(roundrobinTimeLabel[roundrobinTimeCounter++]);
			
			roundrobinTimeLapse += executionTime;
			prevRRBurstLength = burstLength + ((burstLength < 20)? 20-burstLength : 0);					
			comp.setBounds(xRR, y, procLength, 37);	
			roundrobinTimePanel.repaint();
			
		} else if (algorithm == SchedulingAlgorithm.SJF) {
			container = sjfPanel;
			
			if(prevSJFBurstLength < 0){
				xSJF = 4;
				y = 29;		
			}else{						
				xSJF += prevSJFBurstLength + 1;											
			}
			
			sjfTimeLabel[sjfTimeCounter] = new JLabel("" + (sjfTimeLapse/1000));
			sjfTimeLabel[sjfTimeCounter].setFont(timeLabelFont);
			sjfTimeLabel[sjfTimeCounter].setForeground(Color.WHITE);
			sjfTimeLabel[sjfTimeCounter].setBounds(xSJF-5, 0, 50, 10);
			
			sjfTimePanel.add(sjfTimeLabel[sjfTimeCounter++]);						
			
			sjfTimeLapse += executionTime;
			prevSJFBurstLength = burstLength + ((burstLength < 20)? 20-burstLength : 0);
			comp.setBounds(xSJF, y, procLength, 37);
			sjfTimePanel.repaint();					
			
		} else if (algorithm == SchedulingAlgorithm.NP_PRIO) {
			container = nonpreemptivePanel;
			
			if(prevNonPreemptiveBurstLength < 0){
				xNP = 4;
				y = 29;		
			}else{						
				xNP += prevNonPreemptiveBurstLength + 1;											
			}
			
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter] = new JLabel("" + (nonpreemptiveTimeLapse/1000));
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter].setFont(timeLabelFont);
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter].setForeground(Color.WHITE);
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter].setBounds(xNP-5, 0, 50, 10);
			
			nonpreemptiveTimePanel.add(nonpreemptiveTimeLabel[nonpreemptiveTimeCounter++]);						
			
			nonpreemptiveTimeLapse += executionTime;
			prevNonPreemptiveBurstLength = burstLength + ((burstLength < 20)? 20-burstLength : 0);
			comp.setBounds(xNP, y, procLength, 37);
			nonpreemptiveTimePanel.repaint();
			
		} else if (algorithm == SchedulingAlgorithm.PRIO) {
			container = preemptivePanel;
			preemptiveInnerPanel[preemptiveInnerCounter] = new JPanel();
			JLabel preemptiveLabel = new JLabel(processName, JLabel.CENTER);		
			preemptiveInnerPanel[preemptiveInnerCounter].add(preemptiveLabel);
			
			if(prevPreemptiveBurstLength < 0){
				xP = 4;
				y = 29;		
			}else{						
				xP += prevPreemptiveBurstLength + 1;											
			}
			
			preemptiveTimeLabel[preemptiveTimeCounter] = new JLabel("" + (preemptiveTimeLapse/1000));
			preemptiveTimeLabel[preemptiveTimeCounter].setFont(timeLabelFont);
			preemptiveTimeLabel[preemptiveTimeCounter].setForeground(Color.WHITE);
			preemptiveTimeLabel[preemptiveTimeCounter].setBounds(xP-5, 0, 50, 10);
			
			preemptiveTimePanel.add(preemptiveTimeLabel[preemptiveTimeCounter++]);
						
			
			preemptiveTimeLapse += executionTime;
			prevPreemptiveBurstLength = burstLength + ((burstLength < 20)? 20-burstLength : 0);
			preemptiveInnerPanel[preemptiveInnerCounter].setBounds(xP, y, procLength, 37);			
			preemptiveTimePanel.repaint();
			
			preemptiveInnerPanel[preemptiveInnerCounter].setBackground(new Color(0, 183, 0));		
			container.add(preemptiveInnerPanel[preemptiveInnerCounter++]);
			
		} else if (algorithm == SchedulingAlgorithm.SRTF) {
			container = srtfPanel;
			srtfInnerPanel[srtfInnerCounter] = new JPanel();
			JLabel srtfLabel = new JLabel(processName, JLabel.CENTER);		
			srtfInnerPanel[srtfInnerCounter].add(srtfLabel);
			
			if(prevSRTFBurstLength < 0){
				xSRTF = 4;
				y = 29;		
			}else{						
				xSRTF += prevSRTFBurstLength + 1;											
			}
			
			srtfTimeLabel[srtfTimeCounter] = new JLabel("" + (srtfTimeLapse/1000));
			srtfTimeLabel[srtfTimeCounter].setFont(timeLabelFont);
			srtfTimeLabel[srtfTimeCounter].setForeground(Color.WHITE);
			srtfTimeLabel[srtfTimeCounter].setBounds(xSRTF-5, 0, 50, 10);
			
			srtfTimePanel.add(srtfTimeLabel[srtfTimeCounter++]);						
			
			srtfTimeLapse += executionTime;
			prevSRTFBurstLength = burstLength + ((burstLength < 20)? 20-burstLength : 0);
			srtfInnerPanel[srtfInnerCounter].setBounds(xSRTF, y, procLength, 37);
			srtfTimePanel.repaint();	
			
			srtfInnerPanel[srtfInnerCounter].setBackground(new Color(0, 183, 0));		
			container.add(srtfInnerPanel[srtfInnerCounter++]);
		}
							
		if(algorithm != SchedulingAlgorithm.PRIO){
			comp.setBackground(new Color(0, 183, 0));		
			container.add(comp);
		}
										
		container.repaint();
		con.repaint();
		con.revalidate();
	}
	
	public static void addNewArrivedProcess(int processId, long arrivalTime, long burstTime){							
		
		if(prevProcLengthName < 0){			
			prevProcLengthName = 200;
		}else{
			prevProcLengthName += 25;
		}
		
		String processName = "p" + processId;
		
		JLabel label = new JLabel(processName);
		label.setFont(font);
		label.setForeground(Color.WHITE);
		label.setBounds(prevProcLengthName, 70, 50, 20);
		con.add(label);
		
		prevProcLengthName += processName.length();
		
		con.repaint();
		con.revalidate();
	}
	
	public static void addLastCompletionTime(int algorithm){
		if(algorithm == SchedulingAlgorithm.FCFS){			
			fcfsTimeLabel[fcfsTimeCounter] = new JLabel("" + (fcfsTimeLapse/1000));
			fcfsTimeLabel[fcfsTimeCounter].setFont(timeLabelFont);
			fcfsTimeLabel[fcfsTimeCounter].setForeground(Color.WHITE);
			fcfsTimeLabel[fcfsTimeCounter].setBounds(xFCFS + prevFCFSBurstLength - 4, 0, 50, 10);
			
			fcfsTimePanel.add(fcfsTimeLabel[fcfsTimeCounter++]);
			fcfsTimePanel.repaint();
		}else if(algorithm == SchedulingAlgorithm.RR){			
			roundrobinTimeLabel[roundrobinTimeCounter] = new JLabel("" + (roundrobinTimeLapse/1000));
			roundrobinTimeLabel[roundrobinTimeCounter].setFont(timeLabelFont);
			roundrobinTimeLabel[roundrobinTimeCounter].setForeground(Color.WHITE);
			roundrobinTimeLabel[roundrobinTimeCounter].setBounds(xRR + prevRRBurstLength - 4, 0, 50, 10);
			
			roundrobinTimePanel.add(roundrobinTimeLabel[roundrobinTimeCounter++]);
			roundrobinTimePanel.repaint();
		}else if(algorithm == SchedulingAlgorithm.SJF){			
			sjfTimeLabel[sjfTimeCounter] = new JLabel("" + (sjfTimeLapse/1000));
			sjfTimeLabel[sjfTimeCounter].setFont(timeLabelFont);
			sjfTimeLabel[sjfTimeCounter].setForeground(Color.WHITE);
			sjfTimeLabel[sjfTimeCounter].setBounds(xSJF + prevSJFBurstLength - 4, 0, 50, 10);
			
			sjfTimePanel.add(sjfTimeLabel[sjfTimeCounter++]);
			sjfTimePanel.repaint();
		}else if(algorithm == SchedulingAlgorithm.NP_PRIO){			
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter] = new JLabel("" + (nonpreemptiveTimeLapse/1000));
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter].setFont(timeLabelFont);
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter].setForeground(Color.WHITE);
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter].setBounds(xNP + prevNonPreemptiveBurstLength - 4, 0, 50, 10);
			
			nonpreemptiveTimePanel.add(nonpreemptiveTimeLabel[nonpreemptiveTimeCounter++]);
			nonpreemptiveTimePanel.repaint();
		}else if(algorithm == SchedulingAlgorithm.PRIO){			
			preemptiveTimeLabel[preemptiveTimeCounter] = new JLabel("" + (preemptiveTimeLapse/1000));
			preemptiveTimeLabel[preemptiveTimeCounter].setFont(timeLabelFont);
			preemptiveTimeLabel[preemptiveTimeCounter].setForeground(Color.WHITE);
			preemptiveTimeLabel[preemptiveTimeCounter].setBounds(xP + prevPreemptiveBurstLength - 4, 0, 50, 10);
			
			preemptiveTimePanel.add(preemptiveTimeLabel[preemptiveTimeCounter++]);
			preemptiveTimePanel.repaint();
		}else if(algorithm == SchedulingAlgorithm.SRTF){			
			srtfTimeLabel[srtfTimeCounter] = new JLabel("" + (srtfTimeLapse/1000));
			srtfTimeLabel[srtfTimeCounter].setFont(timeLabelFont);
			srtfTimeLabel[srtfTimeCounter].setForeground(Color.WHITE);
			srtfTimeLabel[srtfTimeCounter].setBounds(xSRTF + prevSRTFBurstLength - 4, 0, 50, 10);
			
			srtfTimePanel.add(srtfTimeLabel[srtfTimeCounter++]);
			srtfTimePanel.repaint();
		}
		
	}

	public static void updatePreemptedProcess(int index, long prevBurst, long burstLeft, int algorithm) {		
		Container container = null;				
		int burstLength = (int)burstLeft;
		int procLength = (burstLength < 20)? 20: burstLength;				
		
		if(algorithm == SchedulingAlgorithm.PRIO){
			container = preemptivePanel;
			
			preemptiveTimeLapse -= prevBurst;
			preemptiveTimeLapse += burstLeft;
			
			preemptiveInnerPanel[index].setBackground(Color.RED);						
			preemptiveInnerPanel[index].setSize(new Dimension(procLength, 37));														
						
			preemptiveTimeLabel[preemptiveTimeCounter] = new JLabel("" + (preemptiveTimeLapse/1000));
			preemptiveTimeLabel[preemptiveTimeCounter].setFont(timeLabelFont);
			preemptiveTimeLabel[preemptiveTimeCounter].setForeground(Color.WHITE);
			preemptiveTimeLabel[preemptiveTimeCounter].setBounds(xP-5, 0, 50, 10);
											
			prevPreemptiveBurstLength = burstLength + ((burstLength < 20)? 20-burstLength : 0);									
			
			preemptiveInnerPanel[preemptiveInnerCounter-1].repaint();
			preemptiveTimePanel.repaint();			
			
		}else if(algorithm == SchedulingAlgorithm.SRTF){
			container = srtfPanel;
			
			srtfTimeLapse -= prevBurst;
			srtfTimeLapse += burstLeft;
			
			System.out.println("  srtfTimeLapse: " + srtfTimeLapse);
			srtfInnerPanel[index].setBackground(Color.RED);						
			srtfInnerPanel[index].setSize(new Dimension(procLength, 37));														
						
			srtfTimeLabel[srtfTimeCounter] = new JLabel("" + srtfTimeLapse);
			srtfTimeLabel[srtfTimeCounter].setFont(timeLabelFont);
			srtfTimeLabel[srtfTimeCounter].setForeground(Color.PINK);
			srtfTimeLabel[srtfTimeCounter].setBounds(xP-5, 0, 50, 10);
											
			prevSRTFBurstLength = burstLength + ((burstLength < 20)? 20-burstLength : 0);									
			
			srtfInnerPanel[srtfInnerCounter-1].repaint();
			srtfTimePanel.repaint();				
		}
																
		container.repaint();
		con.repaint();
		con.revalidate();
	}
}
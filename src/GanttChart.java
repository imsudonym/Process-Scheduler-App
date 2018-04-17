import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class GanttChart extends JFrame{
	public static JPanel fcfsPanel;
	public static JPanel fcfsTimePanel;
	public static JPanel srtfPanel;
	public static JPanel sjfPanel;
	public static JPanel preemptivePanel;
	public static JPanel nonpreemptivePanel;
	public static JPanel roundrobinPanel;
	public static JPanel roundrobinTimePanel;
	
	public JLabel processArrivedLabel;
	public JLabel srtfLabel;
	public JLabel sjfLabel;
	public JLabel preemptiveLabel;
	public JLabel nonpreemptiveLabel;
	public JLabel roundrobinLabel;	
	
	private static JLabel[] fcfsTimeLabel = new JLabel[50];
	private static JLabel[] roundrobinTimeLabel = new JLabel[50];
	
	private static Font font = new Font("Helvetica", Font.BOLD, 20);
	private static Font timeLabelFont = new Font("Helvetica", Font.BOLD, 12);
	
	private static Container con;
	
	private static int xRR = -1;
	private static int xFCFS = -1;
	private static int y = -1;	
	private static int prevProcLengthName = -1;
	private static int prevFCFSBurstLength = -1;
	private static int prevRRBurstLength = -1;
	private int queue_ypos = -1;
		
	private static Color darkBlue = new Color(0, 46, 70);
	private static int fcfsTimeCounter = 0;
	private static int roundrobinTimeCounter = 0;
	private static int fcfsTimeLapse = 0;
	private static int roundrobinTimeLapse = 0;
	
	public GanttChart(){
		super("CPU Scheduling Gantt Chart");		
		setExtendedState(MAXIMIZED_BOTH);
		con = getContentPane();
		con.setBackground(new Color(0, 46, 70));
		con.setLayout(null);				
	}
	
	public void init(int[] algorithms){
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();						
		
		processArrivedLabel = new JLabel("ARRIVED: ");
		processArrivedLabel.setFont(font);
		processArrivedLabel.setForeground(Color.WHITE);
		processArrivedLabel.setBounds(100, 70, 100, 20);
		add(processArrivedLabel);			
				
		fcfsTimeLabel[fcfsTimeCounter] = new JLabel("0");
		fcfsTimeLabel[fcfsTimeCounter].setFont(timeLabelFont);
		fcfsTimeLabel[fcfsTimeCounter].setForeground(Color.WHITE);
		fcfsTimeLabel[fcfsTimeCounter++].setBounds(4, 0, 10, 10);
		
		roundrobinTimeLabel[roundrobinTimeCounter] = new JLabel("0");			
		roundrobinTimeLabel[roundrobinTimeCounter].setFont(timeLabelFont);
		roundrobinTimeLabel[roundrobinTimeCounter].setForeground(Color.WHITE);
		roundrobinTimeLabel[roundrobinTimeCounter++].setBounds(4, 0, 10, 10);
		
		for(int i = 0; i < algorithms.length; i++){
			
			if (queue_ypos < 0) {
				queue_ypos = 150;					
			} else {
				queue_ypos += 70 + 20;
			}
			
			if(algorithms[i] == SchedulingAlgorithm.FCFS){				
				
				System.out.println("Hi FCFS");
				
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
				
				fcfsTimePanel.add(fcfsTimeLabel[0]);
				
				add(fcfsPanel);
				add(fcfsTimePanel);
				
			}
			if (algorithms[i] == SchedulingAlgorithm.RR){
				
				TitledBorder title = BorderFactory.createTitledBorder(
		                loweredbevel, "Round Robin (q= )", TitledBorder.ABOVE_TOP, TitledBorder.ABOVE_TOP, font, Color.WHITE);
				
				roundrobinPanel = new JPanel();
				roundrobinPanel.setLayout(null);
				roundrobinPanel.setBorder(title);
				roundrobinPanel.setBackground(darkBlue);
				roundrobinPanel.setBounds(100, queue_ypos, 1150, 70);		
				
				roundrobinTimePanel = new JPanel();
				roundrobinTimePanel.setLayout(null);
				roundrobinTimePanel.setBackground(darkBlue);
				roundrobinTimePanel.setBounds(100, queue_ypos + 70, 1150, 20);											
				
				roundrobinTimePanel.add(roundrobinTimeLabel[0]);
								
				add(roundrobinTimePanel);
				add(roundrobinPanel);				
			}
		}
		
		/*srtfPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		srtfPanel.setLayout(null);
		srtfPanel.setBackground(new Color(0, 46, 70));
		add(srtfPanel);
		
		sjfPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		sjfPanel.setLayout(null);
		sjfPanel.setBackground(new Color(0, 46, 70));
		add(sjfPanel);
		
		preemptivePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		preemptivePanel.setLayout(null);
		preemptivePanel.setBackground(new Color(0, 46, 70));
		add(preemptivePanel);
		
		nonpreemptivePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		nonpreemptivePanel.setLayout(null);
		nonpreemptivePanel.setBackground(new Color(0, 46, 70));
		add(nonpreemptivePanel);
		
		roundrobinPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		roundrobinPanel.setLayout(null);
		roundrobinPanel.setBackground(new Color(0, 46, 70));
		add(roundrobinPanel);
		
		fcfsLabel = new JLabel("FCFS Algorithm");
		fcfsLabel.setForeground(Color.WHITE);
		fcfsPanel.add(fcfsLabel);
		
		srtfLabel = new JLabel("SRTF Algorithm");
		srtfLabel.setForeground(Color.WHITE);
		fcfsPanel.add(srtfLabel);
		
		sjfLabel = new JLabel("SJF Algorithm");
		sjfLabel.setForeground(Color.WHITE);
		fcfsPanel.add(sjfLabel);
		
		preemptiveLabel = new JLabel("Preemptive Algorithm");
		preemptiveLabel.setForeground(Color.WHITE);
		fcfsPanel.add(preemptiveLabel);
		
		nonpreemptiveLabel = new JLabel("Non Preemptive Algorithm");
		nonpreemptiveLabel.setForeground(Color.WHITE);
		fcfsPanel.add(nonpreemptiveLabel);
		
		roundrobinLabel = new JLabel("Round Robin Algorithm");
		roundrobinLabel.setForeground(Color.WHITE);
		fcfsPanel.add(roundrobinLabel);*/
	}
	
	public static void addExecutingProcess(int processId, long burstTime, int algorithm) {
						
		Container container = null;		
		int burstLength = (int)burstTime/100;
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
				
				fcfsTimeLapse += burstTime;
				fcfsTimeLabel[fcfsTimeCounter] = new JLabel("" + (fcfsTimeLapse/1000));
				fcfsTimeLabel[fcfsTimeCounter].setFont(timeLabelFont);
				fcfsTimeLabel[fcfsTimeCounter].setForeground(Color.WHITE);
				fcfsTimeLabel[fcfsTimeCounter].setBounds(xFCFS, 0, 50, 10);
				
				fcfsTimePanel.add(fcfsTimeLabel[fcfsTimeCounter++]);
			}
									
			prevFCFSBurstLength = burstLength;					
			comp.setBounds(xFCFS, y, burstLength, 37);	
			
		} else if (algorithm == SchedulingAlgorithm.RR) {
			container = roundrobinPanel;
			
			if(prevRRBurstLength < 0){
				xRR = 4;
				y = 29;				
			}else{				
				xRR += prevRRBurstLength + 1;
				roundrobinTimeLapse += burstTime;
				roundrobinTimeLabel[roundrobinTimeCounter] = new JLabel("" + (roundrobinTimeLapse/1000));
				roundrobinTimeLabel[roundrobinTimeCounter].setFont(timeLabelFont);
				roundrobinTimeLabel[roundrobinTimeCounter].setForeground(Color.WHITE);
				roundrobinTimeLabel[roundrobinTimeCounter].setBounds(xRR, 0, 50, 10);
				
				roundrobinTimePanel.add(roundrobinTimeLabel[roundrobinTimeCounter++]);
			}
						
			prevRRBurstLength = burstLength;			
			comp.setBounds(xRR, y, burstLength, 37);	
		}			
								
		comp.setBackground(new Color(0, 183, 0));		
		container.add(comp);
				
		//fcfsTimePanel.repaint();		
		container.repaint();
		con.repaint();
		con.revalidate();
	}
	
	public static void addNewArrivedProcess(String processName, long arrivalTime, long burstTime){							
		
		if(prevProcLengthName < 0){			
			prevProcLengthName = 200;
		}else{
			prevProcLengthName += 25;
		}
		
		JLabel label = new JLabel(processName);
		label.setFont(font);
		label.setForeground(Color.WHITE);
		label.setBounds(prevProcLengthName, 70, 50, 20);
		con.add(label);
		
		prevProcLengthName += processName.length();
		
		con.repaint();
		con.revalidate();
	}
}
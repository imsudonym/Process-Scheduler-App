import java.awt.Color;
import java.awt.Container;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class GanttChart extends JFrame{
	public static JPanel fcfsPanel;
	public static JPanel srtfPanel;
	public static JPanel sjfPanel;
	public static JPanel preemptivePanel;
	public static JPanel nonpreemptivePanel;
	public static JPanel roundrobinPanel;
	
	public JLabel processArrivedLabel;
	public JLabel srtfLabel;
	public JLabel sjfLabel;
	public JLabel preemptiveLabel;
	public JLabel nonpreemptiveLabel;
	public JLabel roundrobinLabel;
	
	private static Font font = new Font("Helvetica", Font.BOLD, 20);
	
	private static Container con;
	
	private static int x = -1;
	private static int y = -1;	
	private static int prevProcLengthName = -1;
	private static int prevBurstLength = -1;
	private int queue_ypos = -1;
		
	
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
				fcfsPanel.setBackground(new Color(0, 46, 70));
				fcfsPanel.setBounds(100, queue_ypos, 1150, 70);				
				add(fcfsPanel);
				
			} else if (algorithms[i] == SchedulingAlgorithm.RR){
				
				TitledBorder title = BorderFactory.createTitledBorder(
		                loweredbevel, "Round Robin (q= )", TitledBorder.ABOVE_TOP, TitledBorder.ABOVE_TOP, font, Color.WHITE);
				
				roundrobinPanel = new JPanel();
				roundrobinPanel.setLayout(null);
				roundrobinPanel.setBorder(title);
				roundrobinPanel.setBackground(new Color(0, 46, 70));
				roundrobinPanel.setBounds(100, 150, 1150, 70);		
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
		
		if( algorithm == SchedulingAlgorithm.FCFS){
			container = fcfsPanel;			
		} else if (algorithm == SchedulingAlgorithm.RR) {
			container = roundrobinPanel;
		}
		
		if(prevBurstLength < 0){
			x = 4;
			y = 29;				
		}else{				
			x += prevBurstLength + 1;
		}
		
		prevBurstLength = burstLength;
				
		JPanel comp = new JPanel();		
		JLabel label = new JLabel(processName, JLabel.CENTER);
		comp.add(label);
		comp.setBackground(new Color(0, 183, 0));
		comp.setBounds(x, y, burstLength, 37);		
		container.add(comp);
				
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

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
	
	private static JPanel pcbPanel;
	private static JPanel pcbIdPanel;	
	private static JPanel pcbArrivalPanel;
	private static JPanel pcbBurstPanel;
	private static JPanel pcbPriorityPanel;	
	
	private static int idYOffset;
	private static int arrYOffset;
	private static int burstYOffset;
	private static int prioYOffset;
	
	private static int processCount = 0;
	
	public static int preemptiveInnerCounter = 0;
	public static int srtfInnerCounter = 0;
	
	private static int pcbPanelHeight = 355;
	private static int fcfsPanelWidth = 1150;
	private static int roundrobinPanelWidth = 1150;
	private static int sjfPanelWidth = 1150;	
	private static int npPanelWidth = 1150;
	private static int pPanelWidth = 1150;
	private static int srtfPanelWidth = 1150;	
		
	public JLabel srtfLabel;
	public JLabel sjfLabel;
	public JLabel preemptiveLabel;
	public JLabel nonpreemptiveLabel;
	public JLabel roundrobinLabel;	
	
	private static JLabel[] fcfsTimeLabel = new JLabel[100];
	private static JLabel[] srtfTimeLabel = new JLabel[100];
	private static JLabel[] sjfTimeLabel = new JLabel[100];
	private static JLabel[] nonpreemptiveTimeLabel = new JLabel[100];
	private static JLabel[] preemptiveTimeLabel = new JLabel[100];
	private static JLabel[] roundrobinTimeLabel = new JLabel[100];
	
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
	
	private static int prevFCFSBurstLength = -1;
	private static int prevRRBurstLength = -1;
	private static int prevSJFBurstLength = -1;
	private static int prevNonPreemptiveBurstLength = -1;
	private static int prevPreemptiveBurstLength = -1;
	private static int prevSRTFBurstLength = -1;		
	private int queue_ypos = -1;
		
	private static Color darkBlue = new Color(0, 46, 70);
	private static Border border = BorderFactory.createLineBorder(darkBlue);
	
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
	public GanttChart(){
		super("CPU Scheduling Gantt Chart");		
		setExtendedState(MAXIMIZED_BOTH);
		con = getContentPane();
		con.setBackground(Color.WHITE);
		con.setLayout(null);
	}
	
	public void init(int[] algorithms, int[] respectiveQuantum){
		for(int i = 0; i < algorithms.length; i++){
			
			if (queue_ypos < 0) {
				queue_ypos = 150;					
			} else {
				queue_ypos += 70 + 20;
			}
			
			JPanel queue = null;
			JLabel queueTitle = null;
			
			if(algorithms[i] == SchedulingAlgorithm.FCFS){															
												
				Border title = BorderFactory.createLineBorder(darkBlue);
				
				JLabel fcfsTitle = new JLabel("FCFS");
				fcfsTitle.setFont(font);
				fcfsTitle.setBounds(110, queue_ypos-40, 100, 50);				
				
				fcfsPanel = new JPanel();
				fcfsPanel.setLayout(null);
				fcfsPanel.setBackground(Color.LIGHT_GRAY);
				fcfsPanel.setBorder(title);
				fcfsPanel.setPreferredSize(new Dimension(fcfsPanelWidth-5, 73));	
				
				fcfsTimePanel = new JPanel();
				fcfsTimePanel.setLayout(null);
				fcfsTimePanel.setBackground(darkBlue);
				fcfsTimePanel.setBounds(1, 51, 1145, 20);																								
				fcfsPanel.add(fcfsTimePanel);
				
				queue = fcfsPanel;
				queueTitle = fcfsTitle;
								
			}else if (algorithms[i] == SchedulingAlgorithm.RR){
				
				Border title = BorderFactory.createLineBorder(darkBlue);
				
				JLabel rrTitle = new JLabel("Round robin (quantum = " + respectiveQuantum[i] + ")");
				rrTitle.setFont(font);
				rrTitle.setBounds(110, queue_ypos-40, 500, 50);											
				
				roundrobinPanel = new JPanel();
				roundrobinPanel.setLayout(null);
				roundrobinPanel.setBorder(title);
				roundrobinPanel.setBackground(Color.LIGHT_GRAY);
				roundrobinPanel.setPreferredSize(new Dimension(roundrobinPanelWidth-5, 73));	
				
				roundrobinTimePanel = new JPanel();
				roundrobinTimePanel.setLayout(null);
				roundrobinTimePanel.setBackground(darkBlue);
				roundrobinTimePanel.setBounds(1, 51, 1145, 20);														
				roundrobinPanel.add(roundrobinTimePanel);				
				
				queue = roundrobinPanel;
				queueTitle = rrTitle;
				
			}else if (algorithms[i] == SchedulingAlgorithm.SJF){
				
				Border title = BorderFactory.createLineBorder(darkBlue);
				
				JLabel sjfTitle = new JLabel("SJF");
				sjfTitle.setFont(font);
				sjfTitle.setBounds(110, queue_ypos-40, 100, 50);					
				
				sjfPanel = new JPanel();
				sjfPanel.setLayout(null);
				sjfPanel.setBorder(title);
				sjfPanel.setBackground(Color.LIGHT_GRAY);
				sjfPanel.setPreferredSize(new Dimension(sjfPanelWidth-5, 73));		
				
				sjfTimePanel = new JPanel();
				sjfTimePanel.setLayout(null);
				sjfTimePanel.setBackground(darkBlue);
				sjfTimePanel.setBounds(1, 51, 1145, 20);
				sjfPanel.add(sjfTimePanel);
								
				queue = sjfPanel;
				queueTitle = sjfTitle;
				
			}else if (algorithms[i] == SchedulingAlgorithm.NP_PRIO){
				
				JLabel npTitle = new JLabel("NP-PRIO");
				npTitle.setFont(font);
				npTitle.setBounds(110, queue_ypos-40, 500, 50);	
				
				nonpreemptivePanel = new JPanel();
				nonpreemptivePanel.setLayout(null);
				nonpreemptivePanel.setBorder(border);
				nonpreemptivePanel.setBackground(Color.LIGHT_GRAY);
				nonpreemptivePanel.setPreferredSize(new Dimension(npPanelWidth-5, 73));		
				
				nonpreemptiveTimePanel = new JPanel();
				nonpreemptiveTimePanel.setLayout(null);
				nonpreemptiveTimePanel.setBackground(darkBlue);
				nonpreemptiveTimePanel.setBounds(1, 51, 1145, 20);	
				nonpreemptivePanel.add(nonpreemptiveTimePanel);
								
				queue = nonpreemptivePanel;
				queueTitle = npTitle;
				
			}else if (algorithms[i] == SchedulingAlgorithm.PRIO){
				
				JLabel title = new JLabel("PRIO");
				title.setFont(font);
				title.setBounds(110, queue_ypos-40, 500, 50);	
				
				preemptivePanel = new JPanel();
				preemptivePanel.setLayout(null);
				preemptivePanel.setBorder(border);
				preemptivePanel.setBackground(Color.LIGHT_GRAY);
				preemptivePanel.setBounds(100, queue_ypos, 1150, 70);		
				
				preemptiveTimePanel = new JPanel();
				preemptiveTimePanel.setLayout(null);
				preemptiveTimePanel.setBackground(darkBlue);
				preemptiveTimePanel.setBounds(1, 51, 1145, 20);			
				preemptivePanel.add(preemptiveTimePanel);
								
				queue = preemptivePanel;
				queueTitle = title;
				
			}else if (algorithms[i] == SchedulingAlgorithm.SRTF){
				
				JLabel title = new JLabel("SRTF");
				title.setFont(font);
				title.setBounds(110, queue_ypos-40, 500, 50);
				
				srtfPanel = new JPanel();
				srtfPanel.setLayout(null);
				srtfPanel.setBorder(border);
				srtfPanel.setBackground(Color.LIGHT_GRAY);
				srtfPanel.setBounds(100, queue_ypos, 1150, 70);		
				
				srtfTimePanel = new JPanel();
				srtfTimePanel.setLayout(null);
				srtfTimePanel.setBackground(darkBlue);
				srtfTimePanel.setBounds(1, 51, 1145, 20);		
				srtfPanel.add(srtfTimePanel);											
								
				queue = srtfPanel;
				queueTitle = title;								
			}
			
			JScrollPane scrollPane = new JScrollPane(queue);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			scrollPane.setBounds(100, queue_ypos, fcfsPanelWidth, 85);
			
			add(queueTitle);
			add(scrollPane);			
		}	
				
		
		pcbPanel = new JPanel(new GridLayout(1, 4));
		pcbPanel.setBorder(border);		
		pcbPanel.setPreferredSize(new Dimension(575, pcbPanelHeight));		
		
		JScrollPane pcbScrollPane = new JScrollPane(pcbPanel);
		pcbScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pcbScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pcbScrollPane.setBounds(100, 320, 575, 360);
		add(pcbScrollPane);
		
		pcbIdPanel = new JPanel(null);
		pcbIdPanel.setBorder(border);		
		pcbIdPanel.setSize(144, 360);
		
		JPanel idLabelPanel = new JPanel();
		idLabelPanel.setBounds(1, 1, 141, 25);
		idLabelPanel.setBackground(Color.ORANGE);
		idLabelPanel.add(new JLabel("PID"));
		pcbIdPanel.add(idLabelPanel);		
		pcbPanel.add(pcbIdPanel);		
		
		pcbArrivalPanel = new JPanel(null);
		pcbArrivalPanel.setBorder(border);		
		pcbArrivalPanel.setSize(144, 360);		
		
		JPanel arrivalLabelPanel = new JPanel();
		arrivalLabelPanel.setBounds(1, 1, 141, 25);
		arrivalLabelPanel.setBackground(Color.ORANGE);
		arrivalLabelPanel.add(new JLabel("ARRIVAL TIME"));
		pcbArrivalPanel.add(arrivalLabelPanel, BorderLayout.NORTH);
		pcbPanel.add(pcbArrivalPanel);
		
		pcbBurstPanel = new JPanel(null);
		pcbBurstPanel.setBorder(border);		
		pcbBurstPanel.setSize(144, 360);
		pcbPanel.add(pcbBurstPanel);
		
		JPanel burstLabelPanel = new JPanel();
		burstLabelPanel.setBounds(1, 1, 141, 25);
		burstLabelPanel.setBackground(Color.ORANGE);
		burstLabelPanel.add(new JLabel("BURST TIME"));
		pcbBurstPanel.add(burstLabelPanel, BorderLayout.NORTH);
		pcbPanel.add(pcbBurstPanel);
		
		pcbPriorityPanel = new JPanel(null);
		pcbPriorityPanel.setBorder(border);		
		pcbPriorityPanel.setSize(144, 360);
		pcbPanel.add(pcbPriorityPanel);		
		
		JPanel priorityLabelPanel = new JPanel();
		priorityLabelPanel.setBounds(1, 1, 141, 25);
		priorityLabelPanel.setBackground(Color.ORANGE);
		priorityLabelPanel.add(new JLabel("PRIORITY"));
		pcbPriorityPanel.add(priorityLabelPanel, BorderLayout.NORTH);
		pcbPanel.add(pcbPriorityPanel);
					
	}
	
	public static void addExecutingProcess(int processId, int executionTime, int algorithm) {
						
		Container container = null;		
		String processName = "p" + processId;		
		
		Border border = BorderFactory.createLineBorder(darkBlue);
		JPanel comp = new JPanel(null);			
		comp.setBorder(border);
		
		JLabel label = new JLabel(processName);
		label.setBounds(18, 18, 30, 15);
		comp.add(label);
		
		if( algorithm == SchedulingAlgorithm.FCFS){
			container = fcfsPanel;
			
			if(fcfsTimeCounter > 21){
				fcfsPanel.setPreferredSize(new Dimension(fcfsPanelWidth += 50, 73));
				fcfsTimePanel.setSize(new Dimension(fcfsPanelWidth, 73));
			}
			
			if(prevFCFSBurstLength < 0){
				xFCFS = 0;
				y = 0;				
			}else{							
				xFCFS += prevFCFSBurstLength;											
			}
			
			fcfsTimeLabel[fcfsTimeCounter] = new JLabel("" + fcfsTimeLapse);
			fcfsTimeLabel[fcfsTimeCounter].setFont(timeLabelFont);
			fcfsTimeLabel[fcfsTimeCounter].setBounds(xFCFS + 1, 2, 30, 15);
			
			fcfsTimePanel.add(fcfsTimeLabel[fcfsTimeCounter++]);
									
			fcfsTimeLapse += executionTime;
			prevFCFSBurstLength = 50;					
			comp.setBounds(xFCFS, y, 50, 51);
			fcfsTimePanel.repaint();
			
		} else if (algorithm == SchedulingAlgorithm.RR) {
			container = roundrobinPanel;
			
			if(roundrobinTimeCounter > 21){
				roundrobinPanel.setPreferredSize(new Dimension(roundrobinPanelWidth += 50, 73));
				roundrobinTimePanel.setSize(new Dimension(roundrobinPanelWidth, 73));
			}
			
			if(prevRRBurstLength < 0){
				xRR = 0;
				y = 0;				
			}else{				
				xRR += prevRRBurstLength;								
			}
			
			roundrobinTimeLabel[roundrobinTimeCounter] = new JLabel("" + roundrobinTimeLapse);
			roundrobinTimeLabel[roundrobinTimeCounter].setFont(timeLabelFont);			
			roundrobinTimeLabel[roundrobinTimeCounter].setBounds(xRR + 1, 2, 30, 15);
			
			roundrobinTimePanel.add(roundrobinTimeLabel[roundrobinTimeCounter++]);
			
			roundrobinTimeLapse += executionTime;
			prevRRBurstLength = 50;					
			comp.setBounds(xRR, y, 50, 51);	
			roundrobinTimePanel.repaint();
			
		} else if (algorithm == SchedulingAlgorithm.SJF) {
			container = sjfPanel;
			
			if(sjfTimeCounter > 21){
				sjfPanel.setPreferredSize(new Dimension(sjfPanelWidth += 50, 73));
				sjfTimePanel.setSize(new Dimension(sjfPanelWidth, 73));
			}
			
			if(prevSJFBurstLength < 0){
				xSJF = 0;
				y = 0;		
			}else{						
				xSJF += prevSJFBurstLength;											
			}
			
			sjfTimeLabel[sjfTimeCounter] = new JLabel("" + sjfTimeLapse);
			sjfTimeLabel[sjfTimeCounter].setFont(timeLabelFont);			
			sjfTimeLabel[sjfTimeCounter].setBounds(xSJF + 1, 2, 30, 15);
			
			sjfTimePanel.add(sjfTimeLabel[sjfTimeCounter++]);						
			
			sjfTimeLapse += executionTime;
			prevSJFBurstLength = 50;
			comp.setBounds(xSJF, y, 50, 51);
			sjfTimePanel.repaint();					
			
		} else if (algorithm == SchedulingAlgorithm.NP_PRIO) {
			container = nonpreemptivePanel;
			
			if(nonpreemptiveTimeCounter > 21){
				nonpreemptivePanel.setPreferredSize(new Dimension(npPanelWidth += 50, 73));
				nonpreemptiveTimePanel.setSize(new Dimension(npPanelWidth, 73));
			}
			
			if(prevNonPreemptiveBurstLength < 0){
				xNP = 0;
				y = 0;		
			}else{						
				xNP += prevNonPreemptiveBurstLength;											
			}
			
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter] = new JLabel("" + nonpreemptiveTimeLapse);
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter].setFont(timeLabelFont);
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter].setBounds(xNP + 1, 2, 30, 15);
			
			nonpreemptiveTimePanel.add(nonpreemptiveTimeLabel[nonpreemptiveTimeCounter++]);						
			
			nonpreemptiveTimeLapse += executionTime;
			prevNonPreemptiveBurstLength = 50;
			comp.setBounds(xNP, y, 50, 51);
			nonpreemptiveTimePanel.repaint();
			
		} else if (algorithm == SchedulingAlgorithm.PRIO) {			
			container = preemptivePanel;
			
			if(preemptiveTimeCounter > 21){
				preemptivePanel.setPreferredSize(new Dimension(pPanelWidth += 50, 73));
				preemptiveTimePanel.setSize(new Dimension(pPanelWidth, 73));
			}
			
			if(prevPreemptiveBurstLength < 0){
				xP = 0;
				y = 0;		
			}else{						
				xP += prevPreemptiveBurstLength;											
			}
			
			preemptiveTimeLabel[preemptiveTimeCounter] = new JLabel("" + preemptiveTimeLapse);
			preemptiveTimeLabel[preemptiveTimeCounter].setFont(timeLabelFont);
			preemptiveTimeLabel[preemptiveTimeCounter].setBounds(xP + 1, 2, 30, 15);
			
			preemptiveTimePanel.add(preemptiveTimeLabel[preemptiveTimeCounter++]);
									
			preemptiveTimeLapse += executionTime;
			prevPreemptiveBurstLength = 50;
			comp.setBounds(xP, y, 50, 51);			
			preemptiveTimePanel.repaint();		
			
		} else if (algorithm == SchedulingAlgorithm.SRTF) {
			container = srtfPanel;
			
			if(srtfTimeCounter > 21){
				srtfPanel.setPreferredSize(new Dimension(srtfPanelWidth += 50, 73));
				srtfTimePanel.setSize(new Dimension(srtfPanelWidth, 73));
			}
			
			if(prevSRTFBurstLength < 0){
				xSRTF = 0;
				y = 0;		
			}else{						
				xSRTF += prevSRTFBurstLength;											
			}
			
			srtfTimeLabel[srtfTimeCounter] = new JLabel("" + srtfTimeLapse);
			srtfTimeLabel[srtfTimeCounter].setFont(timeLabelFont);
			srtfTimeLabel[srtfTimeCounter].setForeground(Color.WHITE);
			srtfTimeLabel[srtfTimeCounter].setBounds(xSRTF + 1, 2, 30, 15);
			
			srtfTimePanel.add(srtfTimeLabel[srtfTimeCounter++]);						
			
			srtfTimeLapse += executionTime;
			prevSRTFBurstLength = 50;
			comp.setBounds(xSRTF, y, 50, 51);
			srtfTimePanel.repaint();				
		}
							
										
		container.add(comp);
		container.repaint();
		con.repaint();
		con.revalidate();
	}
	
	public static void addNewArrivedProcess(int processId, int arrivalTime, int burstTime, int priority){							
				
		processCount++;
		
		if(processCount > 12){
			pcbPanel.setPreferredSize(new Dimension(575, pcbPanelHeight+=25));
		}
		
		JPanel idLabelPanel = new JPanel();
		idLabelPanel.setBounds(1, idYOffset+=25, 141, 25);
		idLabelPanel.setBackground(Color.orange);
		idLabelPanel.add(new JLabel(""+processId));		
		pcbIdPanel.add(idLabelPanel);		

		JPanel arrLabelPanel = new JPanel();
		arrLabelPanel.setBounds(1, arrYOffset+=25, 141, 25);
		arrLabelPanel.setBackground(Color.orange);
		arrLabelPanel.add(new JLabel(""+arrivalTime));		
		pcbArrivalPanel.add(arrLabelPanel);
		
		JPanel burstLabelPanel = new JPanel();
		burstLabelPanel.setBounds(1, burstYOffset+=25, 141, 25);
		burstLabelPanel.setBackground(Color.orange);
		burstLabelPanel.add(new JLabel(""+burstTime));		
		pcbBurstPanel.add(burstLabelPanel);				
		
		JPanel priorityLabelPanel = new JPanel();
		priorityLabelPanel.setBounds(1, prioYOffset+=25, 141, 25);
		priorityLabelPanel.setBackground(Color.orange);
		priorityLabelPanel.add(new JLabel(""+priority));		
		pcbPriorityPanel.add(priorityLabelPanel);
		
		pcbIdPanel.repaint();
		pcbIdPanel.revalidate();	
		pcbArrivalPanel.repaint();
		pcbArrivalPanel.revalidate();
		pcbBurstPanel.repaint();
		pcbBurstPanel.revalidate();
		pcbPriorityPanel.repaint();
		pcbPriorityPanel.revalidate();
	}
	
	public static void addLastCompletionTime(int algorithm){
		if(algorithm == SchedulingAlgorithm.FCFS){			
			fcfsTimeLabel[fcfsTimeCounter] = new JLabel("" + fcfsTimeLapse);
			fcfsTimeLabel[fcfsTimeCounter].setFont(timeLabelFont);
			fcfsTimeLabel[fcfsTimeCounter].setForeground(Color.WHITE);
			fcfsTimeLabel[fcfsTimeCounter].setBounds(xFCFS + prevFCFSBurstLength + 1, 2, 30, 15);			
			fcfsTimePanel.add(fcfsTimeLabel[fcfsTimeCounter++]);
			fcfsTimePanel.repaint();
			
		}else if(algorithm == SchedulingAlgorithm.RR){			
			roundrobinTimeLabel[roundrobinTimeCounter] = new JLabel("" + roundrobinTimeLapse);
			roundrobinTimeLabel[roundrobinTimeCounter].setFont(timeLabelFont);
			roundrobinTimeLabel[roundrobinTimeCounter].setForeground(Color.WHITE);
			roundrobinTimeLabel[roundrobinTimeCounter].setBounds(xRR + prevRRBurstLength + 1, 2, 30, 15);			
			roundrobinTimePanel.add(roundrobinTimeLabel[roundrobinTimeCounter++]);
			roundrobinTimePanel.repaint();
			
		}else if(algorithm == SchedulingAlgorithm.SJF){			
			sjfTimeLabel[sjfTimeCounter] = new JLabel("" + sjfTimeLapse);
			sjfTimeLabel[sjfTimeCounter].setFont(timeLabelFont);
			sjfTimeLabel[sjfTimeCounter].setForeground(Color.WHITE);
			sjfTimeLabel[sjfTimeCounter].setBounds(xSJF + prevSJFBurstLength + 1, 2, 30, 15);			
			sjfTimePanel.add(sjfTimeLabel[sjfTimeCounter++]);
			sjfTimePanel.repaint();
			
		}else if(algorithm == SchedulingAlgorithm.NP_PRIO){			
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter] = new JLabel("" + nonpreemptiveTimeLapse);
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter].setFont(timeLabelFont);
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter].setForeground(Color.WHITE);
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter].setBounds(xNP + prevNonPreemptiveBurstLength + 1, 2, 30, 15);			
			nonpreemptiveTimePanel.add(nonpreemptiveTimeLabel[nonpreemptiveTimeCounter++]);
			nonpreemptiveTimePanel.repaint();
			
		}else if(algorithm == SchedulingAlgorithm.PRIO){			
			preemptiveTimeLabel[preemptiveTimeCounter] = new JLabel("" + preemptiveTimeLapse);
			preemptiveTimeLabel[preemptiveTimeCounter].setFont(timeLabelFont);
			preemptiveTimeLabel[preemptiveTimeCounter].setForeground(Color.WHITE);
			preemptiveTimeLabel[preemptiveTimeCounter].setBounds(xP + prevPreemptiveBurstLength + 1, 2, 30, 15);			
			preemptiveTimePanel.add(preemptiveTimeLabel[preemptiveTimeCounter++]);
			preemptiveTimePanel.repaint();
			
		}else if(algorithm == SchedulingAlgorithm.SRTF){			
			srtfTimeLabel[srtfTimeCounter] = new JLabel("" + srtfTimeLapse);
			srtfTimeLabel[srtfTimeCounter].setFont(timeLabelFont);
			srtfTimeLabel[srtfTimeCounter].setForeground(Color.WHITE);
			srtfTimeLabel[srtfTimeCounter].setBounds(xSRTF + prevSRTFBurstLength + 1, 2, 30, 15);			
			srtfTimePanel.add(srtfTimeLabel[srtfTimeCounter++]);
			srtfTimePanel.repaint();
		}
		
	}
}
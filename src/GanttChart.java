import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GanttChart extends JFrame{

	private static JPanel panel;	
	private static JPanel srtfPanel;
	private static JPanel sjfPanel;	
	private static JPanel preemptivePanel;
	private static JPanel nonpreemptivePanel;	
	private static JPanel roundrobinPanel;		
	
	private static JPanel timePanel;
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
	
	private static int pcbPanelHeight = 355;
	private static int panelWidth = 1150;
	private static int roundrobinPanelWidth = 1150;
	private static int sjfPanelWidth = 1150;	
	private static int npPanelWidth = 1150;
	private static int pPanelWidth = 1150;
	private static int srtfPanelWidth = 1150;	
		
	private static JButton startButton;
	public JLabel srtfLabel;
	public JLabel sjfLabel;
	public JLabel preemptiveLabel;
	public JLabel nonpreemptiveLabel;
	public JLabel roundrobinLabel;	
	
	private JLabel title;
	
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
	
	private JMenuBar menuBar;
	private static JMenu insProcess, setAlgorithm, mlfq, singleQueue;
	
	private boolean MLFQ = false;
	private static boolean alreadyStarted = false;
	private boolean threadStarted = false;
	private int algorithm = SchedulingAlgorithm.FCFS;
	private int quantum = 0;
	
	//private static Scheduler scheduler;
	
	private ArrayList<Integer> PID = new ArrayList<Integer>();
	private ArrayList<Integer> arrivalTime = new ArrayList<Integer>();
	private ArrayList<Integer> burstTime = new ArrayList<Integer>();
	private ArrayList<Integer> priority = new ArrayList<Integer>();
	
	private Process[] processes;
	
	public GanttChart(){
		super("CPU Scheduling Gantt Chart");		
		
		setExtendedState(MAXIMIZED_BOTH);
		con = getContentPane();
		con.setBackground(Color.WHITE);
		con.setLayout(null);
	}
		
	public static void main (String[] args){
		
		GanttChart gantt = new GanttChart();
		gantt.setVisible(true);
		gantt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		gantt.init();
	}
		
	public void init(){
		
		alreadyStarted = false;
		
		JMenuItem importFile;				
		JMenuItem mlfqSet;
		JMenuItem singleSet;
		
		menuBar = new JMenuBar();		
		
		insProcess = new JMenu("Insert processes");
		insProcess.setEnabled(true);
		
		setAlgorithm = new JMenu("Set Algorithm");
		setAlgorithm.setEnabled(true);
		
		mlfq = new JMenu("MLFQ");
		mlfq.setEnabled(false);
		
		singleQueue = new JMenu("Single Queue");
		singleQueue.setEnabled(true);
		
		importFile = new JMenuItem("Import file");
		importFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "Text files", "txt");
			    fileChooser.setFileFilter(filter);
			    int returnVal = fileChooser.showOpenDialog(null);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {				       
			        String fileChosen = fileChooser.getSelectedFile().getAbsolutePath();
			        System.out.println("file: " + fileChosen);
			        try(
			        	BufferedReader in = new BufferedReader(new FileReader(fileChosen));
			        ){
			        	String line;
						while((line = in.readLine()) != null){
						    System.out.println(line);
						    String[] token = line.split("\t");
						    
						    PID.add(Integer.parseInt(token[0]));
						    arrivalTime.add(Integer.parseInt(token[1]));
						    burstTime.add(Integer.parseInt(token[2]));
						    priority.add(Integer.parseInt(token[3]));
						}
						in.close();
						
						int size = PID.size();
						processes = new Process[size];
						System.out.println("size: " + size);
						for(int i = 0; i < size; i++){
							processes[i] = new Process(PID.get(i), arrivalTime.get(i), burstTime.get(i), priority.get(i));
						}
						
						startButton.setEnabled(true);
			        } catch (Exception e1) { 
			        	e1.printStackTrace();
			        	startButton.setEnabled(false);
			        }
			    }
			}
		});
		
		insProcess.add(importFile);				
		
		JRadioButtonMenuItem fcfs, rr, sjf, srtf, prio, npprio;
		fcfs = new JRadioButtonMenuItem("FCFS");
		fcfs.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e) {
				algorithm = SchedulingAlgorithm.FCFS;		
				con.removeAll();
				init();
			}			
		});
		
		rr = new JRadioButtonMenuItem("Round robin");
		rr.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e) {
				algorithm = SchedulingAlgorithm.RR;
				con.removeAll();
				init();
			}			
		});
		
		sjf = new JRadioButtonMenuItem("SJF");
		sjf.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e) {
				algorithm = SchedulingAlgorithm.SJF;
				con.removeAll();
				init();
			}			
		});
		
		srtf = new JRadioButtonMenuItem("SRTF");
		srtf.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e) {
				algorithm = SchedulingAlgorithm.SRTF;
				con.removeAll();
				init();
			}			
		});
		
		prio = new JRadioButtonMenuItem("Priority");
		prio.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e) {
				algorithm = SchedulingAlgorithm.PRIO;
				con.removeAll();
				init();
			}			
		});
		
		npprio = new JRadioButtonMenuItem("Nonpreemptive Priority");
		npprio.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e) {
				algorithm = SchedulingAlgorithm.NP_PRIO;
				con.removeAll();
				init();
			}			
		});
			
		setAlgorithm.add(fcfs);
		setAlgorithm.add(rr);
		setAlgorithm.add(sjf);
		setAlgorithm.add(srtf);
		setAlgorithm.add(prio);
		setAlgorithm.add(npprio);
				
		mlfqSet = new JMenuItem("Set");		
		mlfq.add(mlfqSet);
		
		singleSet = new JMenuItem("Set");
		singleQueue.add(singleSet);
		
		menuBar.add(insProcess);
		menuBar.add(setAlgorithm);
		menuBar.add(mlfq);
		menuBar.add(singleQueue);
		
		setJMenuBar(menuBar);
		
		if(!MLFQ){
							
			String titleName = "";
			int titleWidth = 100;
			
			if(algorithm == SchedulingAlgorithm.FCFS){
				titleName = "FCFS";				
			}else if(algorithm == SchedulingAlgorithm.RR){
				titleName = "Round robin";
				titleWidth = 200;
			}else if(algorithm == SchedulingAlgorithm.SJF){
				titleName = "SJF";
			}else if(algorithm == SchedulingAlgorithm.SRTF){
				titleName = "SRTF";
			}else if(algorithm == SchedulingAlgorithm.PRIO){
				titleName = "Preemptive Priority";
				titleWidth = 300;
			}else if(algorithm == SchedulingAlgorithm.NP_PRIO){
				titleName = "Nonpreemptive Priority";
				titleWidth = 300;
			}
			
			title = new JLabel(titleName);
			title.setFont(font);
			title.setBounds(110, 110, titleWidth, 50);
			
			panel = new JPanel();
			panel.setLayout(null);
			panel.setBackground(Color.LIGHT_GRAY);
			panel.setBorder(border);
			panel.setPreferredSize(new Dimension(panelWidth-5, 73));	
			
			timePanel = new JPanel();
			timePanel.setLayout(null);
			timePanel.setBackground(darkBlue);
			timePanel.setBounds(1, 51, 1145, 20);																								
			panel.add(timePanel);
			
			JScrollPane scrollPane = new JScrollPane(panel);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			scrollPane.setBounds(100, 150, panelWidth, 85);
			
			add(title);
			add(scrollPane);	
			
		}else{
			// do MLFQ;
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
		
		startButton = new JButton("START");
		startButton.setBounds(1120, 250, 100, 50);
		
		if(processes == null){
			startButton.setEnabled(false);
		}else{
			startButton.setEnabled(true);
		}
		
		startButton.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e) {
				if(!alreadyStarted){
					Scheduler scheduler = new Scheduler(processes.length);
					scheduler.initProcesses(processes);
					scheduler.generateQueues(algorithm, quantum);
					
					if(!threadStarted){																		
						scheduler.simulate();
						threadStarted = true;
						System.out.println("huhuhuh");
					}else{
						System.out.println("hahahah");
						con.removeAll();
						init();
						scheduler.restart();			
						
						if(Scheduler.queues[0] instanceof FCFSQueue){
							((FCFSQueue) Scheduler.queues[0]).restart();
						}else if(Scheduler.queues[0] instanceof RRQueue){
							((RRQueue) Scheduler.queues[0]).restart();
						}else if(Scheduler.queues[0] instanceof SJFQueue){
							((SJFQueue) Scheduler.queues[0]).restart();
						}else if(Scheduler.queues[0] instanceof SRTFQueue){
							((SRTFQueue) Scheduler.queues[0]).restart();
						}else if(Scheduler.queues[0] instanceof NonPQueue){
							((NonPQueue) Scheduler.queues[0]).restart();
						}else if(Scheduler.queues[0] instanceof PQueue){
							((PQueue) Scheduler.queues[0]).restart();
						}
					}
					
					startButton.setEnabled(false);
					insProcess.setEnabled(false);
					setAlgorithm.setEnabled(false);
					mlfq.setEnabled(false);
					singleQueue.setEnabled(false);
					
					alreadyStarted = true;					
				}
			}			
		});
		add(startButton);				
		
		con.repaint();
		con.revalidate();					
	}
	
	public static void addExecutingProcess(int processId, int executionTime, int algorithm) {
					
		System.out.println("adding..");
		Container container = null;		
		String processName = "p" + processId;		
		
		Border border = BorderFactory.createLineBorder(darkBlue);
		JPanel comp = new JPanel(null);			
		comp.setBorder(border);
		
		JLabel label = new JLabel(processName);
		label.setBounds(18, 18, 30, 15);
		comp.add(label);
		
		//if( algorithm == SchedulingAlgorithm.FCFS){
			container = panel;
			
			if(fcfsTimeCounter > 21){
				panel.setPreferredSize(new Dimension(panelWidth += 50, 73));
				timePanel.setSize(new Dimension(panelWidth, 73));
			}
			
			if(prevFCFSBurstLength < 0){
				xFCFS = 0;
				y = 0;				
			}else{							
				xFCFS += prevFCFSBurstLength;											
			}
			
			fcfsTimeLabel[fcfsTimeCounter] = new JLabel("" + fcfsTimeLapse);
			fcfsTimeLabel[fcfsTimeCounter].setFont(timeLabelFont);
			fcfsTimeLabel[fcfsTimeCounter].setForeground(Color.WHITE);
			fcfsTimeLabel[fcfsTimeCounter].setBounds(xFCFS + 1, 2, 30, 15);
			
			timePanel.add(fcfsTimeLabel[fcfsTimeCounter++]);
									
			fcfsTimeLapse += executionTime;
			prevFCFSBurstLength = 50;					
			comp.setBounds(xFCFS, y, 50, 51);
			timePanel.repaint();
			timePanel.revalidate();
			
		/*} else if (algorithm == SchedulingAlgorithm.RR) {
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
			roundrobinTimeLabel[roundrobinTimeCounter].setForeground(Color.WHITE);
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
			sjfTimeLabel[sjfTimeCounter].setForeground(Color.WHITE);			
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
			nonpreemptiveTimeLabel[nonpreemptiveTimeCounter].setForeground(Color.WHITE);
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
			preemptiveTimeLabel[preemptiveTimeCounter].setForeground(Color.WHITE);
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
		}*/
							
										
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
		//if(algorithm == SchedulingAlgorithm.FCFS){			
			fcfsTimeLabel[fcfsTimeCounter] = new JLabel("" + fcfsTimeLapse);
			fcfsTimeLabel[fcfsTimeCounter].setFont(timeLabelFont);
			fcfsTimeLabel[fcfsTimeCounter].setForeground(Color.WHITE);
			fcfsTimeLabel[fcfsTimeCounter].setBounds(xFCFS + prevFCFSBurstLength + 1, 2, 30, 15);			
			timePanel.add(fcfsTimeLabel[fcfsTimeCounter++]);
			timePanel.repaint();
			
		/*}else if(algorithm == SchedulingAlgorithm.RR){			
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
		}*/
		
	}
	

	public static void simulationDone() {
				
		startButton.setEnabled(true);
		insProcess.setEnabled(true);
		setAlgorithm.setEnabled(true);
		mlfq.setEnabled(true);
		singleQueue.setEnabled(true);
		
		alreadyStarted = false;
		
		Scheduler.stop();
		
		if(Scheduler.queues[0] instanceof FCFSQueue){
			((FCFSQueue) Scheduler.queues[0]).stopThread();
		}else if(Scheduler.queues[0] instanceof SJFQueue){
			((SJFQueue) Scheduler.queues[0]).stopThread();
		}
		
	}
}
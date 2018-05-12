import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GanttChart extends JFrame{

	private static JPanel panel;	
	private static JPanel timePanel;
	private static JPanel pcbPanel;
	private static JPanel pcbIdPanel;	
	private static JPanel pcbArrivalPanel;
	private static JPanel pcbBurstPanel;
	private static JPanel pcbPriorityPanel;	
	
	private static JPanel turnaroundTimePanel;
	private static JPanel timesPanel;
	private static JPanel waitTimePanel;
	private static JPanel responseTimePanel;
	private static JPanel timesIdPanel;
	
	private static int procYOffset;
	private static int timesYOffset;
	
	private static int processCount = 0;		
	
	private static int pcbPanelHeight = 350;
	private static int timesPanelHeight = 350;
	
	private static int panelWidth = 1150;
	private static JButton startButton;
	public JLabel srtfLabel;
	public JLabel sjfLabel;
	public JLabel preemptiveLabel;
	public JLabel nonpreemptiveLabel;
	public JLabel roundrobinLabel;	
	
	private JLabel title;
	
	private static JLabel[] fcfsTimeLabel = new JLabel[10000];
	private static Font font = new Font("Helvetica", Font.BOLD, 20);
	private static Font timeLabelFont = new Font("Helvetica", Font.BOLD, 12);
	
	private static Container con;
	
	private static int y = -1;			
	private static int xFCFS = -1;
	private static int prevFCFSBurstLength = -1;
	private static Color darkBlue = new Color(0, 46, 70);
	private static Border border = BorderFactory.createLineBorder(darkBlue);
	
	private static int fcfsTimeCounter = 0;
	private static int fcfsTimeLapse = 0;
	private JMenuBar menuBar;
	private static JMenu insProcess, setAlgorithm, mlfq, singleQueue, quantumItem;
	
	private boolean MLFQ = false;
	private static boolean alreadyStarted = false;
	private boolean threadStarted = false;
	private static int algorithm = SchedulingAlgorithm.FCFS;
	private int quantum = 2;
	
	private static Scheduler scheduler;
	private static int timesEntry;
	
	private ArrayList<Integer> PID = new ArrayList<Integer>();
	private ArrayList<Integer> arrivalTime = new ArrayList<Integer>();
	private ArrayList<Integer> burstTime = new ArrayList<Integer>();
	private ArrayList<Integer> priority = new ArrayList<Integer>();
	
	private Process[] processes;
	String fileChosen;
	
	
	private static JPanel avgTimeTable;
	private static JPanel avgResponseTime;
	private static JPanel avgWaitTime;
	private static JPanel avgTurnaroundTime;
	private static JPanel avgTimeLblPanel;
	
	public GanttChart(){
		super("CPU Scheduling Gantt Chart");		
		
		setExtendedState(MAXIMIZED_BOTH);
		con = getContentPane();
		con.setBackground(Color.WHITE);
		con.setLayout(null);
	}
		
	public void init(){
		
		alreadyStarted = false;
		
		JMenuItem importFile;				
		JMenuItem mlfqSet;
		JMenuItem singleSet;
		JMenuItem quantumChange;
		
		menuBar = new JMenuBar();		
		
		insProcess = new JMenu("Insert processes");
		insProcess.setEnabled(true);
		
		setAlgorithm = new JMenu("Set Algorithm");
		setAlgorithm.setEnabled(true);
		
		mlfq = new JMenu("MLFQ");
		mlfq.setEnabled(false);
		
		singleQueue = new JMenu("Single Queue");
		singleQueue.setEnabled(true);
		
		quantumItem = new JMenu("Quantum");
		if(algorithm == SchedulingAlgorithm.RR){
			quantumItem.setEnabled(true);
		}else{
			quantumItem.setEnabled(false);
		}
		
		importFile = new JMenuItem("Import file");
		importFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				PID.removeAll(PID);
				arrivalTime.removeAll(arrivalTime);
				burstTime.removeAll(burstTime);
				
				JFileChooser fileChooser = new JFileChooser();
			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "Text files", "txt");
			    fileChooser.setFileFilter(filter);
			    int returnVal = fileChooser.showOpenDialog(null);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {				       
			        fileChosen = fileChooser.getSelectedFile().getAbsolutePath();			        
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
						for(int i = 0; i < size; i++){
							processes[i] = new Process(PID.get(i), arrivalTime.get(i), burstTime.get(i), priority.get(i));
						}
						
						if(algorithm == SchedulingAlgorithm.RR && quantum < 0)
							startButton.setEnabled(false);
						else
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
				reset();
				con.removeAll();
				init();
			}			
		});
		
		rr = new JRadioButtonMenuItem("Round robin");
		rr.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e) {
				algorithm = SchedulingAlgorithm.RR;
				reset();

				if(quantum == 0){
					while(true){
						String result = JOptionPane.showInputDialog(null, "Quantum:");	
						if(result != null){
							char[] letters = result.toCharArray();
							
							int i;
							for(i = 0; i < letters.length; i++){
								if(!Character.isDigit(letters[i])){
									break;
								}
							}
							
							if(i == letters.length){
								quantum = Integer.parseInt(result);
								break;
							}
						}
					}
				}
					
				con.removeAll();
				init();
			}			
		});
		
		sjf = new JRadioButtonMenuItem("SJF");
		sjf.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e) {
				algorithm = SchedulingAlgorithm.SJF;
				reset();
				con.removeAll();
				init();
			}			
		});
		
		srtf = new JRadioButtonMenuItem("SRTF");
		srtf.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e) {
				algorithm = SchedulingAlgorithm.SRTF;
				reset();
				con.removeAll();
				init();
			}			
		});
		
		prio = new JRadioButtonMenuItem("Priority");
		prio.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e) {
				algorithm = SchedulingAlgorithm.PRIO;
				reset();
				con.removeAll();
				init();
			}			
		});
		
		npprio = new JRadioButtonMenuItem("Nonpreemptive Priority");
		npprio.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e) {
				algorithm = SchedulingAlgorithm.NP_PRIO;
				reset();
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
		
		quantumChange = new JMenuItem("Change (quantum = " + quantum + ")");
		quantumChange.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {	
				while(true){
					String result = JOptionPane.showInputDialog(null, "Quantum:");	
					if(result != null){
						char[] letters = result.toCharArray();
						
						int i;
						for(i = 0; i < letters.length; i++){
							if(!Character.isDigit(letters[i])){
								break;
							}
						}
						
						if(i == letters.length){
							quantum = Integer.parseInt(result);
							break;
						}
					}
				}
				reset();
				con.removeAll();
				init();
				
			}
		});
		quantumItem.add(quantumChange);
		
		menuBar.add(insProcess);
		menuBar.add(setAlgorithm);
		menuBar.add(mlfq);
		menuBar.add(singleQueue);
		menuBar.add(quantumItem);
		
		setJMenuBar(menuBar);
		
		if(!MLFQ){
							
			String titleName = "";
			int titleWidth = 100;
			
			if(algorithm == SchedulingAlgorithm.FCFS){
				titleName = "FCFS";				
			}else if(algorithm == SchedulingAlgorithm.RR){
				titleName = "Round robin (quantum = " + (quantum) + ")";
				titleWidth = 500;
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
			title.setBounds(70, 45, titleWidth, 50);
			
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
			scrollPane.setBounds(60, 90, panelWidth, 85);
			
			add(title);
			add(scrollPane);	
			
		}else{
			// do MLFQ;
		}						
		
		JLabel arrivingProcesses = new JLabel("PROCESSES");
		arrivingProcesses.setFont(font);
		arrivingProcesses.setBounds(270, 200, 500, 50);
		add(arrivingProcesses);
		
		pcbPanel = new JPanel(null);
		pcbPanel.setBorder(border);		
		pcbPanel.setPreferredSize(new Dimension(400, 350));		
		
		JScrollPane pcbScrollPane = new JScrollPane(pcbPanel);
		pcbScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pcbScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pcbScrollPane.setBounds(150, 250, 405, 355);
		add(pcbScrollPane);
		
		pcbIdPanel = new JPanel(null);
		pcbIdPanel.setBorder(border);		
		pcbIdPanel.setBounds(1, 1, 50, 350);
		
		JPanel idLabelPanel = new JPanel();
		idLabelPanel.setBounds(0, 0, 50, 25);
		idLabelPanel.setBorder(border);
		idLabelPanel.add(new JLabel("PID"));
		pcbIdPanel.add(idLabelPanel);		
		pcbPanel.add(pcbIdPanel);		
		
		pcbArrivalPanel = new JPanel(null);
		pcbArrivalPanel.setBorder(border);		
		pcbArrivalPanel.setBounds(51, 1, 120, 350);		
		
		JPanel arrivalLabelPanel = new JPanel();
		arrivalLabelPanel.setBounds(0, 0, 120, 25);
		arrivalLabelPanel.setBorder(border);
		arrivalLabelPanel.add(new JLabel("ARRIVAL TIME"));
		pcbArrivalPanel.add(arrivalLabelPanel, BorderLayout.NORTH);
		pcbPanel.add(pcbArrivalPanel);
		
		pcbBurstPanel = new JPanel(null);
		pcbBurstPanel.setBorder(border);		
		pcbBurstPanel.setBounds(171, 1, 120, 350);
		pcbPanel.add(pcbBurstPanel);
		
		JPanel burstLabelPanel = new JPanel();
		burstLabelPanel.setBounds(0, 0, 120, 25);
		burstLabelPanel.setBorder(border);
		burstLabelPanel.add(new JLabel("BURST TIME"));
		pcbBurstPanel.add(burstLabelPanel, BorderLayout.NORTH);
		pcbPanel.add(pcbBurstPanel);
		
		pcbPriorityPanel = new JPanel(null);
		pcbPriorityPanel.setBorder(border);		
		pcbPriorityPanel.setBounds(291, 1, 110, 350);
		pcbPanel.add(pcbPriorityPanel);		
		
		JPanel priorityLabelPanel = new JPanel();
		priorityLabelPanel.setBounds(0, 0, 110, 25);
		priorityLabelPanel.setBorder(border);
		priorityLabelPanel.add(new JLabel("PRIORITY"));
		pcbPriorityPanel.add(priorityLabelPanel, BorderLayout.NORTH);
		pcbPanel.add(pcbPriorityPanel);
		
		timesPanel = new JPanel(null);
		timesPanel.setBorder(border);		
		timesPanel.setPreferredSize(new Dimension(575, 250));		
		
		JScrollPane timesScrollPane = new JScrollPane(timesPanel);
		timesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		timesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		timesScrollPane.setBounds(650, 300, 435, 255);
		add(timesScrollPane);
		
		timesIdPanel = new JPanel(null);
		timesIdPanel.setBorder(border);		
		timesIdPanel.setBounds(1, 1, 50, 250);
		
		JPanel timeIdLabelPanel = new JPanel();
		timeIdLabelPanel.setBounds(0, 0, 50, 25);
		timeIdLabelPanel.setBorder(border);
		timeIdLabelPanel.add(new JLabel("PID"));
		timesIdPanel.add(timeIdLabelPanel);		
		timesPanel.add(timesIdPanel);			
		
		responseTimePanel = new JPanel(null);
		responseTimePanel.setBorder(border);		
		responseTimePanel.setBounds(51, 1, 120, 250);		
		
		JPanel responseTimeLabelPanel = new JPanel();
		responseTimeLabelPanel.setBounds(0, 0, 120, 25);
		responseTimeLabelPanel.setBorder(border);
		responseTimeLabelPanel.add(new JLabel("RESPONSE TIME"));
		responseTimePanel.add(responseTimeLabelPanel, BorderLayout.NORTH);
		timesPanel.add(responseTimePanel);
		
		waitTimePanel = new JPanel(null);
		waitTimePanel.setBorder(border);		
		waitTimePanel.setBounds(171, 1, 120, 250);
		timesPanel.add(waitTimePanel);
		
		JPanel waitTimeLabelPanel = new JPanel();
		waitTimeLabelPanel.setBounds(0, 0, 120, 25);
		waitTimeLabelPanel.setBorder(border);
		waitTimeLabelPanel.add(new JLabel("WAIT TIME"));
		waitTimePanel.add(waitTimeLabelPanel, BorderLayout.NORTH);
		timesPanel.add(waitTimePanel);
		
		turnaroundTimePanel = new JPanel(null);
		turnaroundTimePanel.setBorder(border);		
		turnaroundTimePanel.setBounds(291, 1, 141, 250);
		timesPanel.add(turnaroundTimePanel);		
		
		JPanel turnaroundTimeLabelPanel = new JPanel();
		turnaroundTimeLabelPanel.setBounds(0, 0, 141, 25);
		turnaroundTimeLabelPanel.setBorder(border);
		turnaroundTimeLabelPanel.add(new JLabel("TURNAROUND TIME"));
		turnaroundTimePanel.add(turnaroundTimeLabelPanel, BorderLayout.NORTH);
		timesPanel.add(turnaroundTimePanel);
		
		initAvgTimeTable();
		
		startButton = new JButton("START");
		startButton.setBounds(1100, 200, 100, 50);
		
		if(processes == null || (algorithm == SchedulingAlgorithm.RR && quantum == 0)){
			startButton.setEnabled(false);
		}else{
			startButton.setEnabled(true);
		}
		
		startButton.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e) {
				if(!alreadyStarted){
					scheduler = new Scheduler(processes.length);					
					
					int size = PID.size();
					for(int i = 0; i < size; i++){
						processes[i] = new Process(PID.get(i), arrivalTime.get(i), burstTime.get(i), priority.get(i));
					}
					
					scheduler.initProcesses(processes);
					scheduler.generateQueues(algorithm, quantum);
					
					if(!threadStarted){																		
						scheduler.simulate();
						threadStarted = true;						
					}else{						
						reset();
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
					quantumItem.setEnabled(false);
					
					alreadyStarted = true;					
				}
			}			
		});
		add(startButton);				
		
		con.repaint();
		con.revalidate();					
	}
	
	private void initAvgTimeTable() {
		avgTimeTable = new JPanel(null);
		avgTimeTable.setBorder(border);		
		avgTimeTable.setBounds(650, 560, 433, 26);		
		add(avgTimeTable);
		
		avgTimeLblPanel = new JPanel(null);
		avgTimeLblPanel.setBorder(border);		
		avgTimeLblPanel.setBounds(1, 1, 50, 25);
		
		JPanel avgTimeLbl = new JPanel();
		avgTimeLbl.setBounds(0, 0, 50, 25);
		avgTimeLbl.setBorder(border);
		avgTimeLbl.add(new JLabel("Avg:"));
		avgTimeLblPanel.add(avgTimeLbl);		
		avgTimeTable.add(avgTimeLblPanel);			
		
		avgResponseTime = new JPanel(null);
		avgResponseTime.setBorder(border);		
		avgResponseTime.setBounds(51, 1, 120, 25);		
		avgTimeTable.add(avgResponseTime);
		
		avgWaitTime = new JPanel(null);
		avgWaitTime.setBorder(border);		
		avgWaitTime.setBounds(171, 1, 120, 25);
		avgTimeTable.add(avgWaitTime);
		
		avgTurnaroundTime = new JPanel(null);
		avgTurnaroundTime.setBorder(border);		
		avgTurnaroundTime.setBounds(291, 1, 141, 25);
		avgTimeTable.add(avgTurnaroundTime);		
		
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
										
		container.add(comp);
		container.repaint();
		con.repaint();
		con.revalidate();
	}
	
	private void reset(){
		fcfsTimeCounter = 0;
		prevFCFSBurstLength = -1;
		xFCFS = 0;
		fcfsTimeLapse = 0;
		procYOffset = 0;
		timesYOffset = 0;
		panelWidth = 1150;		
	}
	
	public static void addNewArrivedProcess(int processId, int arrivalTime, int burstTime, int priority){							
				
		processCount++;
		
		if(processCount > 12){
			pcbPanelHeight += 25;
			pcbPanel.setPreferredSize(new Dimension(575, pcbPanelHeight));
			pcbIdPanel.setSize(new Dimension(pcbIdPanel.getWidth(), pcbPanelHeight));
			pcbArrivalPanel.setSize(new Dimension(pcbArrivalPanel.getWidth(), pcbPanelHeight));
			pcbBurstPanel.setSize(new Dimension(pcbBurstPanel.getWidth(), pcbPanelHeight));
			pcbPriorityPanel.setSize(new Dimension(pcbPriorityPanel.getWidth(), pcbPanelHeight));
		}
		
		JPanel idLabelPanel = new JPanel();
		idLabelPanel.setBounds(1, procYOffset+=25, 50, 25);
		idLabelPanel.add(new JLabel(""+processId));		
		pcbIdPanel.add(idLabelPanel);		

		JPanel arrLabelPanel = new JPanel();
		arrLabelPanel.setBounds(1, procYOffset, 120, 25);
		arrLabelPanel.add(new JLabel(""+arrivalTime));		
		pcbArrivalPanel.add(arrLabelPanel);
		
		JPanel burstLabelPanel = new JPanel();
		burstLabelPanel.setBounds(1, procYOffset, 120, 25);
		burstLabelPanel.add(new JLabel(""+burstTime));		
		pcbBurstPanel.add(burstLabelPanel);				
		
		JPanel priorityLabelPanel = new JPanel();
		priorityLabelPanel.setBounds(1, procYOffset, 118, 25);
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
					
		fcfsTimeLabel[fcfsTimeCounter] = new JLabel("" + fcfsTimeLapse);
		fcfsTimeLabel[fcfsTimeCounter].setFont(timeLabelFont);
		fcfsTimeLabel[fcfsTimeCounter].setForeground(Color.WHITE);
		fcfsTimeLabel[fcfsTimeCounter].setBounds(xFCFS + prevFCFSBurstLength + 1, 2, 30, 15);			
		timePanel.add(fcfsTimeLabel[fcfsTimeCounter++]);
		timePanel.repaint();
	}
	
	public static void addTimesInformation(int processId, long responseTime, long waitTime, long turnaroundTime) {
		timesEntry++;
		if(timesEntry > 9){
			timesPanelHeight += 18;
			timesPanel.setPreferredSize(new Dimension(575, timesPanelHeight));
			timesIdPanel.setSize(new Dimension(timesIdPanel.getWidth(), timesPanelHeight));
			responseTimePanel.setSize(new Dimension(responseTimePanel.getWidth(), timesPanelHeight));
			waitTimePanel.setSize(new Dimension(waitTimePanel.getWidth(), timesPanelHeight));
			turnaroundTimePanel.setSize(new Dimension(turnaroundTimePanel.getWidth(), timesPanelHeight));
		}
		
		JPanel idLabelPanel = new JPanel();
		idLabelPanel.setBounds(1, timesYOffset+=25, 50, 25);
		idLabelPanel.add(new JLabel("" + processId));		
		timesIdPanel.add(idLabelPanel);		

		JPanel resLabelPanel = new JPanel();
		resLabelPanel.setBounds(1, timesYOffset, 120, 25);
		resLabelPanel.add(new JLabel("" + responseTime));		
		responseTimePanel.add(resLabelPanel);
		
		JPanel waitLabelPanel = new JPanel();
		waitLabelPanel.setBounds(1, timesYOffset, 120, 25);
		waitLabelPanel.add(new JLabel("" + waitTime));		
		waitTimePanel.add(waitLabelPanel);				
		
		JPanel turnaroundLabelPanel = new JPanel();
		turnaroundLabelPanel.setBounds(1, timesYOffset, 141, 25);
		turnaroundLabelPanel.add(new JLabel("" + turnaroundTime));		
		turnaroundTimePanel.add(turnaroundLabelPanel);
		
		timesIdPanel.repaint();
		timesIdPanel.revalidate();
		timesPanel.add(responseTimePanel);
		timesPanel.add(waitTimePanel);
		timesPanel.add(turnaroundTimePanel);
	
		con.repaint();
		con.revalidate();
	}
	
	public static void addTimeAverages(double avgWait, double avgResponse, double avgTurnaround) {
		JPanel resLabelPanel = new JPanel();
		resLabelPanel.setBounds(1, 1, 120, 23);
		resLabelPanel.add(new JLabel("" + String.format("%.2f", avgResponse)));		
		avgResponseTime.add(resLabelPanel);
		
		JPanel waitLabelPanel = new JPanel();
		waitLabelPanel.setBounds(1, 1, 120, 23);
		waitLabelPanel.add(new JLabel("" + String.format("%.2f", avgWait)));		
		avgWaitTime.add(waitLabelPanel);				
		
		JPanel avgTurnaroundLabelPanel = new JPanel();
		avgTurnaroundLabelPanel.setBounds(1, 1, 141, 23);
		avgTurnaroundLabelPanel.add(new JLabel("" + String.format("%.2f", avgTurnaround)));		
		avgTurnaroundTime.add(avgTurnaroundLabelPanel);
		
		con.repaint();
		con.revalidate();
	}

	public static void simulationDone() {
				
		startButton.setEnabled(true);
		insProcess.setEnabled(true);
		setAlgorithm.setEnabled(true);		
		singleQueue.setEnabled(true);
		
		if(algorithm == SchedulingAlgorithm.RR)
			quantumItem.setEnabled(true);
		
		alreadyStarted = false;
		
		Scheduler.stop();
		
		if(Scheduler.queues[0] instanceof FCFSQueue){
			((FCFSQueue) Scheduler.queues[0]).stopThread();
		}else if(Scheduler.queues[0] instanceof SJFQueue){
			((SJFQueue) Scheduler.queues[0]).stopThread();
		}else if(Scheduler.queues[0] instanceof SRTFQueue){
			((SRTFQueue) Scheduler.queues[0]).stopThread();
		}else if(Scheduler.queues[0] instanceof PQueue){
			((PQueue) Scheduler.queues[0]).stopThread();
		}else if(Scheduler.queues[0] instanceof NonPQueue){			
			((NonPQueue) Scheduler.queues[0]).stopThread();
		}else if(Scheduler.queues[0] instanceof RRQueue){
			((RRQueue) Scheduler.queues[0]).stopThread();
		}
		
	}
}
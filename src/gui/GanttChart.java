package gui;
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
import javax.swing.JComboBox;
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

import constants.SchedulingAlgorithm;
import process.CPUBoundProcess;
import process.IOBoundProcess;
import queues.FCFSQueue;
import queues.NonPQueue;
import queues.PQueue;
import queues.RRQueue;
import queues.SJFQueue;
import queues.SRTFQueue;
import scheduler.Scheduler;

public class GanttChart extends JFrame{

	private static final int ONE_LEVEL = 1, TWO_LEVEL = 2, THREE_LEVEL = 3, FOUR_LEVEL = 4;
	private static int level = 1;
	
	private static JPanel panel1, panel2, panel3, panel4;	
	private static JPanel timePanel1, timePanel2, timePanel3, timePanel4;
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
	
	private static JPanel avgTimeTable;
	private static JPanel avgResponseTime;
	private static JPanel avgWaitTime;
	private static JPanel avgTurnaroundTime;
	private static JPanel avgTimeLblPanel;	
	private static JPanel mlfqPanel; 
	private static JLabel[] timeLabel = new JLabel[100000];
	
	private static JMenuBar menuBar;
	private static JMenu insProcess, setAlgorithm, mlfq, singleQueue, quantumItem;

	private static JButton startButton;
	private static JButton setQuantum1, setQuantum2, setQuantum3, setQuantum4;
	private static JButton button = null;
	
	private ArrayList<Integer> PID = new ArrayList<Integer>();
	private ArrayList<Integer> arrivalTime = new ArrayList<Integer>();
	private ArrayList<Integer> burstTime = new ArrayList<Integer>();
	private ArrayList<Integer> priority = new ArrayList<Integer>();
	private ArrayList<Integer> iOBoundFlag = new ArrayList<Integer>();
	
	private static int algorithm1, algorithm2, algorithm3, algorithm4, algorithm;
	private static int procYOffset;
	private static int timesYOffset;
	private static int processCount = 0;		
	
	private static int pcbPanelHeight = 350;
	private static int timesPanelHeight = 350;
	private static int panelWidth = 1150;
	
	private static int yOffset = -1;			
	private static int xOffset = -1;
	private static int prevFCFSBurstLength = -1;
	private static int timeCounter = 0;
	private static int timeLapse = 0;
	private static int timesEntry;
	private static int quantum = 2;
	private static int Offset = -2;
	
	private static int prevEndTime = -1;
	
	private static int quantum1, quantum2, quantum3, quantum4;
	
	private static boolean alreadyStarted = false;
	private static boolean threadStarted = false;
	private static boolean MLFQ = false;
	
	private static String fileChosen;
	private static String[] algorithms = {"FCFS", "SJF", "SRTF", "NP-PRIO", "P-PRIO", "RR"};
	
	private static Scheduler scheduler = new Scheduler();;
	private static Font font = new Font("Helvetica", Font.BOLD, 20);
	private static Font timeLabelFont = new Font("Helvetica", Font.BOLD, 12);
	private static Color darkBlue = new Color(0, 46, 70);
	private static Border border = BorderFactory.createLineBorder(darkBlue);
	private static Container con;
	
	private static ArrayList<CPUBoundProcess> processes = new ArrayList<CPUBoundProcess>();
	
	JComboBox<String> algoList1 = new JComboBox<String>(algorithms);
	JComboBox<String> algoList2 = new JComboBox<String>(algorithms);
	JComboBox<String> algoList3 = new JComboBox<String>(algorithms);
	JComboBox<String> algoList4 = new JComboBox<String>(algorithms);
	
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
		JMenuItem singleSet;
		JMenuItem quantumChange;
		
		menuBar = new JMenuBar();		
		
		insProcess = new JMenu("Insert processes");
		insProcess.setEnabled(true);
		
		setAlgorithm = new JMenu("Set Algorithm");
		setAlgorithm.setEnabled(true);
		
		mlfq = new JMenu("MLFQ");
		
		singleQueue = new JMenu("Single Queue");
		singleQueue.setEnabled(true);
		
		quantumItem = new JMenu("Quantum");
		if(algorithm == SchedulingAlgorithm.RR){
			quantumItem.setEnabled(true);
		}else{
			quantumItem.setEnabled(false);
		}
		
		mlfqPanel = new JPanel();
		mlfqPanel.setPreferredSize(new Dimension(con.getWidth(), con.getHeight() + Offset));
		mlfqPanel.setBackground(Color.WHITE);
		mlfqPanel.setLayout(null);
			
		JScrollPane scrollPane = new JScrollPane(mlfqPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(0, 0, con.getWidth(), con.getHeight());
			
		add(scrollPane);
		
		initStartButton(1100, 25);
		
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
						    iOBoundFlag.add(Integer.parseInt(token[4]));
						}
						in.close();
											
						int size = PID.size();
						for(int i = 0; i < size; i++){
							processes.add(new CPUBoundProcess(PID.get(i), arrivalTime.get(i), burstTime.get(i), priority.get(i)));
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
				
		JMenuItem mlfqOneLevel, mlfqTwoLevel, mlfqThreeLevel, mlfqFourLevel;
		mlfqOneLevel = new JMenuItem("1-Level");
		mlfqOneLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Offset = -2;
				reset();
				con.removeAll();
				init();
				initOneLevel();
				MLFQ = true;
			}
		});
		
		mlfqTwoLevel = new JMenuItem("2-Level");
		mlfqTwoLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Offset = 150;
				reset();
				con.removeAll();
				init();
				initTwoLevel();
				MLFQ = true;
			}
		});
		
		mlfqThreeLevel = new JMenuItem("3-Level");
		mlfqThreeLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Offset = 270;
				reset();
				con.removeAll();
				init();
				initThreeLevel();
				MLFQ = true;
			}
		});
		
		mlfqFourLevel = new JMenuItem("4-Level");
		mlfqFourLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Offset = 450;
				reset();
				con.removeAll();
				init();
				initFourLevel();
				MLFQ = true;
			}
		});
		
		mlfq.add(mlfqOneLevel);
		mlfq.add(mlfqTwoLevel);
		mlfq.add(mlfqThreeLevel);
		mlfq.add(mlfqFourLevel);
		
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
		
		con.repaint();
		con.revalidate();					
	}
	
	private void initGanttChart(int x, int y, int level /*JComboBox cb,*/) {
		JPanel panel = null;
		
		if(level == 1) {			
			panel1 = new JPanel();
			panel1.setLayout(null);
			panel1.setBackground(Color.LIGHT_GRAY);
			panel1.setBorder(border);
			panel1.setPreferredSize(new Dimension(panelWidth-5, 73));	
			
			timePanel1 = new JPanel();
			timePanel1.setLayout(null);
			timePanel1.setBackground(darkBlue);
			timePanel1.setBounds(1, 51, 1145, 20);																								
			panel1.add(timePanel1);
			
			panel = panel1;
			
		}else if (level == 2) {
			
			panel2 = new JPanel();
			panel2.setLayout(null);
			panel2.setBackground(Color.LIGHT_GRAY);
			panel2.setBorder(border);
			panel2.setPreferredSize(new Dimension(panelWidth-5, 73));	
			
			timePanel2 = new JPanel();
			timePanel2.setLayout(null);
			timePanel2.setBackground(darkBlue);
			timePanel2.setBounds(1, 51, 1145, 20);																								
			panel2.add(timePanel2);
			
			panel = panel2;
			
		}else if (level == 3) {
			
			panel3 = new JPanel();
			panel3.setLayout(null);
			panel3.setBackground(Color.LIGHT_GRAY);
			panel3.setBorder(border);
			panel3.setPreferredSize(new Dimension(panelWidth-5, 73));	
			
			timePanel3 = new JPanel();
			timePanel3.setLayout(null);
			timePanel3.setBackground(darkBlue);
			timePanel3.setBounds(1, 51, 1145, 20);																								
			panel3.add(timePanel3);
			
			panel = panel3;
			
		}else if (level == 4) {
			
			panel4 = new JPanel();
			panel4.setLayout(null);
			panel4.setBackground(Color.LIGHT_GRAY);
			panel4.setBorder(border);
			panel4.setPreferredSize(new Dimension(panelWidth-5, 73));	
			
			timePanel4 = new JPanel();
			timePanel4.setLayout(null);
			timePanel4.setBackground(darkBlue);
			timePanel4.setBounds(1, 51, 1145, 20);																								
			panel4.add(timePanel4);
			
			panel = panel4;
		}
		
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(x, y, panelWidth, 85);
		
		mlfqPanel.add(scrollPane);

	}
	
	private void initOneLevel() {
		level = ONE_LEVEL;
		
		initOneLevelComboBox();
		
		initGanttChart(60, 90, 1);
		initProcessTable(270, 200, 150, 250);
		initTimesTable(300);
		initAvgTimeTable(650, 560);
	}

	private void initTwoLevel() {
		level = TWO_LEVEL;
		
		initOneLevelComboBox();
		initTwoLevelComboBox();
		
		initGanttChart(60, 90, 1);
		initGanttChart(60, 225, 2);
		initProcessTable(270, 320, 150, 380);
		initTimesTable(400);
		initAvgTimeTable(650, 660);
	}
	
	private void initThreeLevel() {
		level = THREE_LEVEL;
		
		initOneLevelComboBox();
		initTwoLevelComboBox();
		initThreeLevelComboBox();
		
		initGanttChart(60, 90, 1);
		initGanttChart(60, 225, 2);
		initGanttChart(60, 360, 3);
		
		initProcessTable(270, 460, 150, 520);
		initTimesTable(550);
		initAvgTimeTable(650, 820);
	}
	
	private void initFourLevel() {
		level = FOUR_LEVEL;
		
		initOneLevelComboBox();
		initTwoLevelComboBox();
		initThreeLevelComboBox();
		initFourLevelComboBox();
		
		initGanttChart(60, 90, 1);
		initGanttChart(60, 225, 2);
		initGanttChart(60, 360, 3);
		initGanttChart(60, 500, 4);
		
		initProcessTable(270, 620, 150, 680);
		initTimesTable(700);
		initAvgTimeTable(650, 860);
	}
	
	private void initOneLevelComboBox() {
		algoList1.setBounds(70, 65, 80, 20);
		algoList1.setSelectedIndex(0);
		
		algoList1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String selected = (String) ((JComboBox)arg0.getSource()).getSelectedItem();
				
				if (selected.equals("SJF")) {
					algorithm1 = SchedulingAlgorithm.SJF;
				}else if (selected.equals("SRTF")) {
					algorithm1 = SchedulingAlgorithm.SRTF;
				}else if (selected.equals("NP-PRIO")) {
					algorithm1 = SchedulingAlgorithm.NP_PRIO;
				}else if (selected.equals("P-PRIO")) {
					algorithm1 = SchedulingAlgorithm.PRIO;
				}else if (selected.equals("RR")) {
					algorithm1 = SchedulingAlgorithm.RR;
					addSetQuantum(160, 65, 1);
				}else {
					algorithm1 = SchedulingAlgorithm.FCFS;
				}
				
				if(selected.equals("RR")){
					addSetQuantum(160, 65, 1);
				}else{
					if(setQuantum1 != null) {
						mlfqPanel.remove(setQuantum1);
						mlfqPanel.repaint();
						mlfqPanel.revalidate();
					}
				}
			}
			
		});
		mlfqPanel.add(algoList1);
	}
	
	private void initTwoLevelComboBox() {
		algoList2.setBounds(70, 200, 80, 20);
		algoList2.setSelectedIndex(0);
		
		algoList2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String selected = (String) ((JComboBox)arg0.getSource()).getSelectedItem();
				
				if (selected.equals("SJF")) {
					algorithm2 = SchedulingAlgorithm.SJF;
				}else if (selected.equals("SRTF")) {
					algorithm2 = SchedulingAlgorithm.SRTF;
				}else if (selected.equals("NP-PRIO")) {
					algorithm2 = SchedulingAlgorithm.NP_PRIO;
				}else if (selected.equals("P-PRIO")) {
					algorithm2 = SchedulingAlgorithm.PRIO;
				}else if (selected.equals("RR")) {
					algorithm2 = SchedulingAlgorithm.RR;
				}else {
					algorithm2 = SchedulingAlgorithm.FCFS;
				}
				
				if(selected.equals("RR")){
					addSetQuantum(160, 200, 2);
				}else{
					if(setQuantum2 != null) {
						mlfqPanel.remove(setQuantum2);
						mlfqPanel.repaint();
						mlfqPanel.revalidate();
					}
				}
			}
			
		});
		mlfqPanel.add(algoList2);
	}
	
	private void initThreeLevelComboBox() {
		algoList3.setBounds(70, 335, 80, 20);
		algoList3.setSelectedIndex(0);
		
		algoList3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String selected = (String) ((JComboBox)arg0.getSource()).getSelectedItem();
				
				if (selected.equals("SJF")) {
					algorithm3 = SchedulingAlgorithm.SJF;
				}else if (selected.equals("SRTF")) {
					algorithm3 = SchedulingAlgorithm.SRTF;
				}else if (selected.equals("NP-PRIO")) {
					algorithm3 = SchedulingAlgorithm.NP_PRIO;
				}else if (selected.equals("P-PRIO")) {
					algorithm3 = SchedulingAlgorithm.PRIO;
				}else if (selected.equals("RR")) {
					algorithm3 = SchedulingAlgorithm.RR;
				}else {
					algorithm3 = SchedulingAlgorithm.FCFS;
				}
				
				if(selected.equals("RR")){
					addSetQuantum(160, 335, 3);
				}else{
					if(setQuantum3 != null) {
						mlfqPanel.remove(setQuantum3);
						mlfqPanel.repaint();
						mlfqPanel.revalidate();
					}
				}
			}
			
		});
		mlfqPanel.add(algoList3);
	}
	
	private void initFourLevelComboBox() {
		algoList4.setBounds(70, 475, 80, 20);
		algoList4.setSelectedIndex(0);
		
		algoList4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String selected = (String) ((JComboBox)arg0.getSource()).getSelectedItem();
				
				if (selected.equals("SJF")) {
					algorithm4 = SchedulingAlgorithm.SJF;
				}else if (selected.equals("SRTF")) {
					algorithm4 = SchedulingAlgorithm.SRTF;
				}else if (selected.equals("NP-PRIO")) {
					algorithm4 = SchedulingAlgorithm.NP_PRIO;
				}else if (selected.equals("P-PRIO")) {
					algorithm4 = SchedulingAlgorithm.PRIO;
				}else if (selected.equals("RR")) {
					algorithm4 = SchedulingAlgorithm.RR;
				}else {
					algorithm4 = SchedulingAlgorithm.FCFS;
				}
				
				if(selected.equals("RR")){
					addSetQuantum(160, 475, 4);
				}else{
					if(setQuantum4 != null) {
						mlfqPanel.remove(setQuantum4);
						mlfqPanel.repaint();
						mlfqPanel.revalidate();
					}
				}
			}
			
		});
		mlfqPanel.add(algoList4);
	}

	protected void addSetQuantum(int x, int y, int level) {
		String label = "Quantum = "; 
		
		if(level == 1) {
			label += quantum1;
			
			setQuantum1 = new JButton();
			setQuantum1.setText(label);
			setQuantum1.setBounds(x, y, 110, 20);
			setQuantum1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					while(true){
						String result = JOptionPane.showInputDialog("Quantum", quantum1);	
						if(result != null){
							char[] letters = result.toCharArray();
							
							int i;
							for(i = 0; i < letters.length; i++){
								if(!Character.isDigit(letters[i])){
									break;
								}
							}
							
							if(i == letters.length){
								quantum1 = Integer.parseInt(result);
								break;
							}
						}else {
							break;
						}
					}
					
					// TODO does not show change of quantum value
					button.setVisible(false);
					GanttChart.mlfqPanel.remove(button);
					GanttChart.mlfqPanel.repaint();
					GanttChart.mlfqPanel.revalidate();
				}
			});
			
			button = setQuantum1;
		}
		
		if(level == 2) {
			label += quantum2;
			
			setQuantum2 = new JButton(label);
			setQuantum2.setBounds(x, y, 110, 20);
			setQuantum2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					while(true){
						String result = JOptionPane.showInputDialog("Quantum", quantum2);	
						if(result != null){
							char[] letters = result.toCharArray();
							
							int i;
							for(i = 0; i < letters.length; i++){
								if(!Character.isDigit(letters[i])){
									break;
								}
							}
							
							if(i == letters.length){
								quantum2 = Integer.parseInt(result);
								break;
							}
						}else {
							break;
						}
					}
					button.setText("Quantum = " + quantum2);
				}
			});
			
			button = setQuantum2;
		}
		
		if(level == 3) {
			label += quantum3;
			
			setQuantum3 = new JButton(label);
			setQuantum3.setBounds(x, y, 110, 20);
			setQuantum3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					while(true){
						String result = JOptionPane.showInputDialog("Quantum", quantum3);	
						if(result != null){
							char[] letters = result.toCharArray();
							
							int i;
							for(i = 0; i < letters.length; i++){
								if(!Character.isDigit(letters[i])){
									break;
								}
							}
							
							if(i == letters.length){
								quantum3 = Integer.parseInt(result);
								break;
							}
						}else {
							break;
						}
					}
					button.setText("Quantum = " + quantum3);
				}
			});
			
			button = setQuantum3;
		}
		
		if(level == 4) {
			label += quantum4;
			
			setQuantum4 = new JButton(label);
			setQuantum4.setBounds(x, y, 110, 20);
			setQuantum4.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					while(true){
						String result = JOptionPane.showInputDialog("Quantum", quantum4);	
						if(result != null){
							char[] letters = result.toCharArray();
							
							int i;
							for(i = 0; i < letters.length; i++){
								if(!Character.isDigit(letters[i])){
									break;
								}
							}
							
							if(i == letters.length){
								quantum4 = Integer.parseInt(result);
								break;
							}
						}else {
							break;
						}
					}
					button.setText("Quantum = " + quantum4);
				}
			});
			
			button = setQuantum4;
		}
		
		mlfqPanel.add(button);
		mlfqPanel.repaint();
		mlfqPanel.revalidate();
		
	}

	private void initStartButton(int xOffset, int yOffset) {
		startButton = new JButton("START");
		startButton.setBounds(xOffset, yOffset, 100, 50);
		
		if(processes == null || (algorithm == SchedulingAlgorithm.RR && quantum == 0)){
			startButton.setEnabled(false);
		}else{
			startButton.setEnabled(true);
		}
		
		startButton.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e) {
				/*if(!alreadyStarted){
					*/
					int queues_num = level;
					
					/*if(level == TWO_LEVEL) {
						queues_num = TWO_LEVEL;
					}else if(level == THREE_LEVEL) {
						queues_num = THREE_LEVEL;
					}else if(level == FOUR_LEVEL) {
						queues_num = FOUR_LEVEL;
					}*/
															
					processes.removeAll(processes);
					
					int size = PID.size();
					for(int i = 0; i < size; i++){
						if(iOBoundFlag.get(i) == 1) {
							processes.add(new IOBoundProcess(PID.get(i), arrivalTime.get(i), burstTime.get(i), priority.get(i)));
						}else {
							processes.add(new CPUBoundProcess(PID.get(i), arrivalTime.get(i), burstTime.get(i), priority.get(i)));
						}
					}
					
					scheduler.initProcesses(queues_num, processes);
					
					int[] algorithms = {algorithm1, algorithm2, algorithm3, algorithm4};
					int[] quanta = {quantum1, quantum2, quantum3, quantum4};
					scheduler.generateQueues(algorithms, quanta);
									
					//if(!threadStarted){
						System.out.println("Simulating...");
						scheduler.simulate();
						//threadStarted = true;						
					/*}else{						
						reset();
						//con.removeAll();						
						
						//init();
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
					}*/
					
					startButton.setEnabled(false);
					insProcess.setEnabled(false);
					setAlgorithm.setEnabled(false);
					mlfq.setEnabled(false);
					singleQueue.setEnabled(false);
					quantumItem.setEnabled(false);
					
					alreadyStarted = true;					
				//}
			}			
		});
		mlfqPanel.add(startButton);
	}

	private void initProcessTable(int lbl_x, int lbl_y, int scrl_x, int scrl_y) {
		JLabel arrivingProcesses = new JLabel("PROCESSES");
		arrivingProcesses.setFont(font);
		arrivingProcesses.setBounds(lbl_x, lbl_y, 500, 50);
		mlfqPanel.add(arrivingProcesses);
		
		pcbPanel = new JPanel(null);
		pcbPanel.setBorder(border);		
		pcbPanel.setPreferredSize(new Dimension(400, 350));		
		
		JScrollPane pcbScrollPane = new JScrollPane(pcbPanel);
		pcbScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pcbScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pcbScrollPane.setBounds(scrl_x, scrl_y, 405, 355);
		mlfqPanel.add(pcbScrollPane);
		
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
	}
	
	private void initTimesTable(int offset) {
		timesPanel = new JPanel(null);
		timesPanel.setBorder(border);		
		timesPanel.setPreferredSize(new Dimension(575, 250));		
		
		JScrollPane timesScrollPane = new JScrollPane(timesPanel);
		timesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		timesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		timesScrollPane.setBounds(650, offset, 435, 255);
		mlfqPanel.add(timesScrollPane);
		
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
	}
	
	private void initAvgTimeTable(int xOffset, int yOffset) {
		avgTimeTable = new JPanel(null);
		avgTimeTable.setBorder(border);		
		avgTimeTable.setBounds(xOffset, yOffset, 433, 26);		
		mlfqPanel.add(avgTimeTable);
		
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

	public static void addExecutingProcess(byte level, int processId, int executionTime, int timeNow, int algorithm) {
							
		Container container = null;		
		String processName = "p" + processId;		
		
		Border border = BorderFactory.createLineBorder(darkBlue);
		JPanel comp = new JPanel(null);			
		comp.setBorder(border);
		
		JLabel label = new JLabel(processName);
		label.setBounds(18, 18, 30, 15);
		comp.add(label);
		
		JPanel panel = null, timePanel = null;
				
		if(level == 0) {
			container = panel1;
			panel = panel1;
			timePanel = timePanel1;
		}else if(level == 1) {
			container = panel2;
			panel = panel2;
			timePanel = timePanel2;
		}else if(level == 2) {
			container = panel3;
			panel = panel3;
			timePanel = timePanel3;
		}else if(level == 3) {
			container = panel4;
			panel = panel4;
			timePanel = timePanel4;
		}
			
		if(timeCounter > 21){
			panel.setPreferredSize(new Dimension(panelWidth += 50, 73));
			timePanel.setSize(new Dimension(panelWidth, 73));
		}
		
		if(prevFCFSBurstLength < 0){
			xOffset = 0;
			yOffset = 0;				
		}else{							
			xOffset += prevFCFSBurstLength;											
		}
		
		System.out.println("prevTime = " + prevEndTime + " timeNow = " + timeNow + " executionTime = " + executionTime);
		if(prevEndTime > timeNow-executionTime) {
			timeLabel[timeCounter] = new JLabel("" + (timeNow-executionTime));
			timeLabel[timeCounter].setFont(timeLabelFont);
			timeLabel[timeCounter].setForeground(Color.WHITE);
			timeLabel[timeCounter].setBounds(xOffset + 1, 2, 30, 15);
			
			timePanel.add(timeLabel[timeCounter++]);
		}		
					
		timeLabel[timeCounter] = new JLabel("" + timeNow);
		timeLabel[timeCounter].setFont(timeLabelFont);
		timeLabel[timeCounter].setForeground(Color.WHITE);
		timeLabel[timeCounter].setBounds(xOffset + 37, 2, 30, 15);
		
		timePanel.add(timeLabel[timeCounter++]);
		prevFCFSBurstLength = 50;	
				
		prevEndTime = timeNow;	
		comp.setBounds(xOffset, yOffset, 50, 51);
		timePanel.repaint();
		timePanel.revalidate();			
										
		container.add(comp);
		container.repaint();
		con.repaint();
		con.revalidate();
	}
	
	private void reset(){
		timeCounter = 0;
		prevFCFSBurstLength = -1;
		xOffset = 0;
		timeLapse = 0;
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
	
	public static void addLastCompletionTime(byte level, int algorithm){
				
		JPanel timePanel = null;
		
		if(level == 0)
			timePanel = timePanel1;
		else if(level == 1)
			timePanel = timePanel2;
		else if(level == 2)
			timePanel = timePanel3;
		else if(level == 3)
			timePanel = timePanel4;
			
		timeLabel[timeCounter] = new JLabel("" + timeLapse);
		timeLabel[timeCounter].setFont(timeLabelFont);
		timeLabel[timeCounter].setForeground(Color.WHITE);
		timeLabel[timeCounter].setBounds(xOffset + prevFCFSBurstLength + 1, 2, 30, 15);			
		timePanel.add(timeLabel[timeCounter++]);
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
	
	public static void addTimeAverages(double avgResponse, double avgWait, double avgTurnaround) {
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

	public static void simulationDone(Object queue) {
				
		startButton.setEnabled(true);
		insProcess.setEnabled(true);
		setAlgorithm.setEnabled(true);		
		singleQueue.setEnabled(true);
		mlfq.setEnabled(true);
		
		if(algorithm == SchedulingAlgorithm.RR)
			quantumItem.setEnabled(true);
		
		alreadyStarted = false;
		
		if(queue instanceof FCFSQueue){
			((FCFSQueue) queue).stopThread();
			System.out.println("FCFSThread stopped.");
		}else if(queue instanceof SJFQueue){
			((SJFQueue) queue).stopThread();
		}else if(queue instanceof SRTFQueue){
			((SRTFQueue) queue).stopThread();
			System.out.println("SRTFThread stopped.");
		}else if(queue instanceof PQueue){
			System.out.println("PThread stopped.");
			((PQueue) queue).stopThread();
		}else if(queue instanceof NonPQueue){			
			((NonPQueue) queue).stopThread();
		}else if(queue instanceof RRQueue){
			((RRQueue) queue).stopThread();
			System.out.println("RRThread stopped.");
		}
		
	}
}
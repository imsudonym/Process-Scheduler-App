package gui;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel; 
@SuppressWarnings("serial")
class DataGenerator extends JFrame { 
	int totalExecTime = 0;
	static JFrame f; 
	GanttChart gantt;
	ArrayList<int[]> data = new ArrayList<int[]>();
	
	DataGenerator(GanttChart g){		
		gantt = g;
		f = new JFrame("spinner"); 		
		f.setLayout(null);	
		f.setResizable(false);
				
		JLabel numOfProcessLbl = new JLabel("Total number of jobs:");
		numOfProcessLbl.setBounds(20, 20, 150, 25);
		
		JLabel numOfIOProcessLbl = new JLabel("Number of IOs:");
		numOfIOProcessLbl.setBounds(250, 20, 150, 25);
		
		SpinnerNumberModel spinModel = new SpinnerNumberModel(0, 0, 1000, 1);
		JSpinner procSpinner = new JSpinner(spinModel); 
		procSpinner.setBounds(150, 20, 50, 25); 		
		
		SpinnerNumberModel spinIOModel = new SpinnerNumberModel(0, 0, 1000, 1);
		JSpinner procIOSpinner = new JSpinner(spinIOModel); 
		procIOSpinner.setBounds(350, 20, 50, 25);
		
		procIOSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int numOfIOs = (int) ((JSpinner)e.getSource()).getValue();
				int numOfProcess = (int) procSpinner.getValue();
				if(numOfIOs > numOfProcess) {
					procIOSpinner.setValue(procSpinner.getValue());
				}
			}
		});
		
		String[] columnNames = {"PID", "Arrival Time", "Burst Time", "Priority", "IO Bound"};		
		DefaultTableModel model = new DefaultTableModel(0, columnNames.length) ;
		model.setColumnIdentifiers(columnNames);		
		
		JTable tableProcesses = new JTable(model);
		tableProcesses.setPreferredScrollableViewportSize(new Dimension(500, 70));
	    tableProcesses.setFillsViewportHeight(true);
	    JScrollPane scrollPane = new JScrollPane(tableProcesses);
	    scrollPane.setBounds(35, 100, 410, 230);

		JLabel execTime = new JLabel("Total execution time: " + totalExecTime);
	    execTime.setBounds(35, 320, 200, 50);
	    
	    JButton execButton = new JButton("execute");
	    execButton.setBounds(250, 400, 90, 22);
	    execButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				gantt.readData(data, totalExecTime);
				f.dispose();
			}
	    });
	    
		JButton randButton = new JButton("generate");
		randButton.setBounds(150, 400, 90, 22);
		randButton.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				totalExecTime = 0; 				
				int procsCount = (int)procSpinner.getValue();
				
				if(model.getRowCount() >= procsCount) {
					for(int i = model.getRowCount()-1; i >= procsCount; i--) {
						model.removeRow(i);
					}
				}
				
				if(procsCount > model.getRowCount()) {
					int rowCount = model.getRowCount();
					for(int i = 0; i < procsCount-rowCount; i++) {
						model.addRow(new Object[]{});						
					}
				}
				
				ArrayList<int[]> arr = generateData((int)procSpinner.getValue(), (int)procIOSpinner.getValue());				
				data = arr;
				
				for(int i = 0; i < arr.size(); i++) {
					int[] temp = arr.get(i);
					for(int j = 0; j < temp.length; j++) {
						if(i == 2) {
							totalExecTime += temp[j];
							execTime.setText("Total execution time: " + totalExecTime);
						}
						
						if(i == arr.size()-1) {
							tableProcesses.getModel().setValueAt((temp[j] == 1 ) ? "true" : "false", j, i);	
						}else {
							tableProcesses.getModel().setValueAt(temp[j], j, i);
						}
					}					
				}
				
			}
		});		
		
		f.add(numOfProcessLbl);
		f.add(procSpinner);
		f.add(numOfIOProcessLbl);
		f.add(procIOSpinner);
		f.add(scrollPane);
		f.add(randButton);
		f.add(execButton);
		f.add(execTime);
		
		f.setSize(500, 500); 
		f.setVisible(true);		
	}  
	
	public static ArrayList<int[]> generateData(int size, int ioBounds) {
		ArrayList<int[]> processes = new ArrayList<int[]>();
		Random rand = new Random();		
		
		int[] pid = new int[size];
		int[] arrivalTime = new int[size];
		int[] burstTime = new int[size];
		int[] priority = new int[size];
		int[] isIO = new int[size];
		
		int ctr = 0;
		//generate IO bound processes
		for(; ctr < ioBounds; ctr++) {
			 pid[ctr] = ctr+1;
			 arrivalTime[ctr] = rand.nextInt(100);			 
			 burstTime[ctr] = rand.nextInt(100) + 1;
			 priority[ctr] = rand.nextInt(size);
			 isIO[ctr] = 1;
		}
				
		//generate CPU bound processes		
		for(; ctr < size; ctr++){
			pid[ctr] = ctr+1;
			arrivalTime[ctr] = rand.nextInt(100);
			burstTime[ctr] = rand.nextInt(100) + 1;
			priority[ctr] = rand.nextInt(size);
			isIO[ctr] = 0;
		} 
		
		processes.add(pid);
		processes.add(arrivalTime);
		processes.add(burstTime);
		processes.add(priority);
		processes.add(isIO);
			
		return processes;
	}
} 

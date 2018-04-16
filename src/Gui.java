import javax.swing.*;
import java.awt.*;

public class Gui extends JFrame{
	public JPanel fcfsPanel;
	public JPanel srtfPanel;
	public JPanel sjfPanel;
	public JPanel preemptivePanel;
	public JPanel nonpreemptivePanel;
	public JPanel roundrobinPanel;
	
	public JLabel fcfsLabel;
	public JLabel srtfLabel;
	public JLabel sjfLabel;
	public JLabel preemptiveLabel;
	public JLabel nonpreemptiveLabel;
	public JLabel roundrobinLabel;
	
	public Gui(){
		super("CPU Scheduling Gantt Chart");
		setExtendedState(MAXIMIZED_BOTH);
		setLayout(new BorderLayout());
		
		fcfsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		fcfsPanel.setLayout(null);
		fcfsPanel.setBackground(new Color(0, 46, 70));
		add(fcfsPanel);
		
		srtfPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
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
		fcfsPanel.add(roundrobinLabel);
	}
	
	public static void main(String[] args){
		Gui gantt = new Gui();
		gantt.setVisible(true);
		gantt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
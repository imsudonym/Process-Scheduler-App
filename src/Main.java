import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {	
		GanttChart gantt = new GanttChart();
		gantt.setVisible(true);
		gantt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		gantt.init();
	}
}

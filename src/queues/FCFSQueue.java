package queues;
import constants.QueueType;
import scheduler.Scheduler;

public class FCFSQueue extends Queue{
	public FCFSQueue(int level){
		super(level);
		this.queueType = QueueType.FCFS;
	}
	
	public void startThread(){
		running = true;
		thread.start();
	}
	
	public void stopThread(){
		thread.interrupt();
		running = false;		
	}	
	
	Thread thread = new Thread(){		
		public void run(){
			while(running){
				if(peekHead() != null){									
					currProcess = peekHead();			
					
					long startTime = Scheduler.clockTime;
					if(prevProcess != null && prevProcess.preemptedFlag) {						
						startTime = prevProcess.getTimePreempted(prevProcess.getTimesPreempted()-1);
					}
					
					currProcess.setStartTime(startTime);
					
					if(currProcess.getResponseTime() < 0) {
						currProcess.setStartTime(startTime);
						currProcess.setFirstStartTime(startTime);
						currProcess.setResponseTime();	
					}					
					
					if(currProcess.preemptedFlag) {						
//						System.out.println("resuming p" + currProcess.getId() + "..... timeStart = " + startTime);						
						currProcess.setStartTime(startTime);
						currProcess.setTimeResumed(startTime);						
						currProcess.preemptedFlag = false;
					}
					
					long timeNow = Scheduler.clockTime;	
					if(prevTime < timeNow) {
						//System.out.print(".");
//						System.out.println("level = " + level + " exec p" + currProcess.getId() + " timeNow = " + timeNow);
						int lapse = (int)(timeNow - prevTime);
						int burstLeft = currProcess.getBurstTime() - lapse;					
						currProcess.setBurstTime(burstLeft);
						
						if(burstLeft <= 0){								
							dequeue();									
							/*GanttChart.addExecutingProcess(level, currProcess.getId(), currProcess.getPrevBurstPreempted(), SchedulingAlgorithm.FCFS);*/

							System.out.println("p" + currProcess.getId() + " Done executing. prevBurstPreempted = " + currProcess.getPrevBurstPreempted());
							
							currProcess.preemptedFlag = false;
							prevProcess = currProcess;
							
							prevTimeQuantum = timeNow;
							//currProcess = null;
						}					
					}
					prevTime = timeNow;
					
				}else{				
					
					if(peekHead() == null && isHigherQueueDone() && getSize() == 0) {
						if(Scheduler.processes.size() == 0) {
							System.out.println("[::FCFSQueue] level = " +level + " stopping simulation...");
							simulationDone();
						}
					}
					
					/*if (allProcessesDone == 0 && getSize() == 0){						
						allProcessesDone = 1;		
						
						if(level == Scheduler.getMaxLevelOfQueues() && prevQueueDone == 1 && Scheduler.processes.size() == 0) {
							simulationDone();
						}
					}	*/
				}
			}
		}		
	};	

	public void preemptQueue() {
		stopThread();
	}
}

package queues;
import constants.QueueType;
import process.IOBoundProcess;
import scheduler.Scheduler;

public class SJFQueue extends Queue{
	
	public SJFQueue(int level){
		super((byte)level);
		this.queueType = QueueType.SJF;
	}
	
	public void startThread(){
		running = true;
		SJFThread.start();
	}
	
	public void enqueue(IOBoundProcess newProcess) {
		super.enqueue(newProcess, QueueType.SJF);
	}
	
	public void stopThread(){
		SJFThread.interrupt();
		running = false;
	}
	
	Thread SJFThread = new Thread(){				
		public void run(){
			while(running){		
				if(prevQueueDone == 1 && peekHead() != null){		
					currProcess = peekHead();
					
					long startTime = Scheduler.clockTime;
					if(prevProcess != null && prevProcess.preemptedFlag) {						
						startTime = prevProcess.getTimePreempted(prevProcess.getTimesPreempted()-1);
					}
					
					currProcess.setStartTime(startTime);
					
					if(currProcess.getResponseTime() < 0/* && hasExecuted(currProcess)*/) {
						currProcess.setStartTime(startTime);
						currProcess.setFirstStartTime(startTime);
						currProcess.setResponseTime();
						System.out.print("");
					}					
					
					if(currProcess.preemptedFlag) {						
						System.out.println("resuming p" + currProcess.getId() + "..... timeStart = " + startTime);						
						currProcess.setStartTime(startTime);
						currProcess.setTimeResumed(startTime);						
						currProcess.preemptedFlag = false;
					}
					
					long timeNow = Scheduler.clockTime;	
					if(prevTime < timeNow) {
						System.out.println("level = " + level + " exec p" + currProcess.getId() + " timeNow = " + timeNow);
						int lapse = (int)(timeNow - prevTime);
						int burstLeft = currProcess.getBurstTime() - lapse;					
						currProcess.setBurstTime(burstLeft);
						
						if(burstLeft <= 0){								
							dequeue();															

							System.out.println("p" + currProcess.getId() + " Done executing. prevBurstPreempted = " + currProcess.getPrevBurstPreempted());
							prevTimeQuantum = timeNow;			
						}					
					}
					prevTime = timeNow;
				
				}else{			
					
					if (allProcessesDone == 0  && getSize() == 0){
						System.out.println("size  " + getSize());
						allProcessesDone = 1;							
						
						if(level == Scheduler.getMaxLevelOfQueues() && Scheduler.processes.size() == 0) {
							System.out.println("doooonnnee!");
							simulationDone();
						}
					}
				}
			}
		}
	};
}

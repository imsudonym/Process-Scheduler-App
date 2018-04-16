public class Queue{
		
	private int SCHEDALGORITHM;
	private long quantum = 0;	
	PseudoArray array = new PseudoArray(100);	
	
	public Queue(int algorithm){
		this.SCHEDALGORITHM = algorithm;
	}
	
	public void setQuantum(long quantum){
		if(getSchedAlg() != SchedulingAlgorithm.RR){
			throw new java.lang.RuntimeException("Cannot set quantum in Queue that does not implement Round-Robin.");
		}
		
		this.quantum = quantum;
	}
	
	public long quantum(){return quantum;}
	
	public long getSchedAlg(){
		return SCHEDALGORITHM;
	}
	
	public boolean isEmpty(){
		return array.isEmpty();
	}
	
	public void enqueue(Process newProcess){
					
		array.add(newProcess);
		
		displayQueue(newProcess);		
		//CrazyCalculator.makeThreadSleep();
	}	
	
	public Process dequeue(){
					
		Process prc = array.remove();
						
		/*if(!CrazyCalculator.evaluatingPostfix){
			
			if(stringTemp.length() > 0)
				stringTemp = stringTemp.substring(1, stringTemp.length());			
			
			CrazyCalculator.sShots1.queueBlocks.setText(stringTemp);
		}else{
			
			if(stringTemp.length() > 0)					
				stringTemp = stringTemp.substring(str.length(), stringTemp.length());
			
			CrazyCalculator.sShots2.queueBlocks.setText(stringTemp);						
		}	*/
				
		//CrazyCalculator.makeThreadSleep();			
		
		return prc;
	}
	
	public Process peekHead(){
		//return array.get(getSize()-1).getValue(); 
		return array.get(0).getValue(); 
	}
	
	public Process peekTail(){
		return array.get(getSize()-1).getValue(); 		
	}
	
	public int getSize(){
		return array.getSize();
	}
	
	public void sortSJF(){
		
	}
	
	private void displayQueue(Process input){
		
		/*if(CrazyCalculator.evaluatingPostfix){			
			stringTemp += input;
			CrazyCalculator.sShots2.queueBlocks.setText(stringTemp);
		}else{

			stringTemp += input;
			CrazyCalculator.sShots1.queueBlocks.setText(stringTemp);

		}	*/
	}
	
}


public class PseudoArray {		
	private int size;	
	public int ctr = 0;
	LinkedList list = new LinkedList();	
	
	public PseudoArray(int size){
		this.size = size;
	}
	
	public boolean isEmpty(){
		return list.isEmpty();
	}
	
	public void add(Process value){		
		if(ctr <= size){
			
			Link newLink = new Link(value, ctr);
			list.add(newLink);
			ctr++;
			
			
		}else{
			throw new java.lang.RuntimeException("ArrayIndexOutOfBounds.");
		}		
	}
	
	public Link getHead(){
		return list.first;
	}
	
	public Link get(int index){
		return list.get(index);
	}
	
	public Process remove(){		
		Process prc = list.remove(); 
		if(prc != null){			
			ctr--;			
		}
		return prc;		
	}
	
	public int getSize(){		
		System.out.print("");
		return ctr;
	}	
	
	public void sortSJF(){			
		for(int i = 0; i <= ctr-1; i++){
			Link current = list.first;					
			
			while(true){
				if(current.next == null)
					break;
				
				long currBurst = current.getValue().getBurstTime();
				long nextBurst = current.next.getValue().getBurstTime();												
				
				if(currBurst > nextBurst){					
					Process temp = current.getValue();
					current.setValue(current.next.getValue());
					current.next.setValue(temp);
				}
				current = current.next;
			}			
		}
		
		printContents();
	}
	
	public void sortPriority(){
		for(int i = 0; i <= ctr-1; i++){
			Link current = list.first;					
			
			while(true){
				if(current.next == null)
					break;
				
				int currPriority = current.getValue().getPriority();
				int nextPriority = current.next.getValue().getPriority();												
				
				if(currPriority > nextPriority){					
					Process temp = current.getValue();
					current.setValue(current.next.getValue());
					current.next.setValue(temp);
				}
				current = current.next;
			}			
		}
		
		printContents();
	}
	
	public void sortNonPQ(){
		System.out.println("sortNonPQ was called");
		Link current = list.first;
		if(current != null){
			long timeEnd = current.getValue().getArrivalTime() + current.getValue().getBurstTime();
			
			for(int i = 0; i <= ctr-1; i++){
				if(current.next == null){
					continue;
				}else if(current.next.getValue().getArrivalTime() == current.getValue().getArrivalTime()){
					if(current.next.getValue().getPriority() < current.getValue().getPriority()){ //lower priority number, higher priority
						System.out.println("swapping");
						Process temp = current.getValue();
						current.setValue(current.next.getValue());
						current.next.setValue(temp);
						timeEnd += current.getValue().getBurstTime();
					}
				}else if(current.next.getValue().getArrivalTime() > current.getValue().getArrivalTime() &&
					current.next.getValue().getArrivalTime() <= timeEnd && current.getValue().getArrivalTime() <= timeEnd){
					if(current.next.getValue().getPriority() < current.getValue().getPriority()){ //lower priority number, higher priority
						System.out.println("swapping");
						Process temp = current.getValue();
						current.setValue(current.next.getValue());
						current.next.setValue(temp);
						timeEnd += current.getValue().getBurstTime();
					}
				}
				
				current = current.next;
			}
			
			printContents();
		}
	}
	
	public void printContents(){
		list.printContents();
	}
}

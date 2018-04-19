
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
	
	public Link get(int index){
		return list.get(index);
	}
	
	public Process remove(){		
		Process prc = list.remove(); 											
		ctr--;
		return prc;		
	}
	
	public int getSize(){
		return ctr;
	}	
	
	public void sortSJF(){	
		System.out.println("softSJF was called");
		for(int i = 0; i <= ctr-1; i++){
			Link current = get(0);
			
			if(current.next == null){
				continue;
			}else if(current.getValue().getBurstTime() > current.next.getValue().getBurstTime()){
				System.out.println("swapping");
				Process temp = current.getValue();
				current.setValue(current.next.getValue());
				current.next.setValue(temp);
			}
			
			current = current.next;
		}
		
		printContents();
	}
	
	public void sortNonPQ(){
		System.out.println("sortNonPQ was called");
			Link current = get(0);
			long timeEnd = current.getValue().getBurstTime();
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
	
	public void printContents(){
		for(int i = 0; i <= ctr; i++){
			Link current = get(i);			
			System.out.print("P" + current.getValue().getId() + "-");			
		}
	}
}

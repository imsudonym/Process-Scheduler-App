package datastructure;

import java.util.ArrayList;

import process.CPUBoundProcess;
import process.IOBoundProcess;

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
	
	public void add(CPUBoundProcess value){		
		Link newLink = new Link(value, value.toString());
		list.add(newLink);
		ctr++;
			
		//printContents();
	}
	
	public Link getHead(){
		return list.first;
	}
	
	public Link get(String index){
		return list.get(index);
	}
	
	public CPUBoundProcess remove(){		
		CPUBoundProcess prc = list.remove(); 
		if(prc != null){			
			ctr--;			
		}
		return prc;		
	}
	
	public int getSize(){		
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
					CPUBoundProcess temp = current.getValue();
					current.setValue(current.next.getValue());
					current.next.setValue(temp);
				}
				current = current.next;
			}			
		}
		
		//printContents();
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
					CPUBoundProcess temp = current.getValue();
					current.setValue(current.next.getValue());
					current.next.setValue(temp);
				}
				current = current.next;
			}			
		}
		
		//printContents();
	}
	
	public void sortNonPQ(){		
		Link current = list.first;
		if(current != null){
			long timeEnd = current.getValue().getArrivalTime() + current.getValue().getBurstTime();
			
			for(int i = 0; i <= ctr-1; i++){
				if(current.next == null){
					continue;
				}else if(current.next.getValue().getArrivalTime() == current.getValue().getArrivalTime()){
					if(current.next.getValue().getPriority() < current.getValue().getPriority()){ //lower priority number, higher priority						
						CPUBoundProcess temp = current.getValue();
						current.setValue(current.next.getValue());
						current.next.setValue(temp);
						timeEnd += current.getValue().getBurstTime();
					}
				}else if(current.next.getValue().getArrivalTime() > current.getValue().getArrivalTime() &&
					current.next.getValue().getArrivalTime() <= timeEnd && current.getValue().getArrivalTime() <= timeEnd){
					if(current.next.getValue().getPriority() < current.getValue().getPriority()){ //lower priority number, higher priority						
						CPUBoundProcess temp = current.getValue();
						current.setValue(current.next.getValue());
						current.next.setValue(temp);
						timeEnd += current.getValue().getBurstTime();
					}
				}
				
				current = current.next;
			}
		}
	}
	
	public void printContents(){
		list.printContents();
	}

	public void givePriorityToIoBounds() {
		ArrayList<Link> tempArray = new ArrayList<Link>();
		
		Link current = list.first;						
		while(true){
				
			CPUBoundProcess currProcess = current.getValue();												
				
			if(currProcess instanceof IOBoundProcess){	
				tempArray.add(current); 
				list.remove(current.getKey());
				ctr--;
			}
			
			if(current.next == null)
				break;
			
			current = current.next;
		}			
		
				
		for(int i = tempArray.size()-1; i >= 0; i--) {
			Link newLink = new Link(tempArray.get(i).getValue(), tempArray.get(i).getKey());
			list.addToFront(newLink);
			ctr++;
		}
	}
}

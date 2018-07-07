package datastructure;

import java.util.ArrayList;

import process.CPUBoundProcess;
import process.IOBoundProcess;

public class PseudoArray {		
	public int ctr = 0;
	LinkedList list = new LinkedList();	
	
	public PseudoArray(int size){
	}
	
	public boolean isEmpty(){
		return list.isEmpty();
	}
	
	public void add(CPUBoundProcess value){		
		Link newLink = new Link(value, value.toString());
		list.add(newLink);
		ctr++;
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
			Link current = list.first.next;					
			
			while(true){
				if(current == null || current.next == null)
					break;
				
				long currBurst = current.getValue().getBurstTime();
				long nextBurst = current.next.getValue().getBurstTime();												
				
				//System.out.println("[PseudoArray:] currBurst: " + currBurst + " nextBurst: " + nextBurst);
				if(currBurst > nextBurst){					
					CPUBoundProcess temp = current.getValue();
					current.setValue(current.next.getValue());
					current.next.setValue(temp);
				}
				current = current.next;
			}			
		}
		
		//System.out.println("[PseudoArray:] Sorting SJF");
		printContents();
	}
	
	public void sortSRTF(){			
		for(int i = 0; i <= ctr-1; i++){
			Link current = list.first;					
			
			while(true){
				if(current == null || current.next == null)
					break;
				
				long currBurst = current.getValue().getBurstTime();
				long nextBurst = current.next.getValue().getBurstTime();												
				
				//System.out.println("[PseudoArray:] currBurst: " + currBurst + " nextBurst: " + nextBurst);
				if(currBurst > nextBurst){					
					CPUBoundProcess temp = current.getValue();
					current.setValue(current.next.getValue());
					current.next.setValue(temp);
				}
				current = current.next;
			}			
		}
		
		//System.out.println("[PseudoArray:] Sorting SRTF");
		printContents();
	}
	
	public void sortPQ(){
		for(int i = 0; i <= ctr-1; i++){
			Link current = list.first;			
			while(true){
				if(current == null || current.next == null)
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
	}
	
	public void sortNPQ(){
		for(int i = 0; i <= ctr-1; i++){
			Link current = list.first.next;			
			while(true){
				if(current == null || current.next == null)
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

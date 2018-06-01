package datastructure;

import process.CPUBoundProcess;
import process.IOBoundProcess;

public class LinkedList{
	
	public static String stringTemp = "";
	public Link last = null;
	public Link first = null;		
	
	public boolean isEmpty(){
		return (first == null);
	}
	
	public void add(Link newLink){
		
		if(isEmpty()){
			
			last = newLink;			
			last.next = null;
			last.previous = null;
			first = last;
		}else{
			
			last.next = newLink;
			last.next.previous = last;
			last = last.next;
		}						
	}
	
	public void addToFront(Link newLink) {
		if(isEmpty()){			
			last = newLink;			
			last.next = null;
			last.previous = null;
			first = last;
		}else {
			newLink.next = first;
			first.previous = newLink;
			first = newLink;
		}
	}
	
	public Link get(String key){		
		if(!isEmpty()){
			Link current = first;
						
			String currKey = current.key;
			
			while(!currKey.equals(key)){			
				if(current.next == null){
					current = null;
					break;
				}else if(current.key.equals(last.key)){
					current = null;
					break;
				}else{
					current = current.next;
				}
				
				currKey = current.key;
			}
			
			return current;
		}
		
		return null;
	}	
	
	public Link remove(String index){		
		
		Link element = get(index);
		
		if(element == first) {
			remove();
		} else if(element == last){
			element.previous.next = null;
			last = element.previous;			
		} else {
			element.previous.next = element.next;
			element.next.previous = element.previous;
		}		
		return null;
	}	
	
	public CPUBoundProcess remove(){
		Link temp = null;						
		CPUBoundProcess process = null;
		
		if(!isEmpty()){
			temp = first;
			
			first = first.next;					
			
			process = temp.getValue();			
		}						
		
		if(process != null){			
			return process;
		}
		
		return null;
	}
	
	public void printContents(){
		if(!isEmpty()){
			System.out.print("[");
			Link current = first;
			while(true){
				if(current == null)
					break;
				System.out.print("p" + current.getValue().getId() + " ");
				current = current.next;
			}
			System.out.println("]");
		}else{
			System.out.println("[]");
		}
	}
		
}
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
			
			System.out.println("last = " + last.getValue().getId());
			last.next = newLink;
			System.out.println("last.next = " + last.next.getValue().getId());
			last.next.previous = last;
			System.out.println("last.next.previous = " + last.next.previous.getValue().getId());
			last = last.next;
			System.out.println("last = " + last.getValue().getId());
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
			System.out.println("p" + newLink.getValue().getId() + ".next = " + newLink.next.getValue().getId());
			first.previous = newLink;
			System.out.println("p" + first.getValue().getId() + ".previous = " + newLink.getValue().getId());
			first = newLink;
			System.out.println("first = p" + first.getValue().getId());
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
		System.out.println("| << Element to remove: p" + element.getValue().getId());
		
		if(element == first) {
			System.out.println("Element is == First");
			remove();
		} else if(element == last){
			System.out.println("Element is == Last");
			element.previous.next = null;
			last = element.previous;			
		} else {
			System.out.println("Element is == Neither First nor Last");
			element.previous.next = element.next;
			element.next.previous = element.previous;
		}
		/*
		if(!isEmpty()){
			Link current = first;
						
			int key = current.key;
			
			while(key != index){			
				if(current.next == null){
					current = null;
					break;
				}else if(current.key == last.key){
					current = null;
					break;
				}else{
					current = current.next;
				}
				
				key = current.key;
			}
			
			if(current != null) {
				if(current.next != null && current.previous != null) {
					current.previous.next = current.next;				
					current.next.previous = current.previous;
				}else {
					remove();
				}
			}
			
			return current;
		}*/
		
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
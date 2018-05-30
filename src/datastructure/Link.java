package datastructure;

import process.CPUBoundProcess;

public class Link {
	
	private CPUBoundProcess value;
	public Link next;
	public Link previous;
	public int key;
	
	public Link(CPUBoundProcess value, int i){
		this.value = value;
		this.next = null;
		key = i;
	}
	
	public CPUBoundProcess getValue(){
		return value;
	}

	public void setValue(CPUBoundProcess newValue) {
		this.value = newValue;
	}
}

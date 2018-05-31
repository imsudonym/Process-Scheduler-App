package datastructure;

import process.CPUBoundProcess;

public class Link {
	
	private CPUBoundProcess value;
	public Link next;
	public Link previous;
	public String key;
	
	public Link(CPUBoundProcess value, String key){
		this.value = value;
		this.next = null;
		this.key = key;
	}
	
	public CPUBoundProcess getValue(){
		return value;
	}

	public void setValue(CPUBoundProcess newValue) {
		this.value = newValue;
	}

	public String getKey() {
		return key;
	}
}

public class Link {
	
	private Process value;
	public Link next;
	public Link previous;
	public int key;
	
	public Link(Process value, int i){
		this.value = value;
		this.next = null;
		key = i;
	}
	
	public Process getValue(){
		return value;
	}

	public void setValue(Process newValue) {
		this.value = newValue;
	}
}

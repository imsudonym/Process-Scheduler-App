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
			newLink.next = null;
			first = newLink;
		}
		else{
			
			last.next = newLink;
			last = last.next;
		}						
	}
	
	public Link get(int index){		
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
			
			return current;
		}
		
		return null;
	}	
	
	public Process remove(){
		Link temp = null;						
		Process process = null;
		
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

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
			
			display(value);
			//CrazyCalculator.makeThreadSleep();
			
		}else{
			throw new java.lang.RuntimeException("ArrayIndexOutOfBounds.");
		}
		
	}
	
	public Link get(int index){
		return list.get(index);
	}
	
	public Process remove(){
		
		Process prc = list.remove(); 	
		
		/*if(!CrazyCalculator.evaluatingPostfix){
			if(stringTemp.length() > 0)
				stringTemp = stringTemp.substring(1, stringTemp.length());
			
			CrazyCalculator.sShots1.arrayBlocks.setText(stringTemp);
		}else{
			
			if(stringTemp.length() > 0)
				stringTemp = stringTemp.substring(str.length(), stringTemp.length());
			
			CrazyCalculator.sShots2.arrayBlocks.setText(stringTemp);
		}	
		
		CrazyCalculator.makeThreadSleep();		*/
								
		ctr--;
		return prc;
		
	}
	
	public int getSize(){
		return ctr;
	}	
	
	private void display(Process input){
		
		/*
		if(CrazyCalculator.evaluatingPostfix){			
			stringTemp += input;
			CrazyCalculator.sShots2.arrayBlocks.setText(stringTemp);
		}else{
			stringTemp += input;
			CrazyCalculator.sShots1.arrayBlocks.setText(stringTemp);

		}			*/
	}
}

package de.mink_ing.automation.blocks;

public class PushDetector extends DynamicBlock implements ITextualEventBlock {


	public PushDetector(int Ts) {
		super(Ts);
	}

	private boolean in_old = false;
	private boolean in_event = false; 
	
	//inputs
	private boolean in;

	//updateStates
	public void oneStep(){

		//pushed
		if(in){
			//rising edge
			if(!in_old){
				in_event = true;
			}
			
		}
		in_old = in;
	}

	public void setInput(boolean in){
		this.in = in;
	}

	
	public String getEventAsString() {
		return("1"); //always return "1" as event. Other events might be possible in future or advanced versions
	}
	
//	public String getStateAsString() {
//		if(this.in) {
//			return("CLOSED");
//		}
//		else {
//			return("OPEN");
//		}
//	}

	public boolean isEvent() {
		boolean ev = in_event;
		if(in_event) {
			in_event = false;
		}
		return(ev);
	}

}


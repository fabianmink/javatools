package de.mink_ing.automation.blocks;

public class DetectChange extends DynamicBlock implements ITextualStateBlock{
	
	public DetectChange(int Ts) {
		super(Ts);
	}

	//internal states
	private boolean in_old;
	private boolean first_run = true;
	private boolean state_change = true; //always report state change for first run
		
	//inputs
	private boolean in;
	
	//updateStates
	public void oneStep(){
		if(first_run) {
			first_run = false;
		}
		if(in_old != in) {
			state_change = true;
		}
		in_old = in;
	}
	
	public void setInput(boolean in){
		this.in = in;
	}
	
	public boolean getOutput(){
		return( (in_old!=in) && !first_run );
	}
	

	public String getStateAsString() {
		if(this.in) {
			return("1");
		}
		else {
			return("0");
		}
	}
	
//	public String getStateAsString() {
//		if(this.in) {
//			return("CLOSED");
//		}
//		else {
//			return("OPEN");
//		}
//	}

	public boolean isStateChanged() {
		boolean sc = state_change;
		if(state_change) {
			state_change = false;
		}
		return(sc);
	}
}


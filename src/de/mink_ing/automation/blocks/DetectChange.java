package de.mink_ing.automation.blocks;

public class DetectChange extends DynamicBlock{
	
	public DetectChange(int Ts) {
		super(Ts);
	}

	//internal states
	private boolean in_old;
	private boolean first_run = true;
		
	//inputs
	private boolean in;
	
	//updateStates
	public void oneStep(){
		if(first_run) {
			first_run = false;
		}		
		in_old = in;
	}
	
	public void setInput(boolean in){
		this.in = in;
	}
	
	public boolean getOutput(){
		return( (in_old!=in) && !first_run );
	}
}


package de.mink_ing.automation.blocks;

public class ChangeOnPulse extends DynamicBlock{
	
	public ChangeOnPulse(int Ts) {
		super(Ts);
	}

	//internal states
	private boolean state = false;
			
	//inputs
	private boolean in;
	private boolean in_old;
	
	//updateStates
	public void oneStep(){
		if(in & !in_old) {
			state = !state;
		}				
		in_old = in;
	}
	
	public void setInput(boolean in){
		this.in = in;
	}
	
	public boolean getOutput(){
		return( state );
	}
}


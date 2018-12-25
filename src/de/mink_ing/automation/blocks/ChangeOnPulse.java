package de.mink_ing.automation.blocks;

public class ChangeOnPulse extends DynamicBlock{
	
	public ChangeOnPulse(int Ts) {
		super(Ts);
	}

	//internal states
	private boolean state = false;
	private boolean in_old;
	
	//inputs
	private boolean in;
	private boolean forceOn;
	private boolean forceOff;
	
	//updateStates
	public void oneStep(){
		
		
		if(forceOff) { //Force off has highest prio
			state = false; 
			forceOff = false; //Reset force off (might be set again in next cycle)
		}
		else if(forceOn) { //Force on has prio over toggle
			state = true; 
			forceOn = false; //Reset force off (might be set again in next cycle)
		}
		else if(in & !in_old) { //Toggle has lowest prio
			state = !state;
		}
		in_old = in;
	}
	
	public void setInput(boolean in){
		this.in = in;
	}
	
	public void forceOn(boolean in){
		this.forceOn = in;
	}
	
	public void forceOff(boolean in){
		this.forceOff = in;
	}
	
	public boolean getOutput(){
		return( state );
	}
}


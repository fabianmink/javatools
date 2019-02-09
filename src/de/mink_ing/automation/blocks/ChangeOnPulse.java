package de.mink_ing.automation.blocks;

public class ChangeOnPulse extends DynamicBlock implements ITextualStateAndCmdBlock {
	
	public ChangeOnPulse(int Ts) {
		super(Ts);
	}

	//internal states
	private boolean state = false;
	private boolean in_old;
	private boolean state_change = true;
	
	//inputs
	private boolean in;
	private boolean forceOn;
	private boolean forceOff;
	
	//updateStates
	public void oneStep(){
		boolean state_old = state;
		
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
		if(state_old != state) {
			state_change = true;
		}
	}
	
	public void setInput(boolean in){
		this.in = in;
	}
	
	public void forceOn() {
		this.forceOn = true;
	}
	
	public void forceOff() {
		this.forceOff = true;
	}
	//public void forceOn(boolean in){
	//	this.forceOn = in;
	//}
	
	//public void forceOff(boolean in){
	//	this.forceOff = in;
	//}
	
	public boolean isStateChanged(){
		boolean sc = state_change;
		if(state_change) {
			state_change = false;
		}
		return(sc);
	}
	
	public boolean getOutput(){
		return( state );
	}

	public String getStateAsString() {
		if(getOutput()) {
			return("ON");
		}
		else {
			return("OFF");
		}
	}

	public void cmdAsString(String cmd) {
		//System.out.println("check: " + cmd);
		if(cmd.equalsIgnoreCase("ON")){
			this.forceOn = true;
			//System.out.println("F. ON");
		}
		if(cmd.equalsIgnoreCase("OFF")){
			this.forceOff = true;
		}
	}
}


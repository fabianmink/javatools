package de.mink_ing.automation.blocks;

public class StateInteger extends DynamicBlock implements ITextualStateAndCmdBlock {
	
	public StateInteger(int Ts) {
		super(Ts);
	}

	//internal states
	private int state = 0;
	private boolean state_change = true; //always report state change for first run
	
	//inputs
	private int in;
	
	//updateStates
	public void oneStep(){
		
		if(state != in) { 
			state = in;
			state_change = true;
		}
		
	}
	
	public void setInput(int in){
		this.in = in;
	}
	
		
	public boolean isStateChanged(){
		boolean sc = state_change;
		if(state_change) {
			state_change = false;
		}
		return(sc);
	}
	
	public int getOutput(){
		return( state );
	}

	public String getStateAsString() {
		return("" + this.getOutput());
	}

	public void cmdAsString(String cmd) {
		int cmdi = 0;
		try {
			cmdi = Integer.parseInt(cmd);
		}
		catch (Exception e) {
			// do nothing, cmd will be interpreted as 0
		}
		
		this.setInput(cmdi);
	}
	
	public String getTopicExtensionAsString() {
		return null;
	}
}


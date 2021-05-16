package de.mink_ing.automation.blocks;

public class Hold extends DynamicBlockLog implements ITextualStateAndCmdBlock{
	
	public Hold(int Ts) {
		super(Ts);
	}

	//Parameters
	private int hold_normalized = 1000;
	
	//internal states
	private int state_cnt = 0;
	private boolean state_out = false;
	
	private boolean state_change = true; //always report state change for first run
	
	//inputs
	private boolean in = false;
	private boolean forceOff = false;
	private boolean forceOn = false;
	
	public void setHoldTime(int holdTime){
		this.hold_normalized = holdTime/Ts;
	}
	
	//updateStates
	public void oneStep(){
		
		boolean state_old = state_out;
		
		if(in || forceOn){
			forceOn = false;
			if(state_cnt == 0){
				if(logger != null) logger.fine(name + ": On");
			}
			state_cnt = hold_normalized;
		}
		
		//Perform update of internal states
		if(state_cnt > 0){
			state_cnt--;
			state_out = true;
			if(state_cnt == 0){
				if(logger != null) logger.fine(name + ": Regular off");
			}
		}
		else {
			state_out = false;
		}
		
		
		if(forceOff){
			forceOff = false;
			if(state_cnt > 0){
				if(logger != null) logger.fine(name + ": Forced off");
			}
			state_cnt = 0;
			state_out = false;
		}
		
		if(state_old != state_out) {
			state_change = true;
		}
	}
	
	public void setInput(boolean in){
		this.in = in;
	}
	
	public void setForceOff(boolean in){
		this.forceOff = in;
	}
	
	public boolean getOutput(){
		return(state_out);
	}
	
	public boolean isStateChanged(){
		boolean sc = state_change;
		if(state_change) {
			state_change = false;
		}
		return(sc);
	}
	
	public String getStateAsString() {
		if(getOutput()) {
			//return("ON");
			return("1");
		}
		else {
			//return("OFF");
			return("0");
		}
	}

	public void cmdAsString(String cmd) {
		//System.out.println("check: " + cmd);
		if(cmd.equalsIgnoreCase("ON") || cmd.equals("1") ){
			this.forceOn = true;
			//System.out.println("F. ON");
		}
		if(cmd.equalsIgnoreCase("OFF") || cmd.equals("0")){
			this.forceOff = true;
		}
	}
	
	public String getTopicExtensionAsString() {
		return null;
	}
}


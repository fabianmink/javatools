package de.mink_ing.automation.blocks;

public class Hold extends DynamicBlockLog{
	
	public Hold(int Ts) {
		super(Ts);
	}

	//Parameters
	private int hold_normalized = 1000;
	
	//internal states
	private int state_cnt = 0;
	private boolean state_out = false;
	
	//inputs
	private boolean in = false;
	private boolean forceOff = false;
	
	public void setHoldTime(int holdTime){
		this.hold_normalized = holdTime/Ts;
	}
	
	//updateStates
	public void oneStep(){
		
		if(in){
			if(state_cnt == 0){
				if(logger != null) logger.fine(name + ": On");
			}
			state_cnt = hold_normalized;
		}
		
		//Perform update of internal states
		state_out = false;
		if(state_cnt > 0){
			state_cnt--;
			state_out = true;
			if(state_cnt == 0){
				if(logger != null) logger.fine(name + ": Regular off");
			}
		}
		
		
		if(forceOff){
			if(state_cnt > 0){
				if(logger != null) logger.fine(name + ": Forced off");
			}
			state_cnt = 0;
			state_out = false;
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
}


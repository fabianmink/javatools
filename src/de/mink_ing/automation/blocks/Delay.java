package de.mink_ing.automation.blocks;

public class Delay extends DynamicBlock {
	
	public Delay(int Ts) {
		super(Ts);
	}
	
	//Parameters
	private int tick_normalized = 1000;
		
	//internal states
	private int state_tickcnt = 1000;
	private int state = 0;
	
	private boolean out = false;
	
	
	//inputs
	private boolean in;
		
	public void setTickTime(int tickTime){
		this.tick_normalized = tickTime/Ts;
	}
		
	//updateStates
	public void oneStep(){
		if(out) {
			out = false;
		}
		if(in) {
			state = 1;
		}
		if(state > 0) {
			state++;
		}
		if(state >= state_tickcnt) {
			out = true;
			state = 0;
		}
	}
	
	public void setInput(boolean in){
		this.in = in;
	}
	
	public boolean getOutput(){
		return(out);
	}
	
}

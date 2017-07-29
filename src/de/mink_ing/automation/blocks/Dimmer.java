package de.mink_ing.automation.blocks;

public class Dimmer extends DynamicBlock {
	
	public Dimmer(int Ts) {
		super(Ts);
	}
	
	//Ts is not really necessary
	public Dimmer() {
		this(1);
	}
	
	
	//Parameters
	//private int tick_normalized = 1000;
	private int max = 100;	
	
	//internal states
	//private int state_tickcnt = 0;
	private int level = 0;
	
	//inputs
	private boolean in_up;
	private boolean in_dn;
		
	//public void setTickTime(int tickTime){
	//	this.tick_normalized = tickTime/Ts;
	//}
	
	//updateStates
	public void oneStep(){
		
		//Perform update of internal states
		//state_tickcnt++;
		//if(state_tickcnt >= tick_normalized){
		//	state_tickcnt = 0;
		//}
		
		//todo: Add functionality for pressing both buttons, e.g. dim to 0 or 100%
		if(in_up){
			level++;
		}
		if(in_dn){
			level--;
		}
		if(level < 0) level = 0;
		if(level > max) level = max;
		
		
	}
	
	public void setInputUp(boolean in){
		this.in_up = in;
	}
	
	public void setInputDn(boolean in){
		this.in_dn = in;
	}
	
	public int getLevel(){
		return(this.level);
	}

	public void setMax(int max) {
		this.max = max;
		
	}
}


package de.mink_ing.automation.blocks;

public class Chase extends DynamicBlock {
	
	public Chase(int Ts) {
		super(Ts);
	}
	
	//Parameters
	private int tick_normalized = 1000;
	private int noOfOutputs = 8;
	
	//internal states
	private int state_tickcnt = 0;
	private int state_no = 0;
	
	
	public void setTickTime(int tickTime){
		this.tick_normalized = tickTime/Ts;
	}
	
	public void setNoOfOutputs(int noOfOutputs){
		this.noOfOutputs = noOfOutputs;
	}
	
	
	//updateStates
	public void oneStep(){
		
		//Perform update of internal states
		state_tickcnt++;
		if(state_tickcnt >= tick_normalized){
			state_tickcnt = 0;
			state_no++;
			if(state_no >= noOfOutputs) state_no = 0;
		}
	}
	
	public boolean getOutput(int no){
		return(state_no == no);
	}
	
	public int getBinaryOutput(){
		int val = 0;
		
		if(state_no < 31){
			val = 1 << state_no;
		}
		return(val);
	}
}


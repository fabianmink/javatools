package de.mink_ing.automation.blocks;

public class Counter extends DynamicBlock {
			
	public Counter(int Ts) {
		super(Ts);
	}
	
	//Parameters
	private int clkPeriod_normalized = 1000;
	private int cntMax = 255;
		
	//internal states
	private int state_tickcnt = 0;
	private int state_cnt = 0;
		
	
	public void setClkPeriod(int clkPeriod){
		this.clkPeriod_normalized = clkPeriod/Ts;
	}
	
	public void setCntMax(int cntMax){
		this.cntMax = cntMax;
	}
		
	//updateStates
	public void oneStep(){
		
		//Perform update of internal states
		state_tickcnt++;
		if(state_tickcnt >= clkPeriod_normalized){
			state_tickcnt = 0;
			state_cnt++;
			if(state_cnt > cntMax) state_cnt = 0;
		}
		
	}
	
	public int getOutput(){
		return(state_cnt);
	}
}

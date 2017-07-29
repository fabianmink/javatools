package de.mink_ing.automation.blocks;

public class DelayLine extends DynamicBlock {
	
	public DelayLine(int Ts) {
		super(Ts);
	}
	
	//Parameters
	private int tick_normalized = 1000;
	private int noOfOutputs = 8;
		
	//internal states
	private int state_tickcnt = 0;
	private int state = 0;
	private boolean reverse = false;
	
	//inputs
	private boolean in;
		
	public void setTickTime(int tickTime){
		this.tick_normalized = tickTime/Ts;
	}
	
	public void setNoOfOutputs(int noOfOutputs){
		if(noOfOutputs > 32) throw new RuntimeException("No of outputs too big");
		if(noOfOutputs < 1) throw new RuntimeException("No of outputs must be at least 1");
		
		this.noOfOutputs = noOfOutputs;
	}
		
	//updateStates
	public void oneStep(){
		
		//Perform update of internal states
		state_tickcnt++;
		if(state_tickcnt >= tick_normalized){
			state_tickcnt = 0;
			
			if(!reverse){
				state <<= 1;
				if(in) {
					state |= 1;
				}
			}
			else {
				state >>= 1;
				if(in) {
					state |= (1<<(noOfOutputs-1));
				}
			}
			
		}
	}
	
	public void setInput(boolean in){
		this.in = in;
	}
	
	public boolean getOutput(int no){
		return((state & (1<<no)) != 0);
	}
	
	public void setReverseDirection(boolean reverse){
		this.reverse = reverse;
	}
	
	public int getBinaryOutput(){
		return(state);
	}
}


package de.mink_ing.automation.blocks;

public class Blink extends DynamicBlock implements ITextualStateBlock {
			
	public Blink(int Ts) {
		super(Ts);
	}
	
	//Parameters
	private int onTime_normalized = 1000;
	private int offTime_normalized = 1000;
	
	//internal states
	private int state_cnt = 0;
	private boolean state_on = false;
	
	
	public void setOnTime(int onTime){
		this.onTime_normalized = onTime/Ts;
	}
	
	public void setOffTime(int offTime){
		this.offTime_normalized = offTime/Ts;
	}
	
	//updateStates
	public void oneStep(){
		
		//Perform update of internal states
		if(state_on == false ){
			if(state_cnt >= offTime_normalized){
				state_cnt = 0;
				state_on = true;
			}
		}
		else{
			if(state_cnt >= onTime_normalized){
				state_cnt = 0;
				state_on = false;
			}
		}
		state_cnt++;
	}
	
	public boolean getOutput(){
		return(state_on);
	}

	public String getStateAsString() {
		return("");
	}

	public boolean isStateChanged() {
		return false;
	}
	
	public String getTopicExtensionAsString() {
		return null;
	}
}

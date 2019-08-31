package de.mink_ing.automation.blocks;

public class PushDimmer extends DynamicBlock {
	
	protected PushDetectorAdvanced pd;
	
	public PushDimmer(int Ts) {
		super(Ts);
		pd = new PushDetectorAdvanced(this.Ts);
	}

	//Ts is not really necessary
	public PushDimmer() {
		this(1);
	}

	//Parameters
	protected int max = 100;
	protected int min = 1;
	protected float dim_speed = 1.0f;
	
	//Inputs
	protected boolean forceOn;
	protected boolean forceOff;

	//internal states
	protected float level = 0;
	
	protected boolean dim_up = true;
	protected boolean is_on = false;
	
	protected boolean was_long;

	//updateStates
	public void oneStep(){

		pd.oneStep();
		boolean is_long = pd.getLong();
		
		if(forceOff){
			is_on = false;
			forceOff = false;
		}
		else if(forceOn){
			is_on = true;
			forceOn = false;
		}
		else if(pd.getShortClicks()==1){ //single push
			is_on = !is_on;
		}
		else if(is_long){
			if(is_on){ //already on?
				if(!was_long) { //rising edge?
					dim_up = !dim_up;
				}
				else {
					if(dim_up){
						level += dim_speed;
					}
					else{
						level -= dim_speed;
					}
				}
			}
			else { //dim from off
				is_on = true;
				level = min;
				dim_up = true;
			}
		}

		was_long = is_long;
		
		if(level < min) level = min;
		if(level > max) level = max;		
	}

	public void setInput(boolean in){
		pd.setInput(in);
	}
	
	public void setForceOn(boolean in){
		this.forceOn = in;
	}
	
	public void setForceOff(boolean in){
		this.forceOff = in;
	}
	
	public boolean isOn(){
		return(this.is_on);
	}
	
	public int getLevel(){
		if(is_on){
			return((int)this.level);
		}
		return(0);
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setMin(int min) {
		this.min = min;
	}
	
	public void setDimSpeed(float speed){
		this.dim_speed = speed;
	}
}


package de.mink_ing.automation.blocks;

public class PushDimmerPresence extends PushDimmer {


	public PushDimmerPresence(int Ts) {
		super(Ts);
	}

	//Ts is not really necessary
	public PushDimmerPresence() {
		super();
	}


	protected boolean presence;
	
	protected int dimlevel = 1;
	protected int dimtime = 1000;
	protected int offtime = 1000; //after dimtime

	protected int timer;

	protected boolean is_dim = false;
	


	//updateStates
	public void oneStep(){

		pd.oneStep();
		if(timer > 0){
			timer--;
		}
		boolean is_long = pd.getLong();

		
		if(forceOff){
			is_on = false;
			forceOff = false;
		}
		else if(forceOn){
			timer = dimtime; //Reset timer
			is_on = true;
			is_dim = false;
			forceOn = false;
		}
		else if(pd.getShortClicks()==1){ //single push
			timer = dimtime; //Reset timer
			if(is_on){
				if(is_dim){
					is_dim = false;
				}
				else {
					is_on = false;
				}
			}
			else {
				is_on = true;
				is_dim = false;
			}
		}
		else if(is_long){
			timer = dimtime; //Reset timer
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
		else if(presence){
			presence = false;
			timer = dimtime; //Reset timer
			if(is_on){
				if(is_dim){
					is_dim = false;
				}
			}
		}
		else if(timer == 0){ //Timer elapsed
			if(is_on){
				if(is_dim){
					is_on = false;
				}
				else{
					is_dim = true;
					timer = offtime;
				}
			}
		}
		
		was_long = is_long;
		
		if(level < min) level = min;
		if(level > max) level = max;		
	}

	public void setPresence(boolean in){
		this.presence = in;
	}

	public void setDim(int dim) {
		this.dimlevel = dim;
	}

	public void setTimeDim(int time) {
		this.dimtime = time;
	}

	public void setTimeOff(int time) {
		this.offtime = time;
	}
	
	public int getLevel(){
		if(is_on){
			if(is_dim){
				return(Math.min(this.dimlevel,(int)this.level));
			}
			else{
				return((int)this.level);
			}
		}
		return(0);
	}




}


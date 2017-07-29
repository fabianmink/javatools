package de.mink_ing.automation.blocks;

public class Debounce extends DynamicBlock{
	
	public Debounce(int Ts) {
		super(Ts);
	}

	//parameters
	private int debounce_time=0;
	
	//internal states
	private int timer = 0;
	private boolean in_old;
	
	//inputs
	private boolean in;
	
	//updateStates
	public void oneStep(){
		if(timer > 0){
			timer--;
		}
		else {
			if(in_old!=in) {
				in_old = in;
				timer = debounce_time;
			}
		}
	}
	
	public void setInput(boolean in){
		this.in = in;
	}
	
	public boolean getOutput(){
		if(timer == 0) return(in);
		else return(in_old);
	}
	
	public void setDebounceTime(int debounceTime){ 
		this.debounce_time = debounce_time/Ts;
	}
}


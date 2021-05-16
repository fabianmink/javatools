package de.mink_ing.automation.blocks;

public class EventCounter extends DynamicBlock implements ITextualStateBlock {
	
	public EventCounter(int Ts) {
		super(Ts);
	}
	
	public EventCounter(int Ts, int countInit) {
		super(Ts);
		this.count = countInit;
	}

	//internal states
	private int count = 0; //default
	private boolean in_old;
	private boolean state_change = false;
	private boolean first_run = true;
	
	//inputs
	private boolean in;
	
	//updateStates
	public void oneStep(){
		if(first_run) {
			first_run = false;
			in_old = in;
			return;
		}
		if(in & !in_old) { //rising edge
			count++;
			state_change = true;
		}
		in_old = in;
	}
	
	public void setInput(boolean in){
		this.in = in;
	}
	
	public boolean isStateChanged(){
		boolean sc = state_change;
		if(state_change) {
			state_change = false;
		}
		return(sc);
	}
	
	public int getCount(){
		return( count );
	}

	public String getStateAsString() {
		return(String.valueOf(getCount()));
	}
	
	public String getTopicExtensionAsString() {
		return null;
	}
}


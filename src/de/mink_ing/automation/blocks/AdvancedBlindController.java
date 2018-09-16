package de.mink_ing.automation.blocks;


//todo: Not working. Improve!!
public class AdvancedBlindController extends BlindController {
	
	private enum states{
		state_off,
		state_runDown,
		state_runUp,
		state_jogDown,
		state_jogUp,
		state_wait,
	}
	
	states myState = states.state_off;
	
	
	private final int runTime = 1000;
	private final int jogTime = 2;
	private final int waitTime = 10;
	
	private int timer;
	
	private boolean in_up;
	private boolean in_dn;

	private boolean rem_up;
	private boolean rem_dn;
	
	
	public AdvancedBlindController(int Ts) {
		super(Ts);
	}
	
	//todo:
	//Short press -> run until press again or press other button
	//Long press -> run until release
	
	public void setInputUp(boolean in){
		this.in_up = in;
	}

	public void setInputDn(boolean in){
		this.in_dn = in;
	}
	
	public void oneStep(){

		switch(myState) {
		case state_off:
			rem_dn = false;
			rem_up = false;
			if(in_dn) {
				timer = jogTime;
				myState = states.state_jogDown;
			}
			if(in_up) {
				timer = jogTime;
				myState = states.state_jogUp;
			}
			break;
		
		case state_jogDown:
			rem_dn = true;
			if(timer > 0) {
				timer--;
				if(!in_dn) {
					timer = runTime;
					myState = states.state_runDown;
				}
			}
			else {
				if(!in_dn) {
					myState = states.state_off;
				}
			}
			break;
		
		case state_jogUp:
			rem_up = true;
			if(timer > 0) {
				timer--;
				if(!in_up) {
					timer = runTime;
					myState = states.state_runUp;
				}
			}
			else {
				if(!in_up) {
					myState = states.state_off;
				}
			}
			break;
			
		case state_runDown:
			if(timer > 0) {
				timer--;
				if(in_up || in_dn) {
					timer = waitTime;
					myState = states.state_wait;
				}
			}
			else {
				myState = states.state_off;
			}
			break;
			
		case state_runUp:
			if(timer > 0) {
				timer--;
				if(in_up || in_dn) {
					timer = waitTime;
					myState = states.state_wait;
				}
			}
			else {
				myState = states.state_off;
			}
			break;
			
		case state_wait:
			rem_dn = false;
			rem_up = false;
			if(timer > 0) {
				timer--;
			}
			else {
				myState = states.state_off;
			}
		

		}

		
		super.setInputDn(rem_dn);
		super.setInputUp(rem_up);
				
		super.oneStep();
		
	}
	

}

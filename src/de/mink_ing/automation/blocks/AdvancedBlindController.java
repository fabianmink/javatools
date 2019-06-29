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
	
	private boolean force_up = false;
	private boolean force_dn = false;
	private boolean force_st = false;
	
	
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
	
	public void forceUp() {
		force_up = true;
	}
	
	public void forceDown() {
		force_dn = true;
	}
	
	public void forceStop() {
		force_st = true;
	}
	
	public void oneStep(){

		switch(myState) {
		case state_off:
			rem_dn = false;
			rem_up = false;
			if(force_st) {
				force_st = false;
			}
			if(force_dn) {
				force_dn = false;
				timer = runTime;
				myState = states.state_runDown;
			}
			if(force_up) {
				force_up = false;
				timer = runTime;
				myState = states.state_runUp;
			}
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
			rem_up = false;
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
			rem_dn = false;
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
			rem_dn = true;
			rem_up = false;
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
			if(force_st || force_dn || force_up) {
				//go to off, but keep force cmd.
				myState = states.state_off;
			}
			break;
			
		case state_runUp:
			rem_dn = false;
			rem_up = true;
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
			if((force_st || force_dn || force_up) == true) {
				//go to off, but keep force cmd.
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
	
	public void cmdAsString(String cmd) {
		System.out.println("check: " + cmd);
		if(cmd.equalsIgnoreCase("STOP")){
			this.force_st = true;
		}
		if(cmd.equalsIgnoreCase("DOWN")){
			this.force_dn = true;
		}
		if(cmd.equalsIgnoreCase("UP")){
			this.force_up = true;
		}
	}

}

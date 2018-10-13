package de.mink_ing.automation.blocks;

public class BlindController extends DynamicBlock {

	public BlindController(int Ts) {
		super(Ts);
	}

	//Parameters
	//private int upTime = 100;
	//private int downTime = 100;

	//private int blindUpTime = 10;
	//private int blindDownTime = 10;

	private int lockTime = 4;
	private int updnTailTime = 100;


	//internal states
	//private int position = 0;
	//private int blindAngle = 0;

	private int lock_up;
	private int lock_dn;
	private int lock_run;
	private int lock_updn;
	private int updn_tail;


	private boolean in_up;
	private boolean in_dn;

	private boolean up;
	private boolean dn;
	private boolean updn;
	private boolean run;


	//updateStates
	public void oneStep(){
		up = false;
		dn = false;
		run = false;

		if(lock_dn > 0) lock_dn--;
		if(lock_up > 0) lock_up--;
		if(lock_run > 0) lock_run--;
		if(lock_updn > 0) lock_updn--;
		
		if(updn_tail > 0) updn_tail--;
		
		if((updn_tail == 0) && (updn == true)) {
			if(lock_updn == 0) {
				lock_run = lockTime;
				updn = false;
			}
		}
		
		if(in_up && !in_dn) { //Up request
			if(lock_up == 0) {
				up = true;
				lock_dn = lockTime;
			}
			
			if(updn == true) {
				updn_tail = updnTailTime;
				if(lock_run == 0) {
					run = true;
					lock_updn = lockTime; //lock up/dn while running
				}
			}
			else {
				if(lock_updn == 0) {
					updn = true;
					updn_tail = updnTailTime;
					lock_run = lockTime; //lock run after updn switching
				}
			}
			
		}

		if(in_dn && !in_up) { //Down request
			if(lock_dn == 0) {
				dn = true;
				lock_up = lockTime;
			}
			
			
			if(updn == false) {
				if(lock_run == 0) {
					run = true;
					lock_updn = lockTime; //lock up/dn while running
				}
			}
			else {
				if(lock_updn == 0) {
					updn = false;
					lock_run = lockTime; //lock run after updn switching
				}
			}
		}

		//dual press -> lock both
		//could be some special function
		if(in_dn && in_up) {
			lock_dn = lockTime;
			lock_up = lockTime;
		}
		
		if(run) {
			lock_updn = lockTime; //lock up/dn while running
		}
	}

	public void setInputUp(boolean in){
		this.in_up = in;
	}

	public void setInputDn(boolean in){
		this.in_dn = in;
	}

	public boolean getOutputUp(){
		return(up);
	}

	public boolean getOutputDown(){
		return(dn);
	}

	//true = up
	//false = down
	public boolean getOutputUpdown(){
		return(updn);
	}

	public boolean getOutputRun(){
		return(run);
	}


}

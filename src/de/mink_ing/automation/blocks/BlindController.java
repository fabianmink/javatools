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

	private int lockTime = 10;
	private int updnTailTime = 100;


	//internal states
	//private int position = 0;
	//private int blindAngle = 0;

	private int lock_up;
	private int lock_dn;
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
		if(updn_tail > 0) updn_tail--;
		else updn = false;
		
		if(in_up && !in_dn) {
			if(lock_up == 0) {
				up = true;
				lock_dn = lockTime;
			}
		}

		if(in_dn && !in_up) {
			if(lock_dn == 0) {
				dn = true;
				lock_up = lockTime;
			}
		}

		//dual press -> lock both
		//could be some special function
		if(in_dn && in_up) {
			lock_dn = lockTime;
			lock_up = lockTime;
		}
		
		if(up) {
			run = true;
			updn = true;
			updn_tail = updnTailTime;
		}
		
		if(dn) {
			run = true;
			updn = false;
			updn_tail = 0;
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

package de.mink_ing.automation.blocks;

public class PushDetector extends DynamicBlock {

	//Parameters
	private int tPush = 5;


	public PushDetector(int Ts) {
		super(Ts);
	}

	//internal states
	private int timer = 0;
	private int pushCnt = 0;
	private boolean in_old = false;

	//inputs
	private boolean in;

	//outputs
	private boolean out_long;
	private boolean out_short;

	//updateStates
	public void oneStep(){

		//reset out_short
		if(out_short){
			out_short = false;
			pushCnt = 0;
		}

		//pushed
		if(in){
			//rising edge
			if(!in_old){
				timer = 0;
			}
			else {
				if(timer >= tPush){
					out_long = true;
				}
				else{
					timer++;
				}
			}
		}
		//not pushed
		else {
			//falling edge
			if(in_old){
				timer = 0;
				if(!out_long){
					pushCnt++;
				}
			}
			else{
				if(timer >= tPush){
					out_short = true;
				}
				else {
					timer++;
				}
			}
			out_long = false;
		}

		in_old = in;
	}

	public void setInput(boolean in){
		this.in = in;
	}

	public boolean getLong(){
		return(out_long);
	}

	public boolean getShort(){
		return(out_short);
	}

	public int getShortClicks(){
		if(out_short) return(pushCnt);
		else return(0);
	}

	public void settPush(int tPush) {
		this.tPush = tPush/Ts;
	}

}


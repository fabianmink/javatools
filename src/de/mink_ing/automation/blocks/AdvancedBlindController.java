package de.mink_ing.automation.blocks;


//todo: Not working. Improve!!
public class AdvancedBlindController extends BlindController {
	
	
	private int runTime = 1000;
	private int minRunTime = 10;
	
	private boolean in_up;
	private boolean in_dn;
	private int running;
	private boolean rem_up;
	private boolean rem_dn;
	
	private Debounce db_up;
	private Debounce db_dn;
	
	public AdvancedBlindController(int Ts) {
		super(Ts);
		db_up = new Debounce(Ts);
		db_dn = new Debounce(Ts);
		db_up.setDebounceTime(1000);
		db_dn.setDebounceTime(1000);
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
		
		
		if(running > 0) {
			running--;
			if(db_up.getOutput() || db_dn.getOutput()) {
				running = 0;
				rem_up = false;
				rem_dn = false;
			}
		}
		else {
			if(db_dn.getOutput()) {
				running = runTime;
				rem_dn = true;
			}
			else if(db_up.getOutput()) {
				running = runTime;
				rem_up = true;
			}
		}
		
		db_up.setInput(in_up);
		db_dn.setInput(in_dn);
		
		db_up.oneStep();
		db_dn.oneStep();
		
		super.setInputDn(rem_dn);
		super.setInputUp(rem_up);
				
		super.oneStep();
		
	}
	

}

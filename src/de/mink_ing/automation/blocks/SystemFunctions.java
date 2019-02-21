package de.mink_ing.automation.blocks;

import java.io.IOException;

public class SystemFunctions extends Block implements ITextualCmdBlock{
	
	class SystemThread implements Runnable{

		private SystemFunctions sf;
		
		public void setSF(SystemFunctions sf){
			this.sf = sf;
		}

		@Override
		public void run() {
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

			while(true){
				sf.periodicCallback();
				try {
					Thread.sleep(sf.getSampleTime_ms());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		//	public SystemThread() throws Exception{
		//	}

		//	protected void finalize(){
		//		//Destructor
		//	}
	}
	
	private SystemThread sThread;
	private boolean doSd = false;
	private boolean doRb = false;
	private boolean isInitiatedSdRb = false;
	

	public void start(){
		//*** Start communication thread ***
		//Must be called only once!
		//commThread.setCommunicator(this);
		//(new Thread(commThread)).start();
	}
	
	
	
	//Constructor
	public SystemFunctions() {
		sThread = new SystemThread();
		sThread.setSF(this);
		(new Thread(sThread)).start();
	}
	
	private void periodicCallback(){
		if(!isInitiatedSdRb) {
			
			if(doSd) {
				isInitiatedSdRb = true;
				try {
					Process p = Runtime.getRuntime().exec("halt");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(doRb) {
				isInitiatedSdRb = true;
				try {
					Process p = Runtime.getRuntime().exec("reboot");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public int getSampleTime_ms() {
		return(1000);
	}
	
	public void shutdown(){
		doSd = true;
	}
	
	public void reboot(){
		doRb = true;
	}
	
	public void cmdAsString(String cmd) {
		//System.out.println("check: " + cmd);
		if(cmd.equalsIgnoreCase("SHUTDOWN") || cmd.equalsIgnoreCase("HALT") ){
			this.doSd = true;
			System.out.println("shutdown initiating");
		}
		if(cmd.equalsIgnoreCase("REBOOT")){
			this.doRb = true;
			System.out.println("reboot initiating");
		}
	}
}


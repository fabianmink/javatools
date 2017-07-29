package de.mink_ing.dali;

import java.util.logging.Level;
import java.util.logging.Logger;

class CommunicationThread implements Runnable{

	private DaliMasterModule masterModule;
	
	public void setMasterModule(DaliMasterModule masterModule){
		this.masterModule = masterModule;
	}

	@Override
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		try {
			while(true){
				//System.out.println("I'm here!");
				masterModule.periodicCallback();
				Thread.sleep(100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//	public CommunicationThread() throws Exception{
	//	}

	//	protected void finalize(){
	//		//Destructor
	//	}

}


class SlaveUnit {
	boolean isPresent = false;
	int currentArcLevel = 0;
	int newArcLevel = 0;
}

public class DaliMasterModule {

	private DaliController myDaliController;
	private SlaveUnit[] mySlaveUnits = new SlaveUnit[64];
	private int changeLevelAddress = 0;
	private int scanAddress = 0;
	
	private Logger logger;
	private CommunicationThread commThread; 


	//This functions is periodically called from "CommunicationThread"
	void periodicCallback(){
		//System.out.println("I'm also here!");
		boolean operationExecuted = false;

		
		SlaveUnit currentSlaveUnit;
		
		//*** Check, whether any level should be changed ***
		int i = 0;
		while(i<64){
			currentSlaveUnit = mySlaveUnits[changeLevelAddress];
			if(currentSlaveUnit.newArcLevel != currentSlaveUnit.currentArcLevel){
				//System.out.println("diff with " + changeLevelAddress);
				try{
					myDaliController.connect();
					myDaliController.daliTrx(DaliCommands.idv_address_arc(changeLevelAddress), currentSlaveUnit.newArcLevel);
					currentSlaveUnit.currentArcLevel = currentSlaveUnit.newArcLevel;
					myDaliController.disconnect();
				} catch (Exception e) {
					//todo: Will produce Error here if connection does not work
					//e.printStackTrace();
					//logger.log(Level.WARNING, "connection does not work", e);
					logger.warning("Connection does not work");
				}
				operationExecuted = true;
			}
			changeLevelAddress++;
			if(changeLevelAddress >= 64) changeLevelAddress = 0;

			if(operationExecuted) return;

			i++;
		}
		
		//*** Nothing to do? -> Check whether Lamps are present and get their arc levels
		currentSlaveUnit = mySlaveUnits[scanAddress];

		//System.out.print("check on " + scanAddress +  " ...");
		try{
			myDaliController.connect();
			int ret;
			ret = myDaliController.daliTrx(DaliCommands.idv_address_cmd(scanAddress), DaliCommands.QUERY_ACTUAL_LEVEL);
			if(ret == -1){
				//System.out.println("not present!");
				if(currentSlaveUnit.isPresent){
					logger.warning("Slave with address " + scanAddress + " removed. Was present before.");
					currentSlaveUnit.isPresent = false;
				}
			}
			else {
				//System.out.println("is present, level = " + ret);
				if(!currentSlaveUnit.isPresent){
					currentSlaveUnit.isPresent = true;
					logger.info("Slave with address " + scanAddress + " now present.");
				}
				else {
					if(currentSlaveUnit.currentArcLevel != ret){
						currentSlaveUnit.currentArcLevel = ret;
						logger.warning("Slave with address " + scanAddress + " unexpected level of " + ret + ".");
					}
				}
			}
			myDaliController.disconnect();
		} catch (Exception e) {
			//todo: Will produce also Error here if connection does not work
			//e.printStackTrace();
			//logger.log(Level.WARNING, "connection does not work", e);
			logger.warning("Connection does not work");
		}

		scanAddress++;
		if(scanAddress >= 64) scanAddress = 0;

		//todo: Do everything here related to communication!
		//e.g. Scan for addresses to be present
		//Scan for level on present addresses
		//switch lamps on and off / dimming
	}

	//Constructor
	public DaliMasterModule(DaliController controller) {
		this.myDaliController = controller;
		for(int i = 0; i<64;i++){
			this.mySlaveUnits[i] = new SlaveUnit();
		}

		commThread = new CommunicationThread();
	}
	
	public void start(){
		//*** Start communication thread ***
		//Must be called only once!
		commThread.setMasterModule(this);
		(new Thread(commThread)).start();
	}
	
	public void setLogger(Logger logger){
		this.logger = logger;
	}

	public void setArcLevel(int address, int level) { //throws Exception {
		//if(address > 63 || address < 0) throw new Exception("Address range exceeded");
		//if(level > 0xff || level < 0) throw new Exception("Level range exceeded");
		if(address > 63 || address < 0) return;
		if(level > 0xff || level < 0) return;
		
		mySlaveUnits[address].newArcLevel = level;
	}

}

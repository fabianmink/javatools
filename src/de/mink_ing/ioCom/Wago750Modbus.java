package de.mink_ing.ioCom;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadInputDiscretesRequest;
import net.wimpi.modbus.msg.ReadInputDiscretesResponse;
import net.wimpi.modbus.msg.WriteMultipleCoilsRequest;
import net.wimpi.modbus.msg.WriteMultipleCoilsResponse;
import net.wimpi.modbus.net.TCPMasterConnection;


class CommunicationThread implements Runnable{

	private Wago750Modbus communicator;
	
	public void setCommunicator(Wago750Modbus communicator){
		this.communicator = communicator;
	}

	@Override
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		while(true){
			communicator.periodicCallback();
			try {
				Thread.sleep(communicator.getSampleTime_ms());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//	public CommunicationThread() throws Exception{
	//	}

	//	protected void finalize(){
	//		//Destructor
	//	}

}

public class Wago750Modbus {

	private TCPMasterConnection wago750con; //the connection
	private boolean wago750connected = false;
	private ModbusTCPTransaction wago750transInput; //the transaction
	private ReadInputDiscretesRequest wago750reqInput; //the request
	private ReadInputDiscretesResponse wago750resInput; //the response

	
	private ModbusTCPTransaction wago750transCoil; //the transaction
	private WriteMultipleCoilsRequest wago750reqCoil;  //the request
	private WriteMultipleCoilsResponse wago750resCoil; //the response
	
	private int reconnectTimer=0;
		
	private Logger logger;
	private CommunicationThread commThread; 
	
	private int sampleTime_ms = 100;
	
	private boolean inputImage[];
	private boolean outputImage[];
	
	private boolean checkConnected(){

		if(wago750connected) return(true);

		if(reconnectTimer > 0) {
			reconnectTimer--;
			return(false);
		}

		reconnectTimer = 100;

		//Try to reconnect
		//System.out.println("Try (re)connect wago750");
		logger.info("Try (re)connect wago750");
		try {
			wago750con.close();
			wago750con.connect();
			wago750connected = true;
			//System.out.println("connect ok");
			logger.info("connect ok");
			return(true);
			//wago750con.setTimeout(200);
		} catch (Exception ex) {
			//System.out.println("Could not connect wago750!");
			//logger.log(Level.INFO, "connect failed", ex);
			logger.warning("connect failed");
		}
		reconnectTimer--;
		return(false);
	}

	private void readProcessImage(){
		//*** Read input process image ***
		if(wago750connected){
			try{
				wago750transInput.execute();
				wago750resInput = (ReadInputDiscretesResponse) wago750transInput.getResponse();
				
				//int num = wago750resInput.getBitCount();  //todo: does not work, why?;
				int num = this.inputImage.length;
				
				for(int i = 0; i< num; i++) {
					this.inputImage[i] = wago750resInput.getDiscreteStatus(i);
				}
				
			}
			catch (ModbusIOException ex){
				//System.out.println("ModbusIOException while execute!\n");
				logger.log(Level.WARNING, "ModbusIOException while execute", ex);
				//ex.printStackTrace();
				wago750con.close();
				wago750connected = false;
			}
			catch (ModbusException ex){
				//System.out.println("ModbusException while execute!\n");
				logger.log(Level.WARNING, "ModbusException while execute", ex);
				wago750con.close();
				wago750connected = false;
			}
			catch (Exception ex){
				//System.out.println("ModbusException while execute!\n");
				logger.log(Level.WARNING, "Exception while execute", ex);
				wago750con.close();
				wago750connected = false;
			}
		}
	}

	private void writeProcessImage(){

		if(wago750connected){
			
			int num = wago750reqCoil.getBitCount();
			for(int i = 0; i< num; i++) {
				wago750reqCoil.setCoilStatus(i,this.outputImage[i]);
			}
			
			//wago750reqCoil.setCoils(bv);
			try {
				wago750transCoil.execute();
				wago750resCoil = (WriteMultipleCoilsResponse) wago750transCoil.getResponse();
			}
			catch (Exception ex){
				//System.out.println("Exception while execute!\n");
				logger.log(Level.WARNING, "Exception while execute", ex);
				wago750con.close();
				wago750connected = false;
			}
		}
	}


	//This functions is periodically called from "CommunicationThread"
	void periodicCallback(){
		if(checkConnected()){
			readProcessImage();
			writeProcessImage();
		}
	}
	
	int getSampleTime_ms() {
		return(this.sampleTime_ms);
	}
	
	public void start(){
		//*** Start communication thread ***
		//Must be called only once!
		commThread.setCommunicator(this);
		(new Thread(commThread)).start();
	}
	
	
	//Constructor
	public Wago750Modbus(String host) {
		commThread = new CommunicationThread();

		InetAddress wago750addr;
		try {
			wago750addr = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		} //the slave's address
		int wago750port = Modbus.DEFAULT_PORT;


		// *** Wago 750 Setup ***
		//Prepare the connection
		wago750con = new TCPMasterConnection(wago750addr);
		wago750con.setPort(wago750port);
		wago750con.setTimeout(200);
	}
	
	
	//Constructor
	public Wago750Modbus() {
		this("192.168.3.11");
	}
	
	public Wago750Modbus(Logger logger){
		this();
		this.logger = logger;
	}
	
	public Wago750Modbus(String host, Logger logger){
		this(host);
		this.logger = logger;
	}

	public void setNoOfInputs(int cnt) {
		int refInput = 0; //the reference; offset where to start reading from
		this.inputImage = new boolean[cnt];
		wago750reqInput = new ReadInputDiscretesRequest(refInput, cnt);
		wago750transInput = new ModbusTCPTransaction(wago750con);
		wago750transInput.setRequest(wago750reqInput);
	}
	
	public void setNoOfOutputs(int cnt) {
		int refCoil = 0; //the reference; offset where to start reading from
		this.outputImage = new boolean[cnt];
		wago750reqCoil = new WriteMultipleCoilsRequest(refCoil, cnt);
		wago750transCoil = new ModbusTCPTransaction(wago750con);
		wago750transCoil.setRequest(wago750reqCoil);
	}
	
	public void setSampleTime(int ms) {
		this.sampleTime_ms = ms;
	}
	
	public boolean getInputImage(int pos){
		return(this.inputImage[pos]);
	}
		
	public void setOutputImage(int pos, boolean val){
		this.outputImage[pos]= val;
	}

}

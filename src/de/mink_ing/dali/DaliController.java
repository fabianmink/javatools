package de.mink_ing.dali;

public interface DaliController {
	
	
	public void connect() throws Exception;
	public void disconnect();
	
	//public void daliSend(int address, int command) throws Exception;
	public int daliTrx(int address, int command) throws Exception;

}

package de.mink_ing.dali;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class DaliTCP implements DaliController {
	private static final int DALI_TCP_PORT = 600;

	private InetSocketAddress daliTcpAddress;
	private Socket daliTcpSocket;

	private InputStream daliTcpIs;
	private OutputStream daliTcpOs;

	public DaliTCP() throws Exception{
		this("192.168.3.89");
	}

	public DaliTCP(String host) throws Exception{
		this(InetAddress.getByName(host));
	}

	public DaliTCP(InetAddress address) throws Exception{
		this.daliTcpAddress = new InetSocketAddress(address, DALI_TCP_PORT);
	}

	protected void finalize(){
		//Destructor
		//Try to go offline
		this.disconnect();
	}

	/**
	 * Go online with TCP/IP connection.
	 */
	public void connect() throws Exception {
		this.daliTcpSocket = new Socket();


		try{
			//Exception bei Timeout (5000ms)!
			this.daliTcpSocket.connect(this.daliTcpAddress, 5000);

			//Enable/disable SO_TIMEOUT with the specified timeout, in milliseconds.
			//With this option set to a non-zero timeout, a read() call on the 
			//InputStream associated with this Socket will block for only this 
			//amount of time. If the timeout expires, a 
			//java.net.SocketTimeoutException is raised, though the Socket is still
			//valid. The option must be enabled prior to entering the blocking
			//operation to have effect. The timeout must be > 0. A timeout of zero
			//is interpreted as an infinite timeout.
			this.daliTcpSocket.setSoTimeout(5000);

			this.daliTcpIs = this.daliTcpSocket.getInputStream();
			this.daliTcpOs = this.daliTcpSocket.getOutputStream();
		} catch (Exception e){
			this.disconnect();
			throw new Exception("Going online failed!");
		}
	}

	/**
	 * Go offline with TCP/IP connection.
	 */
	public void disconnect(){
		this.daliTcpIs = null;
		this.daliTcpOs = null;

		if(this.daliTcpSocket == null) return;

		try {
			this.daliTcpSocket.close();
		} catch (IOException e) {
			// Do nothing!
		}
		this.daliTcpSocket = null;
	}


	public void daliSend(int address, int command) throws Exception {

		byte[] frame = new byte[3];

		if(address > 0xff || address < 0) throw new RuntimeException("Command range exceeded");
		if(command > 0xff || command < 0) throw new RuntimeException("Val range exceeded");

		frame[0] = 'a'; //0x61;
		frame[1] = (byte) address;
		frame[2] = (byte) command;

		this.daliTcpOs.write(frame);
		//todo: Read or discard answer
		//this.daliIs.
	}


	public void daliSendTwice(int address, int command) throws Exception {

	  byte[] frame = new byte[3];

	  if(address > 0xff || address < 0) throw new RuntimeException("Command range exceeded");
	  if(command > 0xff || command < 0) throw new RuntimeException("Val range exceeded");

	  frame[0] = 'b'; //0x62;
	  frame[1] = (byte) address;
	  frame[2] = (byte) command;

	  this.daliTcpOs.write(frame);
	  //todo: Read or discard answer
	  //this.daliIs.
	  //Should be 2x 2 chars??
	}	

	
	public int daliTrx(int address, int command) throws Exception {

		byte[] frame = new byte[3];
		byte[] retframe = new byte[2];

		if(address > 0xff || address < 0) throw new RuntimeException("Command range exceeded");
		if(command > 0xff || command < 0) throw new RuntimeException("Val range exceeded");

		frame[0] = 0x61;
		frame[1] = (byte) address;
		frame[2] = (byte) command;

		this.daliTcpOs.write(frame);
		//todo: Read or discard answer
		//this.daliIs.
		
		int len = 2;
		int off = 0;
		while (len > 0){
			int num = this.daliTcpIs.read(retframe, off, len);
			len -= num;
			off += num;
		}

		//ok
		if(retframe[0] == 'o'){
			if(retframe[1]<0) return (retframe[1]+256);
			return(retframe[1]);
		}

		//no answer
		if(retframe[0] == 'n'){
			return(-1);
		}

		//transmission still active
		//'a'; //error
		//bit error
		//'b'; //error
		//other things
		//'e'; //error
		throw new Exception("Error occured");
		
		
		//return(0);
	}
}

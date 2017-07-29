package de.mink_ing;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.StringTokenizer;

import de.mink_ing.parameter.BooleanParameter;
import de.mink_ing.parameter.IntegerParameter;
import de.mink_ing.parameter.ParameterGetter;
import de.mink_ing.parameter.ParameterList;
import de.mink_ing.parameter.ParameterSetter;


class NetworkServerCommunicationThread implements Runnable{

	public ConsoleNetworkServer myCommunicator;

	@Override
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		myCommunicator.threadCallback();
	}

}

class testExe implements IExecutable{
	public void main(String[] args, PrintStream out){
		out.println("In testExe!");
		out.println("These are the arguments:");
		for (int i = 0; i<args.length; i++){
			out.println("arg " + i + ": " + args[i]);
		}
	}
}

class help implements IExecutable{
	public void main(String[] args, PrintStream out){
		out.println("*** List of commands ***");
		out.println("Command 1");
		out.println("Command 2");
	}
}

//class get implements IExecutable{
//	public void main(String[] args, PrintStream out){
//		int paraNum = 0;
//		if(args.length < 1){
//			out.println("ERR - wrong number of arguments");
//			return;
//		}
//
//		try{ 
//			paraNum = java.lang.Integer.parseInt(args[0]);
//		}
//		catch(NumberFormatException e){
//			out.println("ERR - wrong parameter number format. Must be integer number");
//			return;
//		}
//
//		out.println("Parameter no.: " + paraNum);
//	}
//}


public class ConsoleNetworkServer {

	private Hashtable<String, IExecutable> commandTable;

	private ParameterList paraList;

	private InputStream is;
	private OutputStream os;
	private boolean closeConn = false;

	private IntegerParameter lightLevelTest;
	
	private BooleanParameter triggerLight1On;
	private BooleanParameter triggerLight1Off;
	private BooleanParameter statusLight1;

	//This functions is called from "CommunicationThread"
	void threadCallback(){
		//do something

		boolean running = true;

		while(running){
			try{
				ServerSocket srvr = new ServerSocket(1234);
				Socket skt = srvr.accept();

				System.out.println("Socket connected to" + ((InetSocketAddress)skt.getRemoteSocketAddress()).getAddress().toString());
				srvr.close();

				os = skt.getOutputStream();
				is = skt.getInputStream();

				//PrintWriter out = new PrintWriter(os, true);
				PrintStream out = new PrintStream(os);

				//StreamTokenizer st = new StreamTokenizer(new BufferedReader(is));
				StreamTokenizer st = new StreamTokenizer(is);

				st.resetSyntax();
				st.wordChars(' ', 255);
				st.whitespaceChars(0, 31);
				//st.commentChar('#');
				//st.quoteChar('"');
				//st.quoteChar('\'');

				out.println("*** Automation platform controller ***");
				out.println("Hello.");
				//out.flush();

				this.closeConn = false;
				while (this.closeConn == false) {
					out.print(">> ");

					if(st.nextToken() == StreamTokenizer.TT_EOF) {
						break;
					}

					StringTokenizer strt = new StringTokenizer(st.sval);
					String function = null;
					String[] args = new String[strt.countTokens()-1];
					int tokakt=0;
					while (strt.hasMoreTokens()) {
						String tok = strt.nextToken();
						//System.out.println(tok);
						if(tokakt == 0){
							function = tok;
						}
						else{
							args[tokakt-1] = tok;
						}
						tokakt++;
					}

					evaluate(function, args, out);
					out.flush();

					//System.out.println("cc" + this.closeConn);
				}

				out.print("...and goodbye!\n");
				out.flush();

				out.close();
				skt.close();
				//srvr.close();
			}
			catch(Exception e) {
				//TODO: logging!!
				//System.out.print("Error\n");
				//System.out.print(e.toString());
				//running = false;
			}
		} //while(running)

	} //void threadCallback()

	private void evaluate(String function, String[] args, PrintStream out){
		//out.println("Function: " + function);

		//System.out.println("cmp: " + function.compareTo("exit"));
		if (function.equals("exit")){
			this.closeConn = true;
			//out.println("goodbye!");
			return;
		}

		//todo: Go through table with function.toLowerCase() in order to find function entry
		IExecutable exeFromTable = commandTable.get(function);
		if(exeFromTable != null){
			exeFromTable.main(args, out);
		}
		else {
			out.println("ERROR - command not found");
		}

	}

	//Constructor
	public ConsoleNetworkServer() {
		NetworkServerCommunicationThread commThread = new NetworkServerCommunicationThread();

		triggerLight1On = new BooleanParameter();
		triggerLight1On.setValue(false);
		triggerLight1Off = new BooleanParameter();
		triggerLight1Off.setValue(false);
		statusLight1 = new BooleanParameter();
		statusLight1.setValue(false);
		

		lightLevelTest = new IntegerParameter();
		lightLevelTest.setValue(100);

		paraList = new ParameterList();
		paraList.add(triggerLight1On);   //0
		paraList.add(triggerLight1Off);  //1
		paraList.add(statusLight1);      //2
		paraList.add(lightLevelTest);    //3


		ParameterGetter myGetter = new ParameterGetter(paraList);
		ParameterSetter mySetter = new ParameterSetter(paraList);

		//*** Fill hashtable with functions ***
		commandTable = new Hashtable<String, IExecutable>();

		commandTable.put("test", new testExe());
		commandTable.put("help", new help());
		commandTable.put("get", myGetter);
		commandTable.put("set", mySetter);


		//*** Start communication thread ***
		commThread.myCommunicator = this;
		(new Thread(commThread)).start();
	}

	public int getLightLevelTest(){
		return(lightLevelTest.getValue());
	}

	public boolean getLight1on(){
		synchronized (triggerLight1On) {
			if(triggerLight1On.getValue() == true){
				triggerLight1On.setValue(false);
				return(true);
			}
		}
		return(false);
	}

	public boolean getLight1off(){
		synchronized (triggerLight1Off) {
			if(triggerLight1Off.getValue() == true){
				triggerLight1Off.setValue(false);
				return(true);
			}
		}
		return(false);
	}
	
	public void setLight1Status(boolean status){
		synchronized (statusLight1){
			statusLight1.setValue(status);
		}
	}

}

package de.mink_ing.mqttTools;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;  //FileNotFoundException extends IOException!
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public class MqttScope {

	public static void main(String[] args) {
		//String current = new java.io.File( "." ).getCanonicalPath();
		//System.out.println("Current dir:"+current);

		Properties myProps = new Properties();

		Reader in;
		try {
			in = new FileReader( "myTestProp.cfg" );
			myProps.load(in);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Program Exit");
			return;
		}


		for (String key : myProps.stringPropertyNames()) {
			String value = myProps.getProperty(key);
			System.out.println("The Value in " + key + " is: " + value);
		}

		String value = myProps.getProperty("xxx"); //not existing
		System.out.println("The Value in xxx is: " + value);

		int numberInt = Integer.parseInt(myProps.getProperty("val"));
		double numberDouble = Double.parseDouble(myProps.getProperty("hallo"));

		myProps.setProperty("val", "" + (numberInt+1));

		Writer out;
		try {
			out = new FileWriter( "myTestProp2.cfg" );
			myProps.store(out, "this is a test");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Program Exit");
			return;
		}
		
		for(int i = 1; i< 1000; i++) {
			long tnow = System.currentTimeMillis();
			System.out.println("t: " + tnow);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

}

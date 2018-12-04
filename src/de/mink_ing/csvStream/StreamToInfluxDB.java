package de.mink_ing.csvStream;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.OutputStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

public class StreamToInfluxDB {

	private static String host = "localhost";
	private static int port = 2134;
	
	private static String databaseURL = "http://localhost:8086";
	private static String databaseName = "mydb";
	private static String databasePointName = "csvData";
	
	private static boolean readTableHeaders = true;
	private static boolean readScalings = false;
	
	private static boolean specifyRetentionPolicy = false;
	private static String rpName = "one_week";
	
	//private static boolean readUnit = false;

	private static Socket mySocket;
	private static InputStream soIs;
	//private static OutputStream soOs;

	private static void connect() throws Exception {
		mySocket = new Socket();

		try {
			InetSocketAddress address;
			address = new InetSocketAddress(InetAddress.getByName(host), port );
			mySocket.connect(address, 1000); //Connect timeout: 1000ms

			//Enable/disable SO_TIMEOUT with the specified timeout, in milliseconds.
			//With this option set to a non-zero timeout, a read() call on the 
			//InputStream associated with this Socket will block for only this 
			//amount of time. If the timeout expires, a 
			//java.net.SocketTimeoutException is raised, though the Socket is still
			//valid. The option must be enabled prior to entering the blocking
			//operation to have effect. The timeout must be > 0. A timeout of zero
			//is interpreted as an infinite timeout.
			mySocket.setSoTimeout(5000);

			soIs = mySocket.getInputStream();
			//soOs = mySocket.getOutputStream();
		} catch (IOException e) {
			//e.printStackTrace();
			disconnect();
			throw new Exception("Failed to establish connection!");
		}

	}

	private static void disconnect() {
		soIs = null;
		//soOs = null;

		if(mySocket == null) return;

		try {
			mySocket.close();
		} catch (IOException e) {
			// Do nothing!
		}
		mySocket = null;
	}


	public static void main(String args[]) throws Exception {
		//System.out.println("hello, world.");
		//e.g. call by
		//StreamToInfluxDB 192.168.3.1 1234 true true testDb testdatapoint
				
		try {
			host = args[0];
			port = Integer.parseInt(args[1]);
			readTableHeaders = Boolean.parseBoolean(args[2]);
			readScalings = Boolean.parseBoolean(args[3]);
			
			databaseName = args[4];
			databasePointName = args[5];
		}
		catch (Exception e) {
			System.out.println("wrong / missing arguments. Using default");
		}
		
		boolean isConnected = false;
		boolean firstLine = true;
		boolean tableHeadersToRead = readTableHeaders;
		boolean scalingsToRead = readScalings;

		StreamTokenizer st = null;
		
		List<String> lineData = null;

		String headlinesArray[] = null;
		double valueArray[] = null;
		double scalingsArray[] = null;

		while(true){

			if(!isConnected) {
				try {
					System.out.println("Try connect.");
					connect();
					firstLine = true;
					tableHeadersToRead = readTableHeaders;
					scalingsToRead = readScalings;
					
					lineData = new ArrayList<String>();

					Reader r = new BufferedReader(new InputStreamReader(soIs));
					st = new StreamTokenizer(r);
					st.resetSyntax();
					st.eolIsSignificant(true);
					st.wordChars(' ', 'z');
					st.whitespaceChars(',', ',');
					isConnected = true;
					System.out.println("connect ok.");
				}
				catch (Exception e) {
					System.out.println("connect fail.");
				}
			}

			if(isConnected) {
				try {
					int token = st.nextToken();

					switch (token) {
					case StreamTokenizer.TT_EOF:
						//System.out.println("End of File encountered.");
						throw (new Exception("eol"));
						//eof = true;
						//break;

					case StreamTokenizer.TT_EOL:
						//System.out.println("End of Line encountered.");
						if(firstLine) {
							firstLine = false;
						
							//TODO: init with argument data, if requested
							headlinesArray = new String[lineData.size()];
							for(int i = 0; i < lineData.size(); i++) {
								headlinesArray[i] = "val_" + i;
							}
							
							//TODO: init with argument data, if requested
							scalingsArray = new double[lineData.size()];
							for(int i = 0; i < lineData.size(); i++) {
								scalingsArray[i] = 1.0;
							}
							
							valueArray = new double[lineData.size()];
							for(int i = 0; i < lineData.size(); i++) {
								valueArray[i] = Double.NaN;
							}
						}
						
						if(tableHeadersToRead) { 
							for(int i = 0; i < lineData.size(); i++) {
								headlinesArray[i] = lineData.get(i);
							}
							tableHeadersToRead = false;
						}
						else if (scalingsToRead){
							for(int i = 0; i < lineData.size(); i++) {
								scalingsArray[i] = Double.parseDouble(lineData.get(i));
							}
							scalingsToRead = false;
						}
						else {
							for(int i = 0; i < lineData.size(); i++) {
								valueArray[i] = scalingsArray[i] * Double.parseDouble(lineData.get(i));
							}
							writeValuesToDB(headlinesArray, valueArray);
						}
						lineData = new ArrayList<String>();
						
						break;

					case StreamTokenizer.TT_WORD:
						lineData.add(st.sval);
						break;

						//case StreamTokenizer.TT_NUMBER:
						//   System.out.println("Number: " + st.nval);
						//   break;

					default:
						//System.out.println((char) token + " encountered.");
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					disconnect();
					isConnected = false;
				}
			} //if(isConnected)
			else {
				Thread.sleep(100);
			}
		}
		//System.out.println("goodbye.");

		//System.exit(0);
	} //public static void main

	private static void writeValuesToDB(String headlines[], double valueArray[]) {
		InfluxDB influxDB = null;
		//System.out.println("connect DB");
		influxDB = InfluxDBFactory.connect(databaseURL);
		influxDB.setDatabase(databaseName);
		
		BatchPoints.Builder thisBatchPointBuilder = BatchPoints.database(databaseName);
		
		if(specifyRetentionPolicy) {
			thisBatchPointBuilder.retentionPolicy(rpName);
		}
		
		BatchPoints batchPoints = thisBatchPointBuilder.build();

		
		//		for (int i = 0; i<headlines.length; i++) {
		//			//System.out.println("to DB: " + headlines[i] + ": " + valueArray[i] + " ");
		//			Point thispoint = Point.measurement(headlines[i])
		//					//.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
		//					.addField("value", valueArray[i])
		//					.build();
		//
		//			batchPoints.point(thispoint);
		//		}

		Point.Builder thispointbuilder = Point.measurement(databasePointName);
		for (int i = 0; i<headlines.length; i++) {
			thispointbuilder.addField(headlines[i], valueArray[i]);
		}
		batchPoints.point(thispointbuilder.build());

		influxDB.write(batchPoints);

		influxDB.close();
	}

}

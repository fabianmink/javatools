package de.mink_ing.csvStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

public class StreamSimulator {

	//private static boolean do_run = true;
	//private static Thread myThread;

	public static void main(String []args) throws IOException, InterruptedException {
		System.out.println("Hello World!");

		InputStream SocketInStream = null;
		OutputStream SocketOutStream = null;

		int portNumber = 2134;
		//myThread = Thread.currentThread();

		//System.out.println(myThread.toString());
		ServerSocket serverSocket = new ServerSocket(portNumber);

		while(true) {

			try { 
				Socket clientSocket = serverSocket.accept();
				System.out.println("Hello Client!");

				SocketOutStream = clientSocket.getOutputStream();
				SocketInStream = clientSocket.getInputStream();

				//SocketOutStream.write(new String("Connected").getBytes());
				SocketOutStream.write(new String("randomData0,randomData1,randomData2\r\n").getBytes());
			} catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port "
						+ portNumber + " or listening for a connection");
				System.out.println(e.getMessage());
			}
			
						
//			byte[] buffer = new byte[1024];
//			buffer[0] = 50;
//			buffer[1] = 51;
//			buffer[2] = 13;
//			buffer[3] = 10;
			while(true){
				String outstring = "";
				for (int i = 0; i< 3; i++) {
					double rnd = (Math.random()-0.5)*65535;
					outstring = outstring + String.format(Locale.ROOT, "%+06.0f", rnd);
					if(i < 2) outstring = outstring + ",";
				}
				outstring = outstring + "\r\n";
				try {
					//SocketOutStream.write(buffer,0,4);
					SocketOutStream.write(outstring.getBytes());
				}
				catch (IOException e) {
					break;
				}
				Thread.sleep(1000);
				
				
				//int len = SocketInStream.read(buffer);
				//if (len < 0) break; //EOF
			} 
			
			System.out.println("Goodbye Client!");
		} //while(true)
		//serverSocket.close();
		//System.out.println("Goodbye World!");
	} //public static void main
}


//Runtime.getRuntime().addShutdownHook(new Thread() {
//    public void run() {
//        try {
//            Thread.sleep(200);
//            //maybe interrupt other thread
//            //myThread.interrupt();
//            do_run = false;
//            
//            System.out.println("Shutting down ...");
//            //some cleaning up code...
//
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//});

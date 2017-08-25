package de.mink_ing.csvStream;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import de.mink_ing.swingComponents.dscope.MultiChannelScope;

public class StreamPlotter {

	//private static final int WT_TCP_PORT = 5007;
	//private static final String WT_IP_ADDRESS = "192.168.39.15";
	//private static final int WT_TCP_PORT = 2134;
	//private static final String WT_IP_ADDRESS = "localhost";

	private static ScopeWindow myWindow = null;
	
	private static Socket mySocket;
	private static InputStream soIs;
	private static OutputStream soOs;

	private static void connect() throws Exception {
		mySocket = new Socket();

		try {
			InetSocketAddress wtAddress;
			wtAddress = new InetSocketAddress(InetAddress.getByName(myWindow.getHost()), java.lang.Integer.parseInt(myWindow.getPort()) );
			mySocket.connect(wtAddress, 1000); //Connect timeout: 1000ms
			
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
			soOs = mySocket.getOutputStream();
		} catch (IOException e) {
			//e.printStackTrace();
			disconnect();
			throw new Exception("Failed to establish connection!");
		}

	}

	private static void disconnect() {
		soIs = null;
		soOs = null;

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
		
		//Run window creation synchronously on event dispatching thread (EDT)
		EventQueue.invokeAndWait(new Runnable() {
			public void run() {
				try {
					myWindow = new ScopeWindow();
					myWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		});

		boolean isConnected = false;
		boolean firstRun = true;
		byte[] receivedBytes = new byte[100];
		int noRead = 0;
		double[] allVal = null;
		String myString;
		String myStringTok;
		StringTokenizer mySt;
		int nTok =0;
		
		while(true){
			if(myWindow.getCnctBtnPressed()) {
				if(!isConnected) {
					try {
						connect();
						firstRun = true;
						isConnected = true;
					}
					catch (Exception e) {
					}
				}
				else {
					disconnect();
					isConnected = false;
				}
			}
			
			if(isConnected) {
				try {
					noRead = soIs.read(receivedBytes);


					myString = new String(receivedBytes, 0, noRead);
					mySt = new StringTokenizer(myString, ",");
					//System.out.println("Read cnt: " + noRead + " String: " + myString);

					if(firstRun) {
						firstRun = false;
						nTok = 0;
						while(mySt.hasMoreTokens()) {
							nTok++;
							myStringTok = mySt.nextToken();
							System.out.println("Tok: " + myStringTok);
						}
						System.out.println("Sets: " + nTok);
						allVal = new double[nTok];
					}
					else {
						double offset = myWindow.getOffset();
						double scale = myWindow.getScale();
						String strAllVal = "";
						for(int iTok=0;iTok<nTok;iTok++) {
							myStringTok = mySt.nextToken();
							double myVal = java.lang.Double.parseDouble(myStringTok);
							allVal[iTok] = myVal * scale + offset;
							strAllVal = strAllVal + String.format(Locale.ROOT, "%f, ", allVal[iTok]);
						}
						
						//System.out.println("Werte: "+ allVal);
						
						//double[] dscopeTempAllData = new double[4];
						//dscopeTempAllData[0] = allVal[0]*0.01;
						//dscopeTempAllData[1] = allVal[1]*0.01;
						//dscopeTempAllData[2] = allVal[2]*0.01;
						//dscopeTempAllData[3] = allVal[3]*0.01;
						
						myWindow.setAllValText(strAllVal);
						
						//d-scopes
						myWindow.addData(allVal);
					}
				}
				catch (Exception e) {
					disconnect();
					isConnected = false;
				}
			} //if(isConnected)
			
			Thread.sleep(100);
			
			myWindow.setConnected(isConnected);

			if(myWindow.isTerminated()) {
				break;
			}
		}
		//System.out.println("goodbye.");

		System.exit(0);
	} //public static void main
	
	static private class ScopeWindow extends JFrame implements WindowListener {

		private static final long serialVersionUID = -4051264144624127380L;
		
		private JLabel label_host, label_port;
		private JTextField text_host, text_port;
		
		private JLabel label_scale, label_offset;
		private JTextField text_scale, text_offset;
		
		private JLabel label_all_val;
		
		private JButton cnctBtn;
		
		volatile private boolean terminate = false;
		volatile private boolean isConnected = false;
		volatile private boolean cnctBtnWasPressed = false;

		private ScopePanelMinMax dscopePanel;
		
		public ScopeWindow() {
			setTitle("csvScope");
			setBounds(100, 100, 600, 800);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			addWindowListener(this);

			JPanel myPanel = new JPanel();
			//myPanel.setBackground(Color.BLUE);
			this.getContentPane().add(myPanel, BorderLayout.NORTH);
			//BoxLayout ml = new BoxLayout(myPanel, BoxLayout.PAGE_AXIS);
			myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.PAGE_AXIS));

			label_host = new JLabel("Host:");
			myPanel.add(label_host);

			text_host = new JTextField("localhost");
			text_host.setMaximumSize(new Dimension(100,20));
			myPanel.add(text_host);
			//parseRefTemp(); // Parse temperature one time to have the initial value correct

			label_port = new JLabel("Port:");
			myPanel.add(label_port);

			text_port = new JTextField("2134");
			text_port.setMaximumSize(new Dimension(100,20));
			myPanel.add(text_port);
			
			label_scale = new JLabel("Scale:");
			myPanel.add(label_scale);

			text_scale = new JTextField("1.0");
			text_scale.setMaximumSize(new Dimension(100,20));
			myPanel.add(text_scale);
			//parseRefTemp(); // Parse temperature one time to have the initial value correct

			label_offset = new JLabel("Offset:");
			myPanel.add(label_offset);

			text_offset = new JTextField("0.0");
			text_offset.setMaximumSize(new Dimension(100,20));
			myPanel.add(text_offset);
			
			cnctBtn = new JButton("Connect");
			ActionListener onCnctClick = new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					// System.out.println("EDT: " +
					// javax.swing.SwingUtilities.isEventDispatchThread());
					// System.out.printf("Conn.");
					cnctBtnWasPressed = true;
				}
			};
			cnctBtn.addActionListener(onCnctClick);
			myPanel.add(cnctBtn);

			label_all_val = new JLabel("***");
			myPanel.add(label_all_val);
			
			JPanel allScopesPanel = new JPanel();
			allScopesPanel.setLayout(new BoxLayout(allScopesPanel, BoxLayout.PAGE_AXIS));
					
			dscopePanel = new ScopePanelMinMax(10,20,40);
			allScopesPanel.add(dscopePanel);
			
			this.getContentPane().add(allScopesPanel, BorderLayout.CENTER);
			this.getContentPane().add(new JPanel(), BorderLayout.WEST);
			this.getContentPane().add(new JPanel(), BorderLayout.EAST);
		
			int delay = 100; // milliseconds
			ActionListener taskPerformer = new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					// System.out.println("EDT: " +
					// javax.swing.SwingUtilities.isEventDispatchThread());
					String myString;
					if (isConnected)
						myString = "Disconnect";
					else
						myString = "Connect";
					cnctBtn.setText(myString);
					
					//For GUI Testing
					//tankTemp = Math.random()*20+20;
					//dscopeTempAllData[0] = refTemp;
					//dscopeTempAllData[1] = tankTemp;
					//dscopeTempAllData[2] = ref_rueckTemp;
					//dscopeTempAllData[3] = rueckTemp;
					//dscopeTempPanel.addData(dscopeTempAllData); 
					
					//Repaint Scopes
					dscopePanel.repaint();
				}
			};
			new Timer(delay, taskPerformer).start();
		}
		
		public void addData(double data[]) {
			dscopePanel.addData(data);
		}
		

		public String getHost() {
			return (text_host.getText());
		}

		public String getPort() {
			return (text_port.getText());
		}
		
		public double getScale() {
			return (java.lang.Double.parseDouble(text_scale.getText()));
		}

		public double getOffset() {
			return (java.lang.Double.parseDouble(text_offset.getText()));
		}
		
		
		public void setAllValText(String text) {
			label_all_val.setText(text);
		}
		
		public boolean isTerminated() {
			return this.terminate;
		}

		public boolean getCnctBtnPressed() {
			boolean ret = this.cnctBtnWasPressed;
			this.cnctBtnWasPressed = false;
			return (ret);
		}

		public void setConnected(boolean val) {
			this.isConnected = val;
		}

		@Override
		public void windowActivated(WindowEvent arg0) {
		}

		@Override
		public void windowClosed(WindowEvent e) {
		}

		@Override
		public void windowClosing(WindowEvent e) {
			// this.setVisible(false);
			this.terminate = true;
			this.dispose();
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowOpened(WindowEvent e) {
		}
		
		private class ScopePanelMinMax extends JPanel{

			private static final long serialVersionUID = -600524620234113234L;
			//private SimpleScope procTempScope;
			private MultiChannelScope procTempScope;
			private JTextField maxField;
			private JTextField minField;
			private double minval, maxval;


			//todo: Maybe program repaint Method to repaint procTempScope!

			private ScopePanelMinMax(int noOfChannels, double min, double max) {
				this.setLayout(new BorderLayout(5,5));
				this.minval = min;
				this.maxval = max;

				maxField = new JTextField(""+max);
				minField = new JTextField(""+min);

				ActionListener onValChng = new ActionListener() {
					public void actionPerformed(ActionEvent evt) {

						double valmin,valmax;
						try {
							valmin = java.lang.Double.parseDouble(minField.getText());
						}catch (Exception e) {
							minField.setBackground(Color.RED);
							return;
						}
						minField.setBackground(Color.WHITE);
						try {
							valmax = java.lang.Double.parseDouble(maxField.getText());
						}catch (Exception e) {
							maxField.setBackground(Color.RED);
							return;
						}
						maxField.setBackground(Color.WHITE);

						System.out.printf("Val change!: " + valmin + " " + valmax);
						procTempScope.setMinMax(valmin, valmax);
					}
				};

				maxField.setPreferredSize(new Dimension(50,20));
				maxField.addActionListener(onValChng);
				JPanel maxPanel = new JPanel(); 
				maxPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
				maxPanel.add(maxField);

				minField.setPreferredSize(new Dimension(50,20));
				minField.addActionListener(onValChng);
				JPanel minPanel = new JPanel(); 
				minPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
				minPanel.add(minField);

				//procTempScope = new SimpleScope();
				procTempScope = new MultiChannelScope(noOfChannels);
				procTempScope.setPreferredSize(new Dimension(200, 100));
				procTempScope.setMinMax(minval, maxval);

				this.add(procTempScope, BorderLayout.CENTER);
				this.add(maxPanel, BorderLayout.NORTH);
				this.add(minPanel, BorderLayout.SOUTH);
			}

			//public void addData(double val) {
			//	procTempScope.addData(val);
			//}

			public void addData(double[] val) {
				procTempScope.addData(val);
			}

		} //private class ScopePanelMinMax

	}

}

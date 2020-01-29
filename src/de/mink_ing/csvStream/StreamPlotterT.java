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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
import de.mink_ing.swingComponents.dscope.MultiChannelXTScope;

public class StreamPlotterT {

	private static boolean readTableHeaders = true;
	private static boolean readScalings = false;

	private static ScopeWindow myWindow = null;

	private static Socket mySocket;
	private static InputStream soIs;
	//private static OutputStream soOs;

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
		boolean firstLine = true;
		boolean tableHeadersToRead = readTableHeaders;
		boolean scalingsToRead = readScalings;

		StreamTokenizer st = null;

		List<String> lineData = null;

		String headlinesArray[] = null;
		double valueArray[] = null;
		double scalingsArray[] = null;

		while(true){
			if(myWindow.getCnctBtnPressed()) {
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
				else {
					disconnect();
					isConnected = false;
				}
			}

			if(isConnected) {
				try {
					int token = st.nextToken();

					switch (token) {
					case StreamTokenizer.TT_EOF:
						//System.out.println("End of File encountered.");
						throw (new Exception("eof"));
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
							
							String hl_text = "- ";
							for(int i = 0; i < lineData.size(); i++) {
								hl_text += headlinesArray[i] + " - ";
							}
							myWindow.setAllValText(hl_text);
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
							myWindow.addData(valueArray);
						}
						lineData = new ArrayList<String>();

						break;

					case StreamTokenizer.TT_WORD:
						lineData.add(st.sval);
						//System.out.println("added: " + st.sval);
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

			//Thread.sleep(100);

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

			dscopePanel = new ScopePanelMinMax(10,-2,2);
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
			long tnow = System.currentTimeMillis();
			for(int i = 0; i<data.length; i++) {
				dscopePanel.addData(i, data[i], tnow);
			}
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
			private MultiChannelXTScope procTempScope;
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
				procTempScope = new MultiChannelXTScope(noOfChannels);
				procTempScope.setPreferredSize(new Dimension(200, 100));
				procTempScope.setMinMax(minval, maxval);

				this.add(procTempScope, BorderLayout.CENTER);
				this.add(maxPanel, BorderLayout.NORTH);
				this.add(minPanel, BorderLayout.SOUTH);
			}

			//public void addData(double val) {
			//	procTempScope.addData(val);
			//}

			public void addData(int channel, double data, long timestamp) {
				procTempScope.addData(channel, data, timestamp);
			}

		} //private class ScopePanelMinMax

	}

}

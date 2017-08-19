package de.mink_ing.swingComponents.demo;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.BoxLayout;
import javax.swing.Timer;

import java.awt.GridLayout;

import javax.swing.JList;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.mink_ing.swingComponents.JIndustrialButton;
import de.mink_ing.swingComponents.JLed;

import javax.swing.JLabel;

import de.mink_ing.swingComponents.OnOffPanel;
import de.mink_ing.swingComponents.dscope.SimpleScope;
import de.mink_ing.swingComponents.dscope.SimpleXYScope;
import de.mink_ing.swingComponents.JPoti;

public class SwingComponentsTest implements ActionListener {

	private JFrame frame;
	private final JButton btnTes = new JButton("Tes1");
	private SimpleXYScope scope2;
	private int phase = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingComponentsTest window = new SwingComponentsTest();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SwingComponentsTest() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Swing Components Test");
		frame.setBounds(100, 100, 647, 423);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel_5 = new JPanel();
		//panel_5.setBackground(Color.CYAN);
		frame.getContentPane().add(panel_5);
		panel_5.setLayout(new BorderLayout(10, 10));
		
		JPanel panel_7 = new JPanel();
		panel_5.add(panel_7, BorderLayout.NORTH);
		//panel_7.setBackground(Color.BLUE);
		panel_7.setLayout(new BorderLayout(0, 0));
		
		JLed led_2 = new JLed();
		panel_7.add(led_2, BorderLayout.WEST);
		
		JLed led_3 = new JLed();
		panel_7.add(led_3, BorderLayout.EAST);
		
		JPanel panel_10 = new JPanel();
		//panel_10.setBackground(Color.BLUE);
		panel_5.add(panel_10, BorderLayout.EAST);
		
		JPanel panel_8 = new JPanel();
		//panel_8.setBackground(Color.BLUE);
		panel_5.add(panel_8, BorderLayout.SOUTH);
		panel_8.setLayout(new BorderLayout(0, 0));
		
		JLed led_4 = new JLed();
		panel_8.add(led_4, BorderLayout.WEST);
		
		JLed led_5 = new JLed();
		panel_8.add(led_5, BorderLayout.EAST);
		
		JPanel panel_9 = new JPanel();
		panel_5.add(panel_9, BorderLayout.WEST);
		
		
		
		//From here
		JPanel onOffPanel = new JPanel();
		onOffPanel.setLayout(new BoxLayout(onOffPanel, BoxLayout.Y_AXIS));
		
		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setBackground(Color.GRAY);
		onOffPanel.add(descriptionPanel);
		
		JLabel descriptionLabel = new JLabel("Lamp 1");
		descriptionPanel.add(descriptionLabel);
		
		JPanel switchPanel = new JPanel();
		//switchPanel.setBackground(Color.YELLOW);
		onOffPanel.add(switchPanel);
		
		JLed onOffLed = new JLed();
		onOffLed.setEnabled(false);
		switchPanel.add(onOffLed);
				
		JIndustrialButton onButton = new JIndustrialButton(1);
		//onButton.setBackground(Color.YELLOW);
		switchPanel.add(onButton);
		onButton.setPreferredSize(new Dimension(50, 50));
		
		JIndustrialButton offButton = new JIndustrialButton(2);
		switchPanel.add(offButton);
		offButton.setPreferredSize(new Dimension(50, 50));
		//to here
			
		
		panel_5.add(onOffPanel, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLed led = new JLed();
		panel_1.add(led);
		
		JIndustrialButton industrialButtonOn = new JIndustrialButton();
		industrialButtonOn.setForeground(Color.BLUE);
		industrialButtonOn.setPreferredSize(new Dimension(50, 50));
		panel_1.add(industrialButtonOn);
		
		JIndustrialButton industrialButtonOff = new JIndustrialButton();
		industrialButtonOff.setPreferredSize(new Dimension(50, 50));
		panel_1.add(industrialButtonOff);
		
		JPoti poti = new JPoti();
		industrialButtonOff.setPreferredSize(new Dimension(50, 50));
		panel_1.add(poti);
		
		OnOffPanel onOffPanel_1 = new OnOffPanel();
		frame.getContentPane().add(onOffPanel_1);
		
		OnOffPanel onOffPanel_2 = new OnOffPanel();
		frame.getContentPane().add(onOffPanel_2);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(new GridLayout(2, 2, 10, 10));
		
		JCheckBox checkBox = new JCheckBox("Check1");
		panel.add(checkBox);
		panel.add(btnTes);
		
		JCheckBox chckbxCheck = new JCheckBox("Check1");
		panel.add(chckbxCheck);
		chckbxCheck.setSelected(true);
		
		JList list = new JList();
		panel.add(list);
		
		JPanel panel_2 = new JPanel();
		frame.getContentPane().add(panel_2);
		
		SimpleScope scope1 = new SimpleScope();
		scope1.setPreferredSize(new Dimension(200, 100));
		for(int i=0; i<1000; i++){
			scope1.addData(0.5*Math.cos(2*Math.PI * 5 * i/1000));
		}
		panel_2.add(scope1);
		
		scope2 = new SimpleXYScope(100);
		scope2.setPreferredSize(new Dimension(300, 300));
		for(int i=0; i<1000; i++){
			phase++;
			scope2.addData(0.5*Math.cos(2*Math.PI * 5 * phase/1000)+0.1*Math.random(), 0.5*Math.sin(2*Math.PI * 5 * phase/1000)+0.1*Math.random());
		}
		panel_2.add(scope2);
		
		Timer timer = new Timer(10, this);
		timer.setInitialDelay(1000);
		timer.start();
		
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		phase++;
		scope2.addData(0.6*Math.cos(2*Math.PI * 5 * phase/1000)+0.1*Math.random(), 0.3*Math.sin(2*Math.PI * 5 * phase/1000)+0.1*Math.random());
		scope2.repaint();
	}
}

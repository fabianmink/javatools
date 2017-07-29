package de.mink_ing.swingComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class OnOffPanel extends JPanel {

	private static final long serialVersionUID = -8992966689909179622L;
	private JLabel descriptionLabel;
	private JLed onOffLed;
	
	private boolean on_pressed = false;
	private boolean off_pressed = false;
	private boolean led_on = false;
		
	
	public void setText(String text){
		descriptionLabel.setText(text);
	}
	
	public OnOffPanel(){
		super();

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setBackground(Color.GRAY);
		this.add(descriptionPanel);

		descriptionLabel = new JLabel(" ");
		descriptionPanel.add(descriptionLabel);

		JPanel switchPanel = new JPanel();
		//switchPanel.setBackground(Color.YELLOW);
		this.add(switchPanel);

		onOffLed = new JLed();
		onOffLed.setEnabled(false);
		switchPanel.add(onOffLed);

		JIndustrialButton onButton = new JIndustrialButton(1);
		//onButton.setBackground(Color.YELLOW);
		switchPanel.add(onButton);
		onButton.setPreferredSize(new Dimension(50, 50));
		onButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized (this) {
					off_pressed = false;
					on_pressed = true;
				}
			}
		});

		JIndustrialButton offButton = new JIndustrialButton(2);
		switchPanel.add(offButton);
		offButton.setPreferredSize(new Dimension(50, 50));
		offButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized (this) {
					on_pressed = false;
					off_pressed = true;
				}
			}
		});
		
		Timer myTimer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized (this) {
					onOffLed.setEnabled(led_on);
				}
			}
		});
		myTimer.setInitialDelay(1000);
		myTimer.start();
		
	}
	
	public OnOffPanel(String text){
		this();
		this.setText(text);
	}
	
	
	public void setLed(boolean enabled){
		synchronized (this){
			this.led_on = enabled;
		}
		//only to be called from EDT!! -> Thus moved to actionPerformed of myTimer
		//onOffLed.setEnabled(enabled);
	}
	
	public boolean getOnPressed(){
		boolean ret = false;
		synchronized (this) {
			ret = this.on_pressed;
			this.on_pressed = false;
		}
		return ret;
	}
	
	public boolean getOffPressed(){
		boolean ret = false;
		synchronized (this) {
			ret = this.off_pressed;
			this.off_pressed = false;
		}
		return ret;
	}
}

package de.mink_ing.automation.blocks;


import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

//TODO: implement abstract class Zigbee2Mqtt
public class Zigbee2Mqtt_TradfriLed1738G7 extends DynamicBlock implements ITextualStateBlock {
	
	private boolean state = false;
	private boolean state_change = true; //always report state change for first run
	
	private boolean in;

	public Zigbee2Mqtt_TradfriLed1738G7(int Ts) {
		super(Ts);
	}

	public void oneStep(){
		if(in != state) {
			state = in;
			state_change = true;
		}
	}
	
	public void setInput(boolean in){
		this.in = in;
	}

	public String getStateAsString() {
		if(state) {
			return("ON");
		}
		else {
			return("OFF");
		}
	}

	public boolean isStateChanged() {
		boolean sc = state_change;
		if(state_change) {
			state_change = false;
		}
		return(sc);
	}
	
	public String getTopicExtensionAsString() {
		return("/set");
	}

}

package de.mink_ing.automation.blocks;


import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

//TODO: implement abstract class Zigbee2Mqtt
public class Zigbee2Mqtt_TradfriLed1738G7 extends DynamicBlock implements ITextualStateBlock {
	
	private boolean state = false;
	private boolean state_change = true; //always report state change for first run
	
	private boolean in;
	
	private int cnt = 0;
	private static final int cntPubChange = 10000; //publish a dummy change all 10000 calls

	public Zigbee2Mqtt_TradfriLed1738G7(int Ts) {
		super(Ts);
	}

	public void oneStep(){
		cnt++;
		if(in != state) {
			state = in;
			state_change = true;
			cnt = 0;
		}
		if(cnt >= cntPubChange) {
			state_change = true;
			cnt = 0;
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

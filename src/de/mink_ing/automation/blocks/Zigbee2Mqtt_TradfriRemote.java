package de.mink_ing.automation.blocks;


import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

//TODO: implement abstract class Zigbee2Mqtt
public class Zigbee2Mqtt_TradfriRemote extends DynamicBlock implements ITextualCmdBlock {

	
	private boolean toggle_rcvd = false, hold_rcvd = false, right_rcvd = false, left_rcvd=false, up_rcvd=false, dn_rcvd=false;
	private boolean toggle = false, hold=false, right = false, left = false, up = false, dn = false;
	
	private int battery = -1; //not received
	private int link = -1; //not received
	
	public Zigbee2Mqtt_TradfriRemote(int Ts) {
		super(Ts);
	}

	public void oneStep(){
		
		toggle = false;
		hold = false;
		right = false;
		left = false;
		up = false;
		dn = false;
		
		if(toggle_rcvd) {
			toggle_rcvd = false;
			toggle = true;
		}
		if(hold_rcvd) {
			hold_rcvd = false;
			hold = true;
		}
		if(right_rcvd) {
			right_rcvd = false;
			right = true;
		}
		if(left_rcvd) {
			left_rcvd = false;
			left = true;
		}
		if(up_rcvd) {
			up_rcvd = false;
			up = true;
		}
		if(dn_rcvd) {
			dn_rcvd = false;
			dn = true;
		}
	}

	public int getBatteryLevel(){
		return battery;
	}
	
	public int getLinkQuality(){
		return link;
	}

	public boolean getOutputToggle(){
		return(toggle);
	}
	
	public boolean getOutputHold(){
		return(hold);
	}

	public boolean getOutputRight(){
		return(right);
	}
	
	public boolean getOutputLeft(){
		return(left);
	}
	
	public boolean getOutputUp(){
		return(up);
	}
	
	public boolean getOutputDn(){
		return(dn);
	}


	public void cmdAsString(String cmd) {
		System.out.println("check: " + cmd);
		//Example Commands: 
		//{"action":"toggle","battery":16,"linkquality":47}
		//{"action":"arrow_right_click","battery":16,"linkquality":44}
		try {

			Object o1 = JSONValue.parse(cmd);
			JSONObject jsonObj = (JSONObject) o1;

			String action = (String) jsonObj.get("action");
			Long battery = (Long) jsonObj.get("battery");
			Long link = (Long) jsonObj.get("linkquality");
			//double xx = (Double) jsonObj.get("xx");

			if(battery != null) {
				this.battery = battery.intValue();
			}
			
			if(link != null) {
				this.link = link.intValue();
			}


			if(action == null) {
				//do nothing
			}
			else{
				switch (action) {
				case "toggle":
					toggle_rcvd = true;
					break;
				case "arrow_right_click":
					right_rcvd = true;
					break;
				case "arrow_left_click":
					left_rcvd = true;
					break;
				case "brightness_up_click":
					up_rcvd = true;
					break;
				case "brightness_down_click":
					dn_rcvd = true;
					break;
				default:
					break;
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
